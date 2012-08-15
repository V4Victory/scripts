package scripts.farming.modules;

import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Ardougne")
public class LunarArdougneTele extends Module{

	public LunarArdougneTele(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Lunar North-Ardougne teleport",INITIAL,SUCCESS,CRITICAL,
				new Requirement[] { new Requirement(0, Constants.AstralRune),
				new Requirement(0, Constants.LawRune),
				new Requirement(1, Constants.MudBattleStaff)
				.or(new Requirement(1, Constants.MysticMudBattleStaff))});		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		
		Tile[] path = new Tile[] {
				new Tile(2615,3347,0),
				new Tile(2615,3355,0),
				new Tile(2615,3363,0),
				new Tile(2620,3368,0),
				new Tile(2626,3373,0),
				new Tile(2630,3379,0),
				new Tile(2638,3379,0),
				new Tile(2645,3379,0),
				new Tile(2652,3381,0),
				new Tile(2660,3381,0),
				new Tile(2664,3374,0)};
	
		INITIAL.add(new MagicCast(Condition.TRUE, TELEPORTING, INITIAL, Magic.Lunar.TeleportNorthArdougne));
		TELEPORTING.add(new Animation(Condition.TRUE, 9606, TELEPORTED, new Timeout(INITIAL,10000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}