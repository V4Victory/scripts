package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import state.Condition;
import state.Module;
import state.State;
import state.edge.Animation;
import state.edge.MagicCast;
import state.edge.Timeout;
import state.edge.WalkPath;

@Target("Catherby")
public class LunarCatherby extends Module {
	public LunarCatherby(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Lunar Catherby teleport", INITIAL, SUCCESS, CRITICAL,
				new Requirement[] {
						new Requirement(0, Constants.AstralRune),
						new Requirement(0, Constants.LawRune),
						new Requirement(1, Constants.MudBattleStaff)
								.or(new Requirement(1,
										Constants.MysticMudBattleStaff)) });
		State TELEPORTED = new State();
		State TELEPORTING = new State();

		Tile[] path = new Tile[] { new Tile(2807, 3463, 0) };

		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTING, INITIAL,
				Magic.Lunar.TeleportCatherby));

		TELEPORTING.add(new Animation(Condition.TRUE, 9606, TELEPORTED, new Timeout(INITIAL,10000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}