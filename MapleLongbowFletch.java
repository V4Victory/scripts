package scripts;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Time;

@Manifest(authors = { "djabby" }, name = "MapleLongbowFletch", description = "Crafts maple longbow", version = 1.00)
public class MapleLongbowFletch extends ActiveScript {

	@Override
	protected void setup() {
		FletchBows fb = new FletchBows();
		provide(new Strategy(fb,fb));

		Withdraw w = new Withdraw();
		provide(new Strategy(w,w));

	}
	
	public class Withdraw extends Strategy implements Condition, Task {

		@Override
		public void run() {
			Bank.open();
			Time.sleep(1300);
			Bank.depositInventory();
			Bank.withdraw(1517, 28);
			Time.sleep(1300);
			Bank.close();
			Time.sleep(700);
		}
		
		public boolean validate() {
			return Inventory.getCount(1517) == 0;
		}
		
	}
	
	public class FletchBows extends Strategy implements Condition, Task {
		boolean isRunning = false;

		@Override
		public void run() {
			isRunning = true;
			Inventory.getItem(1517).getWidgetChild().interact("Craft");
			Time.sleep(1300);
			if(Widgets.get(1179,12).isOnScreen()) {
				Widgets.get(1179,12).click(true);
				Time.sleep(1300);
			}
			Widgets.get(905,15).click(true);
			Time.sleep(700);
			isRunning = false;
		}
		
		public boolean validate() {
			return !isRunning && Inventory.getCount(1517) > 0 && Players.getLocal().getAnimation() != 1248;
		}
		
	}

}