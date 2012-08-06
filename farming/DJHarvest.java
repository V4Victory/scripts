package scripts.farming;

import java.io.File;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.interactive.Players;

import state.Condition;
import state.Module;
import state.State;
import state.StateStrategy;
import state.edge.Edge;
import state.edge.Option;
import state.edge.Timeout;
import state.tools.OptionSelector;
import farming.modules.CheckWorkload;
import farming.modules.DoPatches;
import farming.modules.RunOtherScript;

@Manifest(name = "DJHarvest", authors = "djabby", version = 1.00, description = "does farming")
public class DJHarvest extends ActiveScript {
	
	GUI gui;

	State INITIAL = new State();
	State CRITICAL_FAIL = new State();

	State ON_CHOOSE_LOCATION = new State();
	State START_OTHER_SCRIPT = new State();
	State LOAD_GUI = new State();
	
	RunOtherScript MODULE_RUN_SCRIPT = new RunOtherScript(START_OTHER_SCRIPT,
			INITIAL, CRITICAL_FAIL, null, new Condition() {
				public boolean validate() {
					return Patches.countAllWork() == 0;
				}
			}, new Condition() {
				public boolean validate() {
					return Patches.countAllWork() > 9;
				}
			}, ScriptWrapper.class);

	CheckWorkload MODULE_LOCATION = new CheckWorkload(INITIAL,
			ON_CHOOSE_LOCATION, START_OTHER_SCRIPT);

	// PatchModule MODULE_FALADOR = new PatchModule();
	Module MODULE_FALADOR;

	protected void setup() {
		Option<Location> chooseLocation = new Option<Location>(Condition.TRUE,
				new OptionSelector<Location>() {
					public Location select() {
						Location loc = null;
						Integer mostWorkCount = 0;
						// select the location with the most work to do
						for (Location location : Location.locations) {
							if (location.isActivated()
									&& mostWorkCount < location.countWork(false)) {
								mostWorkCount = location.countWork(false);
								loc = location;
							}
						}
						return loc;
					}
				});
		ON_CHOOSE_LOCATION.add(chooseLocation);
		ON_CHOOSE_LOCATION.add(new Timeout(INITIAL, 2000));
		// add all locations to the options
		for (final Location location : Location.locations) {
			State state = new State();
			State reached = new State();
			chooseLocation.add(location, state);
			Option<Module> chooseTeleport = new Option<Module>(Condition.TRUE,
					new OptionSelector<Module>() {
						public Module select() {
							return location.selectedTeleportOption;
						}
					});
			state.add(chooseTeleport);
			state.add(new Timeout(INITIAL, 2000));
			for (Module module : location.getTeleportOptions()) {
				chooseTeleport.add(module, reached);
			}

			Module doPatches = new DoPatches(location, reached, INITIAL,
					CRITICAL_FAIL);

		}
		
		gui = new GUI(new File(Environment.getStorageDirectory(), Players
				.getLocal().getName() + "-farming-settings.ini"));
		
		LOAD_GUI.add(new Edge(new Condition() {
			public boolean validate() { return gui.isDone(); }
		},INITIAL));

		provide(new StateStrategy(LOAD_GUI, Condition.TRUE));
	}
}
