package scripts.farming.modules;

import scripts.farming.Location;
import scripts.farming.Patch;
import state.Condition;
import state.ConsecutiveState;
import state.Module;
import state.State;
import state.StateCreator;
import state.edge.Animation;
import state.edge.Edge;
import state.edge.InteractItem;
import state.edge.InteractSceneObject;
import state.edge.Notification;
import state.edge.Task;
import state.edge.Timeout;
import state.edge.UseItem;

public class DoPatches extends Module {
	public DoPatches(Location loc, State initial, State success, State critical) {
		super(initial, success, critical);

		initial.add(new Edge(Condition.TRUE, new ConsecutiveState<Patch>(loc
				.getPatches(), success, new StateCreator<Patch>() {
			public State getState(final Patch patch, State nextState) {
				return doPatch(patch).add(new Edge(new Condition() {
					public boolean validate() {
						return patch.isGrowing();
					}
				}, nextState)).add(new Timeout(nextState, 5000));
			}
		})));
	}

	public State doPatch(final Patch patch) {
		State state = new State();
		State processProducts = new State();

		// after idling for 5 seconds, stop script
		state.add(new Timeout(getCriticalState(), 5000));

		// Rake the patch
		State raking = new State();
		State rakingFailed = new State();
		state.add(new InteractSceneObject(new Condition() {
			public boolean validate() {
				return patch.countWeeds() > 0;
			}
		}, raking, patch.getSceneObject(), "Rake"));
		raking.add(new Animation(Condition.TRUE, 123456789, processProducts,
				new Timeout(rakingFailed, 3000)));
		rakingFailed.add(new Notification(Condition.TRUE, state,
				"Raking failed"));

		// Cure disease
		State curing = new State();
		State curingFailed = new State();
		state.add(new UseItem(Condition.TRUE, curing, 123456789, patch
				.getSceneObject()));
		curing.add(new Animation(Condition.TRUE, 12345478, state, new Timeout(
				curingFailed, 3000)));
		curingFailed.add(new Notification(Condition.TRUE, state,
				"Curing failed"));

		// Clear dead
		State clearing = new State();
		State clearingFailed = new State();
		// state.add(new
		// UseItem(Condition.TRUE,curing,123456789,patch.getSceneObject()));
		clearing.add(new Animation(Condition.TRUE, 12345478, state,
				new Timeout(clearingFailed, 3000)));
		clearingFailed.add(new Notification(Condition.TRUE, state,
				"Clearing failed"));

		// check if you should wear secateurs
		state.add(new InteractItem(new Condition() {
			public boolean validate() {
				return patch.useSecateurs();
			}
		}, state, 123456789, "Equip"));

		// harvest
		State harvesting = new State();
		State harvestingFailed = new State();
		state.add(new InteractSceneObject(new Condition() {
			public boolean validate() {
				return patch.getProgress() == 1.0;
			}
		}, harvesting, patch.getSceneObject(),
				patch.getHarvestingInteraction(), true));
		harvesting.add(new Animation(Condition.TRUE, 12345478, state,
				new Timeout(harvestingFailed, 3000)));
		harvestingFailed.add(new Notification(Condition.TRUE, state,
				"Harvesting failed"));

		// Plant a seed
		State planting = new State();
		State plantingFailed = new State();
		State plantedPre = new State();
		State planted = new State();
		state.add(new UseItem(new Condition() {
			public boolean validate() {
				return patch.isEmpty() && patch.countWeeds() == 0;
			}
		}, planting, 123456789, patch.getSceneObject()));
		planting.add(new Animation(Condition.TRUE, 12345478, plantedPre,
				new Timeout(plantingFailed, 3000)));
		plantingFailed.add(new Notification(Condition.TRUE, state,
				"Planting failed"));
		plantedPre.add(new Task(Condition.TRUE, planted) {
			public void run() {
				patch.compost = false;
			}
		});

		// Water patch
		State watering = new State();
		State wateringFailed = new State();
		planted.add(new UseItem(new Condition() {
			public boolean validate() {
				return patch.canWater() && !patch.isWatered();
			}
		}, watering, 123456789, patch.getSceneObject()));
		watering.add(new Animation(Condition.TRUE, 12345478, planted,
				new Timeout(wateringFailed, 3000)));
		wateringFailed.add(new Notification(Condition.TRUE, planted,
				"Watering failed"));

		// Composting patch and return to 'state'
		State composting = new State();
		State compostingFailed = new State();
		State composted = new State();
		planted.add(new UseItem(new Condition() {
			public boolean validate() {
				return !patch.compost;
			}
		}, composting, 123456789, patch.getSceneObject()));
		composting.add(new Animation(Condition.TRUE, 12345478, composted,
				new Timeout(compostingFailed, 3000)));
		composted.add(new Task(Condition.TRUE, state) {
			public void run() {
				patch.compost = true;
			}
		});
		compostingFailed.add(new Notification(Condition.TRUE, state,
				"Composting failed"));

		return state;
	}
}
