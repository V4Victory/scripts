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

@Target("Falador")
public class DraynorLoadstone extends Module{

	public DraynorLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Draynor loadstone",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();
		
		Tile[] path = new Tile[] {
				new Tile(3103,3299,0)
				,new Tile(3099,3306,0)
				,new Tile(3095,3312,0)
				,new Tile(3091,3319,0)
				,new Tile(3084,3323,0)
				,new Tile(3083,3325,0)
				,new Tile(3078,3331,0)
				,new Tile(3072,3335,0)
				,new Tile(3069,3328,0)
				,new Tile(3063,3324,0)
				,new Tile(3061,3317,0)
				,new Tile(3057,3311,0)
				};
	
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 44).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Widgets.get(1092, 44).click(true);
			}
		});
		TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
				new Timeout(INITIAL, 15000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}