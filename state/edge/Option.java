package scripts.state.edge;

import java.util.HashMap;
import java.util.Map;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.tools.OptionSelector;

public class Option<T> extends Edge {
	Map<T,Edge> edges = new HashMap<T,Edge>();
	OptionSelector<T> selector;
	
	public Option(Condition c, OptionSelector<T> selector_) {
		super(c, new State("OPT"));
		selector = selector_;
	}
	
	public Map<T,Edge> getEdges() { return edges; }
	
	public Option<T> add(T t, State s) {
		Edge e = new Edge(selector.createCondition(t), s);
		edges.put(t,e);
		state.add(e);
		return this;
	}
}
