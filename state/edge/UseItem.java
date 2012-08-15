package scripts.state.edge;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.state.Condition;
import scripts.state.Constant;
import scripts.state.State;
import scripts.state.Value;

public class UseItem extends Task {
	
	Value<Integer> id;
	Value<Integer> id2 = null;
	
	public UseItem(Condition c, State s, final Integer id_) {
		this(c,s,new Constant<Integer>(id_));
	}
	public UseItem(Condition c, State s, final Value<Integer> id_) {
		super(null, s);
		id = id_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getItem(id.get()) != null;
			}
		}));
	}
	
	public UseItem(Condition c, State s, final Integer id_, Value<Integer> id2_) {
		this(c,s,new Constant<Integer>(id_),id2_);
	}
	public UseItem(Condition c, State s, final Value<Integer> id_, Integer id2_) {
		this(c,s,id_,new Constant<Integer>(id2_));
	}
	public UseItem(Condition c, State s, final Integer id_, Integer id2_) {
		this(c,s,new Constant<Integer>(id_),new Constant<Integer>(id2_));
	}
	public UseItem(Condition c, State s, final Value<Integer> id_, Value<Integer> id2_) {
		super(null, s);
		id = id_;
		id2 = id2_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getItem(id.get()) != null && Inventory.getItem(id2.get()) != null;
			}
		}));
	}


	@Override
	public void run() {
		Item item = Inventory.getItem(id.get());
		if (item != null)
			item.getWidgetChild().interact("Use");
		if(id2 != null) {
			item = Inventory.getItem(id2.get());
			if (item != null)
				item.getWidgetChild().click(true);
		}
	}
}
