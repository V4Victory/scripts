package scripts.farming;

import org.powerbot.game.api.methods.tab.Skills;

import scripts.farming.Magic.Spellbook;
import state.Condition;

public class Spell {
	String name;
	int widget;
	Spellbook book;
	int magiclvl;
	public Spell(String name_, int widget_, Spellbook book_, int magiclvl_) {
		name = name_;
		widget = widget_;
		book = book_;
		magiclvl = magiclvl_;
	}
	public String getName() { return name; }
	public int getWidgetId() { return widget; }
	public Spellbook getSpellbook() { return book; }
	public int getMagicLevel() { return magiclvl; }
	
	public Condition getCondition() {
		return new Condition() {
			@Override
			public boolean validate() {
				return Skills.getLevel(Skills.MAGIC) >= magiclvl && Magic.getCurrentSpellbook() == book;
			}
		};
	}
	
	public int getAnimation() {
		return 0;
	}
}