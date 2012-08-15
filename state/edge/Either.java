package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;

public class Either extends Edge {
	State other;
	
	public Either(Condition c_, State s_, State other_) {
		super(c_,s_);
		other = other_;
	}

	public State validate() {
		return condition.validate() ? state : other;
	}
}
