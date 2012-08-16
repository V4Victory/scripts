package scripts.state.edge;

import scripts.farming.Equipment;
import scripts.state.Condition;
import scripts.state.State;

public class Equip extends InteractItem {
	public Equip(Condition c, State s, final int id, final Equipment equip, Edge timeout) {
		super(c,new State().add(new Edge(new Condition() {
			public boolean validate() {
				return equip.getEquipped() == id;
			}
		}, s)).add(timeout),id,"Wield");
	}
}
