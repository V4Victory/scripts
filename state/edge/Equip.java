package scripts.state.edge;

import org.powerbot.game.api.methods.tab.Inventory;

import scripts.farming.Equipment;
import scripts.state.Condition;
import scripts.state.State;

public class Equip extends Edge {
	public Equip(Condition c, State s, State f, final int id, final Equipment equip, Timeout timeout) {
		super(c,new State("EQ1"));
		state.add(new Edge(new Condition() {
			public boolean validate() {
				return equip.getEquipped() == id;
			}
		}, s)).add(timeout);
		state.add(new Edge(new Condition() {
			public boolean validate() {
				return Inventory.getCount(id) == 0;
			}
		},s));
		state.add(new InteractItem(Condition.TRUE,new State(state).add(new Timeout(f,timeout)),id,"Wield"));
		state.add(timeout);
		
	}
}
