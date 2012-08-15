package scripts.state.edge;

import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;

import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.State;
import scripts.state.StateCreator;

public class WalkPath extends Edge {
	public WalkPath(Condition c, final Tile[] tiles, State success, final Timeout timeout) {
		super(c, new ConsecutiveState<Tile>(tiles, success, new StateCreator<Tile>() {
			public State getState(final Tile tile, State nextState) {
				return new State()
				.add(new Edge(new Condition() {
					public boolean validate() {
						Walking.walk(tile);
						return Players.getLocal().getLocation().distance(tile) <= 5;
					}}, nextState))
				.add(timeout);
			}
		}));	
	}
}
