package scripts.farming;

import scripts.farming.Magic.Spellbook;

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
}