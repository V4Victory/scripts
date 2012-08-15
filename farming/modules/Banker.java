package scripts.farming.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;

import scripts.farming.DJHarvest;
import scripts.farming.Location;
import state.Condition;
import state.ConsecutiveState;
import state.Module;
import state.SharedModule;
import state.State;
import state.StateCreator;
import state.Value;
import state.edge.Edge;
import state.edge.Option;
import state.edge.Task;
import state.edge.Timeout;
import state.tools.OptionSelector;

public class Banker extends SharedModule {
	// id -> amount
	public Map<Integer, Integer> getRequirements(DJHarvest main) {
		Map<Integer, Integer> items = new HashMap<Integer, Integer>();
		List<Module> modules = new ArrayList<Module>();
		modules.add(main.MODULE_RUN_SCRIPT);
		for (Location location : Location.locations) {
			if (location.activated) {
				modules.add(location.getModule());
				modules.add(location.selectedTeleportOption);
			}
		}
		return items;
	}

	public enum Method {
		IDLE, DEPOSIT, WITHDRAW
	};

	public Method method = Method.IDLE;

	public Banker(final DJHarvest main, State initial, State success,
			State critical) {
		super("Banker", initial, success, critical);

		State AT_BANK = new State("BANK");
		Option<Module> chooseTeleport = new Option<Module>(Condition.TRUE,
				new OptionSelector<Module>() {
					public Module select() {
						return (Module) Location.getLocation("Bank").selectedTeleportOption;
					}
				});
		initial.add(chooseTeleport);
		initial.add(new Timeout(critical, 15000));

		for (Module module : Location.getLocation("Bank").getTeleportOptions()) {
			chooseTeleport.add(module, module.getInitialState());
			module.getSuccessState().add(new Edge(Condition.TRUE, AT_BANK));
			module.getCriticalState().add(new Edge(Condition.TRUE, critical));
		}
		Location.getLocation("Bank").setModule(this);

		State BANKING_FINISHED = new State("BANKF");
		State BANK_OPEN = new State("BANKO");
		State DEPOSIT = new State("BANKD");

		State WITHDRAW = new ConsecutiveState<Entry<Integer, Integer>>(
				new Value<Set<Entry<Integer, Integer>>>() {
					public Set<Entry<Integer, Integer>> get() {
						return getRequirements(main).entrySet();
					}
				}, BANKING_FINISHED,
				new StateCreator<Entry<Integer, Integer>>() {
					public State getState(Entry<Integer, Integer> value,
							State nextState) {
						final Integer id = value.getKey();
						final Integer amount = value.getValue();
						if (id == 0)
							return nextState;
						State state = new State();
						state.add(new Edge(new Condition() {
							public boolean validate() {
								return Inventory.getCount() > (amount == 0 ? 0
										: amount - 1);
							}
						}, nextState));
						state.add(new Task(Condition.TRUE, state) {
							public void run() {
								Bank.setWithdrawNoted(amount == 0);
								Bank.withdraw(id, amount);
							}
						});
						return state;
					}
				});

		AT_BANK.add(new Edge(new Condition() {
			public boolean validate() {
				return Bank.open();
			}
		}, BANK_OPEN));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.method == Method.DEPOSIT;
			}
		}, DEPOSIT));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.method == Method.WITHDRAW;
			}
		}, WITHDRAW));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.method == Method.IDLE;
			}
		}, BANKING_FINISHED));

		DEPOSIT.add(new Edge(new Condition() {
			public boolean validate() {
				return Inventory.getCount() == 0;
			}
		}, BANKING_FINISHED));

		DEPOSIT.add(new Task(Condition.TRUE, DEPOSIT) {
			public void run() {
				Bank.depositInventory();
			}
		});

		BANKING_FINISHED.add(new Task(Condition.TRUE, success) {
			public void run() {
				Bank.close();
			}
		});

	}
}
