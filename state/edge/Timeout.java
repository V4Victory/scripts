package scripts.state.edge;

import org.powerbot.game.api.util.Timer;

import scripts.state.Condition;
import scripts.state.State;

public class Timeout extends Edge {
	Timer timer = null;
	int timeout;
	
	public Timeout(final State state, Timeout timeout) {
		this(state,timeout.timeout);
	}
	public Timeout(final State state, final int timeout_) {
		setCondition(new Condition() {
			public boolean validate() {
			if(timer == null) {
				timer = new Timer(timeout_);
			}
			if(!timer.isRunning()) System.out.println("Timeout"+timeout_);
			return !timer.isRunning();
		}});
		setState(state);
		timeout = timeout_;
	}
	
	public void cleanup() {
		timer = null;
	}

}
