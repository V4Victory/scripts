package scripts.state.tools;

import scripts.state.Condition;

public abstract class OptionSelector<T> {
	public Condition createCondition(final T value) {
		return new Condition() {
			public boolean validate() {
				return value.equals(select());
			}
		};
	}
	
	public abstract T select();
}
