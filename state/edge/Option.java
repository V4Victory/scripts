package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.tools.OptionSelector;

public class Option<T> extends Edge {
	OptionSelector<T> selector;
	
	public Option(Condition c, OptionSelector<T> selector_) {
		super(c, new State("OPT"));
		selector = selector_;
	}
	
	public Option<T> add(T t, State s) {
		state.add(new Edge(selector.createCondition(t), s));
		//edgeValues.add(t);
		return this;
	}
}
