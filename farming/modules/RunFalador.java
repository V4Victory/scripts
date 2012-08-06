package scripts.farming.modules;

import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.Tile;

import state.Condition;
import state.Module;
import state.State;
import state.edge.InteractItem;
import state.edge.InteractSceneObject;
import state.edge.Timeout;
import state.edge.Walk;

public class RunFalador extends Module {

	public String toString() {
		return "Cabbage port";
	}

	public RunFalador(State INITIAL, State SUCCESS, State CRITICAL) {
		super(INITIAL, SUCCESS, CRITICAL);
		State TELEPORTED = new State();
		State IN_FRONT_OF_GATE = new State();
		INITIAL.add(new InteractItem(Condition.TRUE, TELEPORTED, 123456, "Cabbage-port"));
		TELEPORTED.add(new Walk(Condition.TRUE, new Tile(12345,12345,0), IN_FRONT_OF_GATE,new Timeout(INITIAL,5000)));
		IN_FRONT_OF_GATE.add(new InteractSceneObject(Condition.TRUE, IN_FRONT_OF_GATE, SceneEntities.getNearest(12345), "Open"));
		IN_FRONT_OF_GATE.add(new Walk(Condition.TRUE,new Tile(123,123,0), SUCCESS, new Timeout(INITIAL, 5000)));
	}
}