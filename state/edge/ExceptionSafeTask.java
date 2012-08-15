package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;

public abstract class ExceptionSafeTask extends Either {
	public ExceptionSafeTask(final Condition c, State s, State onException) {
		super(c,s,onException);
		setCondition(c.and(new Condition() {
			public boolean validate() {
				try {
					ExceptionSafeTask.this.run();
					return true;
				} catch(Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}));

		setState(s);
	}

	public abstract void run() throws Exception;

}
