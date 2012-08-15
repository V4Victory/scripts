package scripts.state;

import java.util.Collection;

import scripts.state.edge.Edge;
import scripts.state.edge.Task;

public class ConsecutiveState<T> extends State {
	private static <T> State init(T[] values, State finalState,
			StateCreator<T> stateCreator) {
		State currentState = finalState;
		for (int i = values.length - 1; i >= 0; i--) {
			currentState = stateCreator.getState(values[i], currentState);
		}
		return currentState;
	}

	public ConsecutiveState(T[] values, State finalState,
			StateCreator<T> stateCreator) {
		super(init(values, finalState, stateCreator));
	}

	public ConsecutiveState(Collection<T> values, State finalState,
			StateCreator<T> stateCreator) {
		super(init((T[]) values.toArray(), finalState, stateCreator));
	}

	public ConsecutiveState(final Value<? extends Collection<T>> values,
			final State finalState, final StateCreator<T> stateCreator) {
		final State statesRemoved = new State();
		add(new Task(Condition.TRUE, statesRemoved) {
			public void run() {
				statesRemoved.removeAllEdges();
				statesRemoved.add(new Edge(Condition.TRUE, init((T[])values.get().toArray(),finalState,stateCreator)));
			}
		});
	}
}
