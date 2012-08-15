package scripts.state.edge;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.state.Condition;
import scripts.state.State;

public class InteractItem extends Task {
	
	int id;
	String interaction;
	
	public InteractItem(Condition c, State s, final int id_, String interaction_) {
		super(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getCount(id_) > 0;
			}
		}), s);
		id = id_;
		interaction = interaction_;
	}

	@Override
	public void run() {
		System.out.println("Interact with " + id);
		Item item = Inventory.getItem(id);
		if (item != null)
			item.getWidgetChild().interact(interaction);
		Time.sleep(700);
	}
}
