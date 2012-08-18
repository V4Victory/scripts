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


@ScriptWrapper(banking = true)
public class FletchWrapper {

	private static MapleLongbowFletch instance = null;
	public static ActiveScript getInstance() {
		if(instance == null) {
			instance = new MapleLongbowFletch();
		}
		return instance;
	}
	
	public static void prepare() {
		Bank.open();
		Bank.depositInventory();
	}

	public static void cleanup() {
		Bank.open();
		Time.sleep(1300,1500);
		Bank.depositInventory();
	}


}