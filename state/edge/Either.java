package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.Constant;
import scripts.state.State;
import scripts.state.Value;

public class Either extends Edge {
	Value<State> other;
	
	public Either(Condition c_, State s_, State other_) {
		this(c_,s_,new Constant<State>(other_));
	}
	public Either(Condition c_, State s_, Value<State> other_) {
		super(c_,s_);
		other = other_;
	}

	public State validate() {
		return condition.validate() ? state : other.get();
	}
}
