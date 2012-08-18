package scripts.farming.modules;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Bank")
public class LunarBank extends Module {
	public LunarBank(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Lunar Bank Loadstone", INITIAL, SUCCESS, CRITICAL);
		State CASTED = new State();
		State TELEPORTED = new State();
		State TELEPORTING = new State();

		Tile[] path = new Tile[] { new Tile(2093, 3914, 0),
				new Tile(2098, 3919, 0) };

		INITIAL.add(new AssureLocation(Condition.TRUE,new Tile(2085,3914,0),3,TELEPORTED));
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Standard.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 39).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Mouse.move(Widgets.get(1092, 39).getCentralPoint());
				Widgets.get(1092, 39).click(true);
				Time.sleep(700);
			}
		});
		// Unsafe, because spell can be interrupted
//		TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
//				new Timeout(INITIAL, 15000)));
		TELEPORTING.add(new AssureLocation(Condition.TRUE,new Tile(2085,3914,0),3,TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL,8000));

		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}