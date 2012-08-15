package scripts.state;

public interface StateCreator<T> {
	public State getState(T value, State nextState);
}
