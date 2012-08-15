package scripts.state.edge;

import scripts.farming.Magic;
import scripts.farming.Spell;
import scripts.state.Condition;
import scripts.state.State;


public class MagicCast extends Task {
	
	Spell spell;
	
	public MagicCast(Condition c, State s, State f, final Spell spell_) {
		super(c.and(spell_.getCondition()), 
				new State().add(new Animation(Condition.TRUE, spell_.getAnimation(), s, new Timeout(f,5000))));
		spell = spell_;
	}

	public void run() {
		Magic.cast(spell.getWidgetId());
	}
}
