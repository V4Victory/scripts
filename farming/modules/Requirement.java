package scripts.farming.modules;

import scripts.state.Constant;
import scripts.state.Value;

public class Requirement {
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

	
	public String toString() {
		String s = "";
		if(or_req != null) s += "(";
		s += id.get()+","+amount;
		if(or_req != null) s += "|"+or_req + ")";
		if(and_req != null) s+= "&"+and_req;
		return s;
	}
}
