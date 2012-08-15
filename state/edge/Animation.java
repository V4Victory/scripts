package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;

public class Animation extends AnimationPath {
	public Animation(Condition c, final Integer anim, State success, final Edge timeout) {
		super(c,new Integer[] { anim },success,timeout);
	}
}
