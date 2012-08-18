package scripts.state.edge;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;

import scripts.state.Condition;
import scripts.state.State;

public class AssureLocation extends Edge {

	public AssureLocation(Condition condition_, final Tile loc_,
			final int diff, State state_) {
		super(condition_.and(new Condition() {
			public boolean validate() {
				return new Area(new Tile(loc_.getX() - diff,
						loc_.getY() - diff, 0), new Tile(loc_.getX() + diff,
						loc_.getY() + diff, 0)).contains(Players.getLocal());
			}
		}), state_);
	}

}
