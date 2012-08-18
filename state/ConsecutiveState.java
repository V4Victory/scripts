package scripts.state;

import java.util.Collection;

import scripts.state.edge.Either;
import scripts.state.edge.ValuedEither;
import scripts.state.tools.Cons;

public class ConsecutiveState<T> extends State {
	private static <T> State init(T[] values, State finalState,
			StateCreator<T> stateCreator) {
		State currentState = finalState;
		for (int i = values.length - 1; i >= 0; i--) {
			currentState = stateCreator.getState(values[i], currentState);
		}
		System.out.println("Last: " + "["+currentState.name + "," + currentState.id+"]");
		return currentState;
	}

	private static <T> State init(final Value<Cons<T>> values,
			final State finalState, final StateCreator<T> stateCreator) {
		State first = new State();
		first.add(new ValuedEither<Cons<T>>(Condition.TRUE, values) {
			public boolean validateValue(Cons<T> val) {
				return val.isEmpty();
			}

			public State getFirstState(Cons<T> val) {
				return finalState;
			}

			public State getSecondState(final Cons<T> val) {
				return stateCreator.getState(val.getHead(),
						init(new Value<Cons<T>>() {
							public Cons<T> get() {
								return val.getTail();
							}
						}, finalState, stateCreator));
			}
		}

		);

		return first;
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
		super(init(new Value<Cons<T>>() {
			public Cons<T> get() {
				return new Cons<T>(values.get().iterator());
			}
		}, finalState, stateCreator));
	}
}
