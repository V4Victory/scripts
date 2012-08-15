package scripts.state;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.util.Time;

public class StateSequenceStrategy extends Strategy 
	implements Task, org.powerbot.concurrent.strategy.Condition {
	State currentState, initialState;
	Condition startCondition;
	public StateSequenceStrategy(State initial, Condition c) {
		initialState = initial;
		startCondition = c;
	}
	
	public void run() {
		if(currentState == null) return;
		while((currentState = currentState.run()) != null) {
			Time.sleep(5);
		}
	}
	
	public State getCurrentState() { return currentState; }
	
	public boolean validate() { 
		if(currentState == null && startCondition.validate()) {
			currentState = initialState;
			return true;
		} else
			return false;
	}

}
