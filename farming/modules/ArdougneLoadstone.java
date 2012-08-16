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
import scripts.state.edge.MagicCast;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.WalkPath;

@Target("Ardougne")
public class ArdougneLoadstone extends Module{

	public ArdougneLoadstone(State INITIAL, State SUCCESS, State CRITICAL) {
		super("Ardougne loadstone",INITIAL,SUCCESS,CRITICAL);		
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State CASTED = new State();
		
		Tile[] path = new Tile[] {
				new Tile(2634,3348,0),
				new Tile(2635,3355,0),
				new Tile(2636,3362,0),
				new Tile(2641,3368,0),
				new Tile(2644,3375,0),
				new Tile(2649,3380,0),
				new Tile(2656,3381,0),
				new Tile(2661,3376,0),
				new Tile(2668,3375,0)};
	
		INITIAL.add(new MagicCast(Condition.TRUE, CASTED, INITIAL,
				Magic.Lunar.HomeTeleport));
		CASTED.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(1092, 41).isOnScreen();
			}
		}, TELEPORTING) {
			public void run() {
				Mouse.move(Widgets.get(1092,41).getCentralPoint());
				Widgets.get(1092, 41).click(true);
				Time.sleep(700);
			}
		});
		TELEPORTING.add(new Animation(Condition.TRUE, 16385, TELEPORTED,
				new Timeout(INITIAL, 15000)));
		TELEPORTED.add(new WalkPath(Condition.TRUE,path,SUCCESS,new Timeout(INITIAL,10000)));

	}
}