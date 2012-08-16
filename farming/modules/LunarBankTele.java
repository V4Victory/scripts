package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Equipment;
import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
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

		Tile[] path = new Tile[] { new Tile(2109, 3915, 0),
				new Tile(2102, 3915, 0), new Tile(2098, 3919, 0) };

		INITIAL.add(new Equip(Condition.TRUE, INITIAL,
				Constants.MudBattleStaff, Equipment.WEAPON, new Timeout(
						INITIAL, 5000)));
		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTED, INITIAL,
				Magic.Lunar.TeleportMoonClan));
		TELEPORTED.add(new WalkPath(Condition.TRUE, path, SUCCESS, new Timeout(
				INITIAL, 10000)));

	}
}