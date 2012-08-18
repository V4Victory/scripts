package scripts.state.edge;

import java.util.Map;

import scripts.state.State;


public interface OptionCreator<T> {
	public Map<T,State> create();
}
