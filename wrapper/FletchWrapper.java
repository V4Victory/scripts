package scripts.wrapper;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;

import scripts.MapleLongbowFletch;
import scripts.farming.Magic;
import scripts.farming.ScriptWrapper;


@ScriptWrapper
public class FletchWrapper {

	private static MapleLongbowFletch instance = null;
	public static ActiveScript getInstance() {
		if(instance == null) {
			instance = new MapleLongbowFletch();
		}
		return instance;
	}
	
	public static void prepare() {
		Magic.cast(Magic.Lunar.TeleportMoonClan.getWidgetId());
		Time.sleep(4300);
		Walking.walk(new Tile(2098,3919,0));
		Time.sleep(1300);
		while(Players.getLocal().isMoving()) Time.sleep(20);
		Bank.open();
		Bank.depositInventory();
	}

	public static boolean cleanup() {
		Bank.open();
		Time.sleep(1300,1500);
		Bank.depositInventory();
		Time.sleep(1300,1500);
		Bank.close();
		return true;
	}


}