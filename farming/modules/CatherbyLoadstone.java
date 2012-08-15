package scripts.farming.modules;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Animation;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Catherby")
public class CatherbyLoadstone extends Module{

	public CatherbyLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Catherby loadstone",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();
		
		Tile[] path = new Tile[] {
				new Tile(2831,3451,0)
				,new Tile(2825,3455,0)
				,new Tile(2818,3457,0)
				,new Tile(2812,3461,0)
				};
	
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 43).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Widgets.get(1092, 43).click(true);
			}
		});
		TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
				new Timeout(INITIAL, 15000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}