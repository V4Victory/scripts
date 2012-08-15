package scripts.state;

import java.util.ArrayList;
import java.util.List;

import scripts.state.edge.Edge;

public class State {
	List<Edge> edges = new ArrayList<Edge>();
	public static int i = 0;
	public String name;
	public int id;
	
	public State() {
		this("");
	}
	
	public State(State other) {
		this();
		edges = other.edges;
	}
	
	public State(String name_) {
		name = name_;
		id = i;
		i++;
	}
	
	public State(State other, String name_) {
		this(name_);
		edges = other.edges;
	}
	
	public State add(Edge e) {
		edges.add(e);
		return this;
	}

	public State run() {
		System.out.print(name + " " + id+",");
		for(Edge edge : edges) {
			State s = edge.validate();
			if(s != null) {
				for(Edge edge_ : edges) edge_.cleanup();
				return s;
			}
		}
		return this;
	}

	public void removeAllEdges() {
		edges.clear();
	}
}
