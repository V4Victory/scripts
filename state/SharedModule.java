package scripts.state;

import scripts.farming.modules.Requirement;
import scripts.state.edge.Edge;
import scripts.state.edge.Task;

public class SharedModule extends Module {
	State entered = null;
	protected Object intermediateValue = null;

	public SharedModule(String description_, State initial_, State success_,
			State critical_) {
		this(description_, initial_, success_, critical_, new Requirement[] {});
	}

	public SharedModule(String description_, State initial_, State success_,
			State critical_, Requirement[] requirements_) {
		super(description_, initial_, success_, critical_, requirements_);
	}

	public <T> State addSharedStates(final State init,State succ, final T val, final T reset) {
		init.add(new Task(Condition.TRUE, initial) {
			public void run() {
				entered = init;
				intermediateValue = val;
			}
		});
		success.add(new Edge(new Condition() {
			public boolean validate() {
				intermediateValue = reset;
				return entered == init;
			}
		}, succ));
		return init;
	}
}
