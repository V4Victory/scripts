package scripts.state.edge;

import org.powerbot.game.api.methods.tab.Skills;

import scripts.farming.Equipment;
import scripts.farming.Magic;
import scripts.farming.Spell;
import scripts.farming.modules.Constants;
import scripts.state.Condition;
import scripts.state.State;
import scripts.state.edge.Task;

public class MagicCast extends Edge {

	Spell spell;

	public MagicCast(Condition c, State s, State f, final Spell spell_) {
		super(c.and(new Condition() {
			public boolean validate() {
				return Skills.getLevel(Skills.MAGIC) >= spell_.getMagicLevel()
						&& Magic.getCurrentSpellbook() == spell_.getSpellbook();
			}
		}), new State());
		state.add(new Equip(Condition.TRUE, state, Constants.MudBattleStaff,
				Equipment.WEAPON, new Timeout(f, 3000)));
		State casted = new State();
		state.add(new Task(Condition.TRUE, casted) {
			public void run() {
				Magic.cast(spell.getWidgetId());
			}
		});
		casted.add(new Animation(Condition.TRUE, spell_.getAnimation(), s,
				new Timeout(f, 5000)));
		setState(state);
		spell = spell_;
	}
}
