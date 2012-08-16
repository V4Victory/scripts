package scripts.wrapper;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Time;

import scripts.Cockatrice;
import scripts.farming.ScriptWrapper;

@ScriptWrapper(banking = true)
public class CockatriceWrapper {

	private static Cockatrice instance = null;

	public static ActiveScript getInstance() {
		if (instance == null) {
			instance = new Cockatrice();
		}
		return instance;
	}

	public static void prepare() {
		Bank.open();
		Time.sleep(1300, 1500);
		Bank.withdraw(12436, 0);
		Time.sleep(1300, 1500);
		Bank.withdraw(12140, 5);
		Time.sleep(1300, 1500);
		Bank.close();
	}

	public static void cleanup() {
		System.out.println("Cocka cleanup!");
		while (Inventory.getCount() > 0) {
			while (!Bank.open())
				Time.sleep(10);
			System.out.println("Bank Opened");
			Time.sleep(1300, 1500);
			Bank.depositInventory();
			Time.sleep(1300, 1500);
			System.out.println("Deposited");
		}
		Bank.close();
	}

}