package scripts.farming;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;


/** taken from http://www.powerbot.org/community/topic/767552-getappearance/ **/

public enum Equipment {
	HELM(0, 6), BACK(1, 9), NECK(2, 12), WEAPON(3, 15), CHEST(4, 18), SHIELD(5,
			21), LEG(7, 24), HAND(9, 27), FEET(10, 30), RING(11, 33), AMMO(12,
			36);
	private final int index;
	private final int widget;
	private final int main = 387;

	private Equipment(final int index, final int widget) {
		this.index = index;
		this.widget = widget;
	}

	public int getEquipped() {
		return Players.getLocal().getAppearance()[getIndex()];
	}

	public boolean isEmpty() {
		return getEquipped() == -1;
	}

	public int getIndex() {
		return index;
	}

	public void interact(String action) {
		if (!Tabs.EQUIPMENT.isOpen()) {
			Tabs.EQUIPMENT.open();
		}
		Widgets.get(main, widget).interact(action);
	}
}