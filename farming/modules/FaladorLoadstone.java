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

@Target("Falador")
public class FaladorLoadstone extends Module{

	public FaladorLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Falador loadstone",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();
		
		Tile[] path = new Tile[] {
				new Tile(2967,3403,0)
				,new Tile(2967,3395,0)
				,new Tile(2967,3387,0)
				,new Tile(2968,3380,0)
				,new Tile(2975,3378,0)
				,new Tile(2982,3374,0)
				,new Tile(2989,3371,0)
				,new Tile(3023,3330,0)
				,new Tile(2992,3369,0)
				,new Tile(2997,3363,0)
				,new Tile(3000,3356,0)
				,new Tile(3005,3350,0)
				,new Tile(3007,3343,0)
				,new Tile(3007,3335,0)
				,new Tile(3007,3330,0)
				,new Tile(3008,3322,0)
				,new Tile(3015,3324,0)
				,new Tile(3022,3323,0)
				,new Tile(3030,3323,0)
				,new Tile(3076,3314,0)
				,new Tile(3036,3322,0)
				,new Tile(3042,3318,0)
				,new Tile(3049,3320,0)
				,new Tile(3054,3314,0)
				,new Tile(3056,3307,0)
				};
	
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 46).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Widgets.get(1092, 46).click(true);
			}
		});
		TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
				new Timeout(INITIAL, 15000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}