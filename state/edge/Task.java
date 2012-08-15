package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;

public abstract class Task extends Edge {
	public Task(final Condition c, State s) {
		super();
		setCondition(new Condition() {
			public boolean validate() {
				if (c.validate()) {
					Task.this.run();
					return true;
				} else
					return false;
			}
		});
		setState(s);
	}
	
	public void setCondition(final Condition c) {
		super.setCondition(new Condition() {
			public boolean validate() {
				if (c.validate()) {
					Task.this.run();
					return true;
				} else
					return false;
			}
		});
	}

	public abstract void run();

}
