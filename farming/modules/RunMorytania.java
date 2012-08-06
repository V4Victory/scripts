package scripts.farming.modules;

import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;

import state.Condition;
import state.Module;
import state.State;
import state.edge.AnimationPath;
import state.edge.Either;
import state.edge.InteractItem;
import state.edge.Notification;
import state.edge.Timeout;
import state.edge.UseItem;
import state.edge.WalkPath;

public class RunMorytania extends Module {

	public String toString() {
		return "Ectovial";
	}

	public RunMorytania(State INITIAL, State SUCCESS, State CRITICAL) {
		super(INITIAL, SUCCESS, CRITICAL);
		// Integer[] refillAnimations = new Integer[] { 9609, 8939, 8941, 832 };
		Integer[] refillAnimations = new Integer[] { 832 };

		Tile[] path = new Tile[] { new Tile(3658, 3522, 0),
				new Tile(3659, 3529, 0), new Tile(3651, 3529, 0),
				new Tile(3642, 3529, 0), new Tile(3635, 3531, 0),
				new Tile(3628, 3533, 0), new Tile(3587, 3541, 0),
				new Tile(3627, 3533, 0), new Tile(3619, 3533, 0),
				new Tile(3611, 3533, 0), new Tile(3606, 3528, 0) };

		/**
		 * teleporting done > refill vial
		 */
		State ECTOFUNTUS_REFILL = new State();

		/**
		 * check vial state > if refilled, proceed > if not refilled, try to
		 * fill manually
		 */
		State ECTOFUNTUS_CHECK = new State();

		/**
		 * the refilling failed somehow > refill vial manually
		 */
		State ECTOFUNTUS_REFILL_MANUALLY = new State();

		/**
		 * refilling done > run to farming patch
		 */
		State ECTOFUNTUS_DONE = new State();

		/**
		 * something went wrong > try again
		 */
		State FAIL = new State();

		State VIAL_FOUND = new State();

		INITIAL.add(new Either(new Condition() {
			public boolean validate() {
				return Inventory.getCount(4251) > 0;
			}
		}, VIAL_FOUND, CRITICAL));

		VIAL_FOUND.add(new InteractItem(Condition.TRUE, ECTOFUNTUS_REFILL,
				4251, "Empty"));
		ECTOFUNTUS_REFILL.add(new AnimationPath(Condition.TRUE,
				refillAnimations, ECTOFUNTUS_DONE, new Timeout(FAIL, 10000)));

		ECTOFUNTUS_CHECK.add(new Either(new Condition() {
			public boolean validate() {
				return Inventory.getCount(4251) == 1;
			}
		}, ECTOFUNTUS_DONE, ECTOFUNTUS_REFILL_MANUALLY));

		ECTOFUNTUS_REFILL_MANUALLY.add(new UseItem(Condition.TRUE,ECTOFUNTUS_REFILL,4251,SceneEntities.getNearest(12345)));

		ECTOFUNTUS_DONE.add(new WalkPath(Condition.TRUE, path, SUCCESS,
				new Timeout(FAIL, 10000)));

		FAIL.add(new Notification(Condition.TRUE, INITIAL, "Fail, try again!"));

	}
}