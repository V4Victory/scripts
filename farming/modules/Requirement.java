package scripts.farming.modules;

import org.powerbot.game.api.methods.tab.Inventory;

import scripts.farming.Equipment;
import scripts.state.Constant;
import scripts.state.Value;

public class Requirement {
	public int amount;
	public Value<Integer> id;

	public Requirement or_req = null;
	public Requirement and_req = null;

	boolean optional;
	
	public Requirement(int amount_, Value<Integer> id_) {
		this(amount_,id_,false);
	}

	public Requirement(int amount_, int id_) {
		this(amount_, new Constant<Integer>(id_), false);
	}
	
	public Requirement(int amount_, int id_, boolean optional_) {
		this(amount_, new Constant<Integer>(id_), optional_);
	}

	public Requirement(int amount_, Value<Integer> id_, boolean optional_) {
		amount = amount_;
		id = id_;
		optional = optional_;
	}

	public Requirement or(Requirement other) {
		if (other.or_req == null) {
			other.or_req = this;
			return other;
		} else { // or is associative
			return other.or_req.or(this);
		}
	}

	public Requirement and(Requirement other) {
		if (and_req == null) {
			and_req = other;
			return this;
		} else { // and is associative
			return and_req.and(other);
		}
	}

	public boolean validate() {
		if (id.get() == 0
				|| Inventory.getCount(id.get()) >= Math.max(1, amount)
				|| optional || Equipment.WEAPON.getEquipped() == id.get()) {
			return and_req != null ? and_req.validate() : true;
		} else if (or_req != null && or_req.validate()) {
			return true;
		}
		return false;
	}

	public String toString() {
		String s = "";
		if (or_req != null)
			s += "(";
		s += id.get() + "," + amount;
		if (or_req != null)
			s += "|" + or_req + ")";
		if (and_req != null)
			s += "&" + and_req;
		return s;
	}
}
