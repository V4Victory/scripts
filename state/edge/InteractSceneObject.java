package scripts.state.edge;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.Value;

public class InteractSceneObject extends Task {

	Value<SceneObject> object;
	String interaction;

	public InteractSceneObject(Condition c, State s,
			final Value<SceneObject> object_, String interaction_) {
		this(c, s, object_, interaction_, false);
	}

	public InteractSceneObject(final Condition c, State s,
			final Value<SceneObject> object_, String interaction_,
			final boolean turnCamera) {
		super(null, s);
		object = object_;
		interaction = interaction_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				if (turnCamera)
					Camera.turnTo(object.get());
				return object.get() != null && object.get().isOnScreen();
			}
		}));
	}

	@Override
	public void run() {
		System.out.println("Interact scene object : " + object.get().getId() + "/" + interaction + "/" + object.get().isOnScreen());
		Mouse.move(object.get().getCentralPoint());
		Time.sleep(100);
		Mouse.click(false);
		Time.sleep(100);
		if(!Menu.isOpen()) {
			Time.sleep(100);
			Mouse.click(false);
			Time.sleep(300);
		}
		if(!Menu.select(interaction)) {
			Time.sleep(300);
			object.get().getModel().interact(interaction);	
		}
	}
}
