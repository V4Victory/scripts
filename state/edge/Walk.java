package scripts.state.edge;

import org.powerbot.game.api.wrappers.Tile;

import scripts.state.Condition;
import scripts.state.State;

public class Walk extends WalkPath {
	public Walk(Condition c, final Tile tile, State success, final Timeout timeout) {
		super(c,new Tile[] { tile },success,timeout);
	}
}
