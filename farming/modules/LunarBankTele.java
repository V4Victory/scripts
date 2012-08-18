package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Equipment;
import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.AssureLocation;
import scripts.state.edge.Equip;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Bank")
public class LunarBankTele extends Module {
	public LunarBankTele(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Lunar Bank Tele", INITIAL, SUCCESS, CRITICAL,
				new Requirement[] {
						new Requirement(0, Constants.AstralRune),
						new Requirement(0, Constants.LawRune),
						new Requirement(1, Constants.MudBattleStaff)
								.or(new Requirement(1,
										Constants.MysticMudBattleStaff)) });
		State TELEPORTED = new State();
		State TELEPORTING = new State();

		Tile[] path = new Tile[] { new Tile(2109, 3915, 0),
				new Tile(2102, 3915, 0), new Tile(2098, 3919, 0) };

		INITIAL.add(new AssureLocation(Condition.TRUE,new Tile(2115,3915,0),3,TELEPORTED));
		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTING, INITIAL,
				Magic.Lunar.TeleportMoonClan));
		TELEPORTING.add(new AssureLocation(Condition.TRUE,new Tile(2115,3915,0),3,TELEPORTED));
		TELEPORTING.add(new Timeout(INITIAL,8000));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}