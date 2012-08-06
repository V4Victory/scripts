package scripts.farming.modules;

import state.Condition;
import state.Module;
import state.State;
import state.edge.Either;

public class CheckWorkload extends Module {
	public CheckWorkload(State initial, State success, State critical) {
		super(initial, success, critical);
		initial.add(new Either(new Condition() {
				public boolean validate() {
					return false; // work to do?!
				}
			}, success, critical));
	}
}
