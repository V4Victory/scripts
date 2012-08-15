package scripts.state.edge;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.state.Condition;
import scripts.state.Constant;
import scripts.state.State;
import scripts.state.Value;

public class UseItemWithSceneObject extends Task {
	
	Value<Integer> id;
	Value<SceneObject> object = null;
	
	public UseItemWithSceneObject(Condition c, State s, Integer id_, final Value<SceneObject> object_) {
		this(c,s,id_,object_,false);
	}
	public UseItemWithSceneObject(Condition c, State s, Integer id_, final Value<SceneObject> object_, 
			final boolean turnCamera) {
		this(c,s,new Constant<Integer>(id_),object_,turnCamera);
	}
	public UseItemWithSceneObject(Condition c, State s, Value<Integer> id_, final Value<SceneObject> object_) {
		this(c,s,id_,object_,false);
	}
	public UseItemWithSceneObject(Condition c, State s, Value<Integer> id_, final Value<SceneObject> object_, boolean turnCamera) {
		super(null,s);
		id = id_;
		object = object_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getItem(id.get()) != null && object.get().isOnScreen();
			}
		}));
	}
	

	@Override
	public void run() {
		Item item = Inventory.getItem(id.get());
		if (item != null)
			item.getWidgetChild().interact("Use");
		if(object != null) {
			while(Mouse.getLocation().distance(object.get().getCentralPoint())>3)
				Mouse.move(object.get().getCentralPoint());
			Mouse.click(false);
			if(!Menu.select("Use")) {
				Time.sleep(100);
				object.get().getModel().interact("Use");	
			}
		}
	}
}
