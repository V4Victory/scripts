package scripts.state.edge;

import org.powerbot.game.api.util.Timer;

import scripts.state.Condition;
import scripts.state.State;

public class Timeout extends Edge {
	Timer timer = null;
	
	public Timeout(final State state, final int timeout) {
		setCondition(new Condition() {
			public boolean validate() {
			if(timer == null) {
				timer = new Timer(timeout);
			}
			return !timer.isRunning();
		}});
		setState(state);
	}
	
	public void cleanup() {
		timer = null;
	}

}
