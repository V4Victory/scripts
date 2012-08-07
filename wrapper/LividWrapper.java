package scripts.wrapper;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import ox5377656c6c.livid.Constants;
import ox5377656c6c.livid.doLivid;
import scripts.farming.Magic;
import scripts.farming.ScriptWrapper;


@ScriptWrapper
public class LividWrapper {

	private static doLivid instance = null;
	public static ActiveScript getInstance() {
		if(instance == null) {
			instance = new doLivid();
		}
		return instance;
	}
	
	public static void prepare() {
		/* go to iLivid area */
		Item mudstaff = Inventory.getItem(6562);
		if (mudstaff != null)
			mudstaff.getWidgetChild().click(true);

		Magic.cast(Magic.Lunar.TeleportMoonClan.getWidgetId());
		Time.sleep(4300);
		Walking.walk(new Tile(2113,3929,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		Walking.walk(new Tile(2111,3939,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		Walking.walk(new Tile(2107,3945,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		//doLivid.RUN_TIME.reset();
	}

	public static boolean cleanup() {
		/* Destroy items so we have more space for herbs/.. */
		Item item = Inventory.getItem(Constants.LIVID_PLANT_SINGLE);
		if(item != null) {
			item.getWidgetChild().interact("Destroy");
			Time.sleep(1300);
			Widgets.get(1183,27).click(true);
			Time.sleep(1300);
		}
		
		item = Inventory.getItem(Constants.LIVID_PLANT_BUNDLE);
		if(item != null) {
			item.getWidgetChild().interact("Destroy");
			Time.sleep(1300);
			Widgets.get(1183,27).click(true);
			Time.sleep(1300);
		}
		
		item = Inventory.getItem(Constants.LUNAR_LOGS);
		if(item != null) {
			item.getWidgetChild().interact("Destroy");
			Time.sleep(1300);
			Widgets.get(1183,27).click(true);
			Time.sleep(1300);
		}
		
		item = Inventory.getItem(Constants.LUNAR_PLANK);
		if(item != null) {
			item.getWidgetChild().interact("Destroy");
			Time.sleep(1300);
			Widgets.get(1183,27).click(true);
			Time.sleep(1300);
		}
		if(Widgets.get(1081,0).isOnScreen()) {
			Mouse.click(Players.getLocal().getCentralPoint(),true);
		}
		Camera.setPitch(89);
		Time.sleep(1300);
		return false;
	}


}