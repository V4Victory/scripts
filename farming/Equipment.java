package scripts.farming;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

/**
 * Code taken from
 * http://www.powerbot.org/community/topic/673668-tabsequipmentopen/
 **/

// Structure taken from Caleb
public enum Equipment {

	// updated tab IDs to get child IDs
	HELM(6), NECK(12), CAPE(9), ARROW(36), WEAPON(15), SHIELD(21), BODY(18), LEGS(
			24), HANDS(27), BOOTS(30), RING(33), AURA(45);

	private int widgetId;
	private final int WIDGET = 387;

	Equipment(final int widgetId) {
		this.widgetId = widgetId;
	}

	public boolean interact(final String action) {
		Tabs.EQUIPMENT.open();
		Time.sleep(50, 200);
		final WidgetChild widget = getSlotWidget();
		if (widget.validate()) {
			return widget.interact(action);
		}
		return false;
	}

	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// Written by Caleb
	// ----------------------------------------------------------------------

	public WidgetChild getSlotWidget() {
		Tabs.EQUIPMENT.open();
		Time.sleep(50, 200);
		return Widgets.get(WIDGET, widgetId);
	}

	public int getItemId() {
		final WidgetChild widget = getSlotWidget();
		return widget.getChildId();
	}

	public int getStackSize() {
		final WidgetChild slot = getSlotWidget();
		return slot.getChildStackSize();
	}

	public String getItemName() {
		final WidgetChild slot = getSlotWidget();
		String name = slot.getChildName();
		return ((name.contains("<")) ? name.substring(name.indexOf(">") + 1)
				: slot.getChildName());
	}
}