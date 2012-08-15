package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;

public class Edge {
	protected Condition condition;
	protected State state;
	
	public Edge(Condition condition_, State state_) {
		condition = condition_;
		state = state_;
	}
	
	public Edge(Condition condition_) {
		this(condition_,null);
	}
	
	public Edge() {
		this(Condition.FALSE,null);
	}
	
	public void cleanup() {}
	
	public void setCondition(Condition condition_) {
		condition = condition_;
	}
	
	public void setState(State state_) {
		state = state_;
	}
	
	public State validate() {
		return condition.validate() ? state : null;
	}
}
