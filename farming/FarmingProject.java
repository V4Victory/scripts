package scripts.farming;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;

import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.bot.event.listener.PaintListener;

import scripts.farming.modules.Banker;
import scripts.farming.modules.DoPatches;
import scripts.farming.modules.RunOtherScript;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.StateStrategy;
import scripts.state.edge.Option;
import scripts.state.edge.Edge;
import scripts.state.edge.Timeout;
import scripts.state.tools.OptionSelector;

@Manifest(name = "DJHarvest", authors = "djabby", version = 1.00, description = "does farming")
public class FarmingProject extends ActiveScript implements PaintListener {

	Timer timer = new Timer(0);

	GUI gui;

	State INITIAL;
	State CRITICAL_FAIL;

	State ON_CHOOSE_LOCATION;
	State LOAD_GUI;

	public ScriptLoader loader;
	public Banker banker;
	public RunOtherScript MODULE_RUN_SCRIPT;

	protected void setup() {
		try {
			System.out.println("Initialize...");

			loader = new ScriptLoader();
			INITIAL = new State("I");
			CRITICAL_FAIL = new State("C");
			ON_CHOOSE_LOCATION = new State("O");
			LOAD_GUI = new State("G");
			banker = new Banker(this, new State("BI"), new State("BS"),
					CRITICAL_FAIL);

			provide(new StateStrategy(LOAD_GUI, Condition.TRUE));

			INITIAL.add(new Edge(new Condition() {
				public boolean validate() {
					System.out.println("Total work = " + Patches.countAllWork());
					return Patches.countAllWork() > 0;
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
									System.out.println("Let's stay in "
											+ location.name);
									return location;
								}

								if (location.activated
										&& mostWorkCount < location
												.countWork(false)) {
									mostWorkCount = location.countWork(false);
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

			LOAD_GUI.add(new Edge(new Condition() {
				public boolean validate() {
					return gui.isDone();
				}
			}, INITIAL));

			System.out.println("Setup alternative script...");

			MODULE_RUN_SCRIPT = new RunOtherScript(this, INITIAL, INITIAL,
					CRITICAL_FAIL, new OptionSelector<Class<?>>() {
						public Class<?> select() {
							return gui.getSelectedScript();
						}
					}, new Condition() {
						public boolean validate() {
							return Patches.countAllWork() == 0
									&& gui.scriptsEnabled;
						}
					}, new Condition() {
						public boolean validate() {
							return Patches.countAllWork() > 9;
						}
					});

			System.out.println("Start GUI...");
			gui = new GUI(new File(Environment.getStorageDirectory(),
					"farming-settings.ini"), loader);

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
		g.drawString("Farming work: " + Patches.countAllWork(), 7, 18);
		g.drawString("Time: " + timer.toElapsedString(), 7, 33);

	}

	public void customProvide(Strategy s) {
		provide(s);
	}

	public void customRevoke(Strategy s) {
		revoke(s);
	}

}
