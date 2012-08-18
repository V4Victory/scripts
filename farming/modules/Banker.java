package scripts.farming.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.farming.Patches;
import scripts.farming.Equipment;
import scripts.farming.FarmingProject;
import scripts.farming.Location;
import scripts.farming.Patch;
import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.Module;
import scripts.state.SharedModule;
import scripts.state.State;
import scripts.state.StateCreator;
import scripts.state.Value;
import scripts.state.edge.Edge;
import scripts.state.edge.Notification;
import scripts.state.edge.Option;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.tools.OptionSelector;

public class Banker extends SharedModule {
	// id -> amount
	public List<Requirement> getRequirements(FarmingProject main) {
		List<Requirement> items = new ArrayList<Requirement>();
		List<Module> modules = new ArrayList<Module>();
		modules.add(main.MODULE_RUN_SCRIPT);
		modules.add(this);
		for (Location location : Location.locations) {
			if (location.activated) {
				modules.add(location.getModule());
				modules.add(location.selectedTeleportOption);
			}
		}
		for (Module m : modules) {
			for (Requirement r : m.getRequirements()) {
				Requirement and_req;
				do {
					and_req = r.and_req;
					r.and_req = null;
					items.add(r);
				} while ((r = and_req) != null);
			}
		}

		return items;
	}

	public enum Method {
		IDLE, DEPOSIT, WITHDRAW
	};

	public Banker(final FarmingProject main, State initial, State success,
			final State critical) {
		super("Banker", initial, success, critical);

		State AT_BANK = new State("BANK");
		Option<Module> chooseTeleport = new Option<Module>(Condition.TRUE,
				new OptionSelector<Module>() {
					public Module select() {
						return (Module) Location.getLocation("Bank").selectedTeleportOption;
					}
				});
		initial.add(new Edge(new Condition() {
			public boolean validate() {
				return Bank.open();
			}
		}, AT_BANK));
		initial.add(chooseTeleport);
		initial.add(new Timeout(critical, 15000));

		for (Module module : Location.getLocation("Bank").getTeleportOptions()) {
			chooseTeleport.add(module, module.getInitialState());
			module.getSuccessState().add(new Edge(Condition.TRUE, AT_BANK));
			module.getCriticalState().add(new Edge(Condition.TRUE, critical));
		}
		Location.getLocation("Bank").setModule(this);

		State BANKING_FINISHED = new State("BANKF");
		State BANKING_FINISHED_WITHDRAW = new State("BANKFWD");
		State BANK_OPEN = new State("BANKO");
		State DEPOSIT = new State("BANKD");

		State WITHDRAW = new ConsecutiveState<Requirement>(
				new Value<List<Requirement>>() {
					public List<Requirement> get() {
						return getRequirements(main);
					}
				}, BANKING_FINISHED_WITHDRAW, new StateCreator<Requirement>() {
					public State getState(Requirement value, State nextState) {
						State state = new State(value.toString());
						int i = 0;
						do {
							final Integer id = value.id.get();
							final Integer amount = value.amount;
							if (id > 0) {
								i++;
								final Requirement or_req = value.or_req;
								final Requirement val = value;
								state.add(new Edge(new Condition() {
									public boolean validate() {
										return Inventory.getCount(id) > (amount == 0 ? 0
												: amount - 1)
												|| (or_req == null && Bank
														.getItem(id) == null);
									}
								}, nextState));
								state.add(new Task(new Condition() {
									public boolean validate() {
										return Bank.getItem(id) != null;
									}
								}, state) {
									public void run() {
										Bank.withdraw(id, amount);
										Time.sleep(300);
									}
								});
								state.add(new Notification(new Condition() {
									public boolean validate() {
										return !val.optional && or_req == null;
									}
								}, critical, "Having #" + id + " is mandatory"));
							}
						} while ((value = value.or_req) != null);

						if (i == 0)
							state.add(new Edge(Condition.TRUE, nextState));
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
				return Banker.this.intermediateValue == Method.DEPOSIT;
			}
		}, DEPOSIT));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.intermediateValue == Method.WITHDRAW;
			}
		}, WITHDRAW));

		BANK_OPEN.add(new Edge(new Condition() {
			public boolean validate() {
				return Banker.this.intermediateValue == Method.IDLE;
			}
		}, BANKING_FINISHED));

		DEPOSIT.add(new Edge(new Condition() {
			public boolean validate() {
				return Inventory.getCount() == 0;
			}
		}, BANKING_FINISHED));

		DEPOSIT.add(new Task(Condition.TRUE, DEPOSIT) {
			public void run() {
				if (Equipment.WEAPON.getEquipped() > 0)
					Bank.depositEquipment();
				if (Inventory.getCount() > 0)
					Bank.depositInventory();
			}
		});

		BANKING_FINISHED.add(new Task(Condition.TRUE, success) {
			public void run() {
				// Bank.close();
			}
		});

		BANKING_FINISHED_WITHDRAW.add(new Task(Condition.TRUE, success) {
			public void run() {
				boolean change = false;
				for (Patch patch : Patches.patches.values()) {
					if (!patch.getRequirement().validate()) {
						System.out.println(patch + " deactivated");
						patch.activated = false;
						change = true;
					}
				}
				if (change)
					FarmingProject.gui.saveSettings();
				Bank.close();
			}
		});

	}
}
