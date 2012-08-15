package scripts.state;

import scripts.farming.modules.Requirement;
import scripts.state.edge.Edge;
import scripts.state.edge.Task;

public class SharedModule extends Module {
	State entered = null;

	public SharedModule(String description_, State initial_, State success_,
			State critical_) {
		this(description_, initial_, success_, critical_, new Requirement[] {});
	}

	public SharedModule(String description_, State initial_, State success_,
			State critical_, Requirement[] requirements_) {
		super(description_, initial_, success_, critical_, requirements_);
	}

	public State addSharedStates(final State init,State succ) {
		init.add(new Task(Condition.TRUE, initial) {
			public void run() {
				entered = init;
			}
		});
		success.add(new Edge(new Condition() {
			public boolean validate() {
				return entered == init;
			}
		}, succ));
		return init;
	}
}
