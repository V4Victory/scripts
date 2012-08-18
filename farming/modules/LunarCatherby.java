package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Equipment;
import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.Equip;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

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

		INITIAL.add(new AssureLocation(Condition.TRUE,new Tile(2803,3451,0),3,TELEPORTED));
		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTING, INITIAL,
				Magic.Lunar.TeleportCatherby));

		TELEPORTING.add(new Animation(Condition.TRUE, 9606, TELEPORTED,
				new Timeout(INITIAL, 10000)));
		TELEPORTING.add(new AssureLocation(Condition.TRUE,new Tile(2803,3451,0),3,TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL,8000));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}