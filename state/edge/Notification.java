package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;

public class Notification extends Task {
	String msg;
	public Notification(Condition c, State s, String msg_) {
		super(c,s);
		msg = msg_;
	}


	public void run() {
		System.out.println(msg);
	}
}
