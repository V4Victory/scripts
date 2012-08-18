package scripts.state.edge;

import org.powerbot.game.api.methods.interactive.Players;

import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.State;
import scripts.state.StateCreator;

public class AnimationPath extends Edge {
	public AnimationPath(Condition c, final Integer[] anims, State success,
			final Edge timeout) {
		super(new Condition() {
			public boolean validate() {
				System.out.println("Current animation: "
						+ Players.getLocal().getAnimation() + " / "
						+ (anims.length != 0 ? anims[0] : ""));
				return (anims.length == 0
						|| Players.getLocal().getAnimation() == anims[0] || (Players
						.getLocal().getAnimation() == -1 && anims[0] == 0));
			}
		}, new ConsecutiveState<Integer>(anims, success,
				new StateCreator<Integer>() {
			public State getState(final Integer anim,
					State nextState) {
				if (anim == 0) {
					return new State("animp").add(new Edge(
							Condition.TRUE, nextState));
				}
				return new State("<anim " + anim).add(
						new Edge(new Condition() {
							public boolean validate() {
								if (Players.getLocal()
										.getAnimation() != anim)
									System.out
											.println("Current animation: "
													+ Players
															.getLocal()
															.getAnimation());
								return Players.getLocal()
										.getAnimation() != anim;
							}
						}, nextState)).add(timeout);
			}
		}));
	}
}
