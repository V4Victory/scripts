package scripts.state.edge;

import org.powerbot.game.api.methods.interactive.Players;

import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.State;
import scripts.state.StateCreator;

public class AnimationPath extends Edge {
	public AnimationPath(Condition c, final Integer[] anims, State success, final Edge timeout) {
		super(c, new ConsecutiveState<Integer>(anims, success, new StateCreator<Integer>() {
			public State getState(final Integer anim, State nextState) {
				if(anim == 0) {
					return new State().add(new Edge(Condition.TRUE,nextState));
				}
				Condition cond = new Condition() {
					public boolean validate() {
						return Players.getLocal().getAnimation() == anim ;
					}};
				return new State()
				.add(new Edge(cond, new State().add(new Edge(cond.negate(), nextState)).add(timeout)))
				.add(timeout);
			}
		}));	
	}
}
