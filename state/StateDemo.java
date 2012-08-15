package scripts.state;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.state.edge.AnimationPath;
import scripts.state.edge.Either;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;


@Manifest(authors = { "djabby" }, name = "StateDemo", description = "Runs to morytania patch", version = 1.00)
public class StateDemo extends ActiveScript {

	// Integer[] refillAnimations = new Integer[] { 9609, 8939, 8941, 832 };
	Integer[] refillAnimations = new Integer[] { 832 };

	Tile[] path = new Tile[] { new Tile(3658, 3522, 0),
			new Tile(3659, 3529, 0), new Tile(3651, 3529, 0),
			new Tile(3642, 3529, 0), new Tile(3635, 3531, 0),
			new Tile(3628, 3533, 0), new Tile(3587, 3541, 0),
			new Tile(3627, 3533, 0), new Tile(3619, 3533, 0),
			new Tile(3611, 3533, 0), new Tile(3606, 3528, 0) };

	/**
	 * initial state > uses ectovial
	 */
	State INITIAL = new State();

	/**
	 * teleporting done > refill vial
	 */
	State ECTOFUNTUS_REFILL = new State();

	/**
	 * check vial state > if refilled, proceed > if not refilled, try to fill
	 * manually
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
	 * arrived at patch > finish!
	 */
	State SUCCESS = new State();

	/**
	 * something went wrong > try again
	 */
	State FAIL = new State();

	protected void setup() {

		INITIAL.add(new Task(Condition.TRUE, ECTOFUNTUS_REFILL) {
			public void run() {
				Item vial = Inventory.getItem(4251);
				if (vial != null)
					vial.getWidgetChild().interact("Empty");
			}
		});
		ECTOFUNTUS_REFILL.add(new AnimationPath(Condition.TRUE,
				refillAnimations, ECTOFUNTUS_DONE, new Timeout(FAIL, 10000)));

		ECTOFUNTUS_CHECK.add(new Either(new Condition() {
			public boolean validate() {
				return Inventory.getCount(4251) == 1;
			}
		}, ECTOFUNTUS_DONE, ECTOFUNTUS_REFILL_MANUALLY));

		ECTOFUNTUS_REFILL_MANUALLY.add(new Task(Condition.TRUE,
				ECTOFUNTUS_REFILL) {
			public void run() {
				Item vial = Inventory.getItem(4251);
				if (vial != null)
					vial.getWidgetChild().interact("Use");
			}
		});

		ECTOFUNTUS_DONE.add(new WalkPath(Condition.TRUE, path, SUCCESS,
				new Timeout(FAIL, 10000)));

		SUCCESS.add(new Task(Condition.TRUE, null) {
			public void run() {
				System.out.println("Success!!");
				StateDemo.this.stop();
			}
		});

		FAIL.add(new Task(Condition.TRUE, INITIAL) {
			public void run() {
				System.out.println("Fail, try again!");
			}
		});

		provide(new StateStrategy(INITIAL, Condition.TRUE));
	}
}