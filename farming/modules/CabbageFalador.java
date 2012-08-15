package scripts.farming.modules;

import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.Value;
import scripts.state.edge.AnimationPath;
import scripts.state.edge.InteractItem;
import scripts.state.edge.InteractSceneObject;
import scripts.state.edge.Timeout;
import scripts.state.edge.Walk;

@Target("Falador")
public class CabbageFalador extends Module {

	public CabbageFalador(State INITIAL, State SUCCESS, State CRITICAL) {
		super(
				"Cabbage port",
				INITIAL,
				SUCCESS,
				CRITICAL,
				new Requirement[] { new Requirement(1, Constants.ExplorersRing3)
						.or(new Requirement(1, Constants.ExplorersRing4)) });
		
		State TELEPORTED = new State();
		State TELEPORTING = new State();
		State IN_FRONT_OF_GATE = new State();
		INITIAL.add(new InteractItem(Condition.TRUE, TELEPORTING,
				Constants.ExplorersRing3, "Cabbage-port"));
		INITIAL.add(new InteractItem(Condition.TRUE, TELEPORTING,
				Constants.ExplorersRing4, "Cabbage-port"));
		TELEPORTING.add(new AnimationPath(Condition.TRUE, new Integer[] { 9984,
				9986 }, TELEPORTED, new Timeout(INITIAL, 6000)));
		TELEPORTED.add(new Walk(Condition.TRUE, new Tile(3052, 3299, 0),
				IN_FRONT_OF_GATE, new Timeout(INITIAL, 5000)));
		IN_FRONT_OF_GATE.add(new InteractSceneObject(Condition.TRUE,
				IN_FRONT_OF_GATE, new Value<SceneObject>() {
					public SceneObject get() {
						return SceneEntities.getNearest(7049);
					}
				}, "Open"));
		IN_FRONT_OF_GATE.add(new Walk(Condition.TRUE, new Tile(3056, 3307, 0),
				SUCCESS, new Timeout(INITIAL, 5000)));

	}
}