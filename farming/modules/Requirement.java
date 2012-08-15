package scripts.farming.modules;

import org.powerbot.game.api.methods.tab.Inventory;

import state.Condition;
import state.Constant;
import state.Value;

public class Requirement extends Condition {
	public int amount;
	public Value<Integer> id;
	
	public Requirement or_req = null;
	public Requirement and_req = null;
	
	public Requirement(int amount_, int id_) {
		this(amount_, new Constant<Integer>(id_));
	}
	public Requirement(int amount_, Value<Integer> id_) {
		amount = amount_;
		id = id_;
	}
	
	public Requirement or(Requirement other) {
		if(other.or_req == null) {
			other.or_req = this;
			return other;
		} else { // or is associative
			return other.or_req.or(this);
		}
	}
	
	public Requirement and(Requirement other) {
		if(and_req == null) {
			and_req = other;
			return this;
		} else { // and is associative
			return and_req.and(other);
		}
	}
	
	public boolean validate() {
		if(id.get() == 0 || Inventory.getCount(id.get()) >= Math.max(1, amount)) {
			return and_req != null ? and_req.validate() : true;
		} else if(or_req != null && or_req.validate()) {
			return true;
		}
		return false;
	}
}
