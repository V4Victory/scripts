package scripts.farming;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.util.Map.Entry;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.event.listener.PaintListener;

import scripts.farming.modules.Banker;
import scripts.farming.modules.DoPatches;
import scripts.farming.modules.RunOtherScriptv2;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.StateStrategy;
import scripts.state.edge.Edge;
import scripts.state.edge.Option;
import scripts.state.edge.Timeout;
import scripts.state.tools.OptionSelector;

@Manifest(name = "FarmingProject", authors = "djabby", version = 1.00, description = "does farming")
public class FarmingProject extends ActiveScript implements PaintListener {

	Timer timer = new Timer(0);

	public static GUI gui;

	State INITIAL;
	State CRITICAL_FAIL;

	State ON_CHOOSE_LOCATION;
	State LOAD_GUI;
	State BANK_INIT_DEPOSIT;
	State BANK_INIT_WITHDRAW;
	State LOAD_ACTIVE_SCRIPT;

	public ScriptLoader loader;
	public Banker banker;
	public RunOtherScriptv2 MODULE_RUN_SCRIPT;

	protected void setup() {
		try {
			System.out.println("Initialize...");

			loader = new ScriptLoader();

			INITIAL = new State("I");
			CRITICAL_FAIL = new State("C");
			ON_CHOOSE_LOCATION = new State("O");
			LOAD_GUI = new State("G");
			LOAD_ACTIVE_SCRIPT = new State("LAS");
			BANK_INIT_DEPOSIT = new State("BID");
			BANK_INIT_WITHDRAW = new State("BIW");
			banker = new Banker(this, new State("BI"), new State("BS"),
					CRITICAL_FAIL);

			provide(new StateStrategy(LOAD_GUI, Condition.TRUE));
			provide(new Antiban());

			INITIAL.add(new Edge(new Condition() {
				public boolean validate() {
					System.out.println("Total work = "
							+ Patches.countAllWork(true));
					boolean b = false;
					for (Location location : Location.locations) {
						if (!location.isBank() && location.activated
								&& location.checkRequirements())
							b = true;
					}
					return Patches.countAllWork(true) > 0 && b;
				}
			}, ON_CHOOSE_LOCATION));

			Option<Location> chooseLocation = new Option<Location>(
					Condition.TRUE, new OptionSelector<Location>() {
						public Location select() {
							System.out.println("Select location");
							Location loc = null;
							Integer mostWorkCount = 0;
							// select the location with the most work to do
							for (Location location : Location.locations) {
								if (location.isBank())
									continue;
								if (location.area.contains(Players.getLocal())
										&& location.countWork(true) > 0) {
									loc = location;
									break;
								}

								if (location.activated
										&& mostWorkCount < location
												.countWork(true)) {
									mostWorkCount = location.countWork(true);
									loc = location;
								}
							}
							System.out.println("Let's go to " + loc.name);
							return loc;
						}
					});
			System.out.println("Setup locations...");
			ON_CHOOSE_LOCATION.add(chooseLocation);
			ON_CHOOSE_LOCATION.add(new Timeout(INITIAL, 2000));
			// add all locations to the options
			for (final Location location : Location.locations) {
				if (!location.isBank()) {
					State state = new State("LOC");
					State reached = new State("LOCR");
					chooseLocation.add(location, state);
					Option<Module> chooseTeleport = new Option<Module>(
							Condition.TRUE, new OptionSelector<Module>() {
								public Module select() {
									return (Module) location.selectedTeleportOption;
								}
							});
					state.add(chooseTeleport);
					state.add(new Timeout(INITIAL, 2000));

					for (Module module : location.getTeleportOptions()) {
						chooseTeleport.add(module, module.getInitialState());
						module.getSuccessState().add(
								new Edge(Condition.TRUE, reached));
						module.getCriticalState().add(
								new Edge(Condition.TRUE, CRITICAL_FAIL));
					}
					location.setModule(new DoPatches(location, reached,
							INITIAL, CRITICAL_FAIL));
				}

			}

			Condition scriptStartCondition = new Condition() {
				public boolean validate() {
					return Patches.countAllWork(false) == 0
							&& gui.scriptsEnabled;
				}
			};

			LOAD_GUI.add(new Edge(new Condition() {
				public boolean validate() {
					return gui.isDone();
				}
			}, LOAD_ACTIVE_SCRIPT));

			LOAD_ACTIVE_SCRIPT.add(new scripts.state.edge.Task(new Condition() {
				public boolean validate() {
					return gui.miscSettings.setupScript;
				}
			}, INITIAL) {
				public void run() {
					try {
						gui.activeScript = RunOtherScriptv2.initiateScript(
								FarmingProject.this, gui.getSelectedScript());
					} catch (Exception e1) {
						gui.activeScript = null;
						e1.printStackTrace();
						System.out.println("Descr:" + e1.getMessage());
					}

				}
			});
			LOAD_ACTIVE_SCRIPT.add(new Edge(Condition.TRUE, INITIAL));

			/**
			 * When there is no location that we can visit with our current
			 * items, go to bank
			 **/
			INITIAL.add(new Edge(scriptStartCondition.negate(),
					BANK_INIT_DEPOSIT));

			banker.addSharedStates(BANK_INIT_DEPOSIT, BANK_INIT_WITHDRAW,
					Banker.Method.DEPOSIT, Banker.Method.IDLE);
			banker.addSharedStates(BANK_INIT_WITHDRAW, INITIAL,
					Banker.Method.WITHDRAW, Banker.Method.IDLE);

			System.out.println("Setup alternative script...");

			MODULE_RUN_SCRIPT = new RunOtherScriptv2(this, INITIAL, INITIAL,
					CRITICAL_FAIL, new OptionSelector<Class<?>>() {
						public Class<?> select() {
							return gui.getSelectedScript();
						}
					}, scriptStartCondition, new Condition() {
						public boolean validate() {
							return Patches.countAllWork(true) > 9;
						}
					});

			System.out.println("Start GUI...");
			gui = new GUI(new File(Environment.getStorageDirectory(), Players
					.getLocal().getName() + "-farming-settings.ini"), loader);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Setup finished");
	}

	@Override
	public void onRepaint(Graphics g) {
		if (!gui.isDone())
			return;

		int y = 35;
		g.setFont(new Font("Arial", Font.BOLD, 12));
		for (Location location : Location.locations) {
			if (!location.activated)
				continue;
			g.setColor(Color.YELLOW);
			g.fillRect(5, y, 200, 15);
			g.setColor(Color.BLACK);
			g.drawString(location.toString()
					+ (location.selectedTeleportOption == null ? "" : " - "
							+ location.selectedTeleportOption), 10, y + 10);
			y += 15;
			for (Patch patch : Patches.patches.values()) {
				if (!patch.activated)
					continue;
				if (patch.getLocation() == location) {
					g.setColor(Color.YELLOW);
					g.fillRect(5, y, 200, 15);
					g.setColor(Color.RED);
					g.fillRect(20, y + 2, 100, 11);
					int width = (int) Math.round(patch.getProgress() * 100);
					if (patch.isDiseased()) {
						g.setColor(Color.GRAY);
						g.fillRect(20, y + 2, 100, 11);
					} else if (patch.isDead()) {
						g.setColor(Color.BLACK);
						g.fillRect(20, y + 2, 100, 11);
					} else if (patch.isWatered()) {
						g.setColor(Color.BLUE);
						g.fillRect(20, y + 2, Math.min(100, width), 11);
					} else if (patch.countWeeds() > 0) {
						g.setColor(Color.GREEN);
						g.fillRect(20, y + 2, 100, 11);
					} else {
						g.setColor(new Color(0, 128, 0));
						g.fillRect(20, y + 2, Math.min(100, width), 11);
					}
					g.setColor(Color.BLACK);
					g.drawString(patch.getCorrespondingSeed() == null ? "Weed"
							: patch.getCorrespondingSeed().toString(), 130,
							y + 10);
					y += 15;
				}
			}
			for (Entry<String, Integer> item : Product.notedProducts.entrySet()) {
				/* if (item.getValue() > 0) */{
					g.setColor(Color.YELLOW);
					g.fillRect(5, y, 200, 15);
					g.setColor(Color.BLACK);
					g.drawString(item.getKey() + ": " + item.getValue(), 10,
							y + 10);
					y += 15;
				}
			}
		}

		/** inject the alternative script's painting into ours **/
		if (MODULE_RUN_SCRIPT.activeScript != null) {
			for (Class<?> i : MODULE_RUN_SCRIPT.activeScript.getClass()
					.getInterfaces()) {
				if (i.getName().equals(
						"org.powerbot.game.bot.event.listener.PaintListener")) {
					PaintListener paint = (PaintListener) MODULE_RUN_SCRIPT.activeScript;
					paint.onRepaint(g);
					g.setColor(Color.YELLOW);
					break;
				}
			}
		}
		g.setColor(Color.YELLOW);
		g.fillRect(5, 5, 110, 30);
		g.setColor(Color.BLACK);
		g.drawString("Farming work: " + Patches.countAllWork(true), 7, 18);
		g.drawString("Time: " + timer.toElapsedString(), 7, 33);

	}

	public void customProvide(Strategy s) {
		provide(s);
	}

	public void customRevoke(Strategy s) {
		revoke(s);
	}

	public class Antiban extends Strategy implements Task {
		Timer timer = new Timer(Random.nextInt(30000, 40000));

		public void run() {
			int action = Random.nextInt(0, 4);
			switch (action) {
			case 0:
				int randomSkill = Random.nextInt(0, 24);
				Tabs.STATS.open();
				WidgetChild randStat = Skills.getWidgetChild(randomSkill);
				Point randStatPoint = randStat.getAbsoluteLocation();
				randStatPoint.x += Random.nextInt(-10, 10); // Don't have the
															// mouse go to the
															// EXACT same spot
															// every time! :)
				randStatPoint.y += Random.nextInt(-10, 10); // Also, you can
															// change all of
															// these values. >_>
				Mouse.move(randStatPoint);
				break;
			default:
				int currentPitch = Camera.getPitch();
				int currentYaw = Camera.getYaw();
				Camera.setPitch(currentPitch + Random.nextInt(-50, 50));
				Camera.setAngle(currentYaw + Random.nextInt(-70, 70));
				break;
			}
			if (Widgets.get(906, 186).isOnScreen()) {
				Widgets.get(906, 186).click(true);
				Time.sleep(700, 3000);
			}
			timer.setEndIn(Random.nextInt(30000, 40000));
		}

		public boolean validate() {
			// don't mess with the antiban of the other script
			return !timer.isRunning() && MODULE_RUN_SCRIPT.activeScript == null;
		}
	}

}
