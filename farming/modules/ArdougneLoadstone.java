package scripts.farming.modules;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.Magic;
import state.Condition;
import state.Module;
import state.State;
import state.edge.Animation;
import state.edge.MagicCast;
import state.edge.Task;
import state.edge.Timeout;
import state.edge.WalkPath;

@Target("Ardougne")
public class ArdougneLoadstone extends Module{

	public ArdougneLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Ardougne loadstone",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();
		
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
	
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 41).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Widgets.get(1092, 41).click(true);
			}
		});
		TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
				new Timeout(INITIAL, 15000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}