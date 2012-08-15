package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "djabby" }, name = "Cockatrice", description = "Eggs->Cockatrice Eggs", version = 1.00)
public class Cockatrice extends ActiveScript implements PaintListener {

	Timer timer = new Timer(0);
	int count = 0;
	int lastcount = 0;

	@Override
	protected void setup() {
		provide(new Withdraw());
		provide(new Summon());
		provide(new Drink());
		provide(new Produce());

	}

	public class Withdraw extends Strategy implements Task {
		public Withdraw() {
			setSync(true);
			setLock(true);
		}


		public void run() {
			while(!Bank.open());
			Time.sleep(1500);
			if (Inventory.getCount(229) > 2) {
				Bank.deposit(229, 0);
				Time.sleep(700);
				Bank.withdraw(12140, 5);
				Time.sleep(700);
			}
			while (Inventory.getCount(12109) > 0) {
				Bank.deposit(12109, 0);
				Time.sleep(700);
			}

			while (Inventory.getCount(1944) == 0) {
				Bank.withdraw(1944, 0);
				Time.sleep(700);
			}
			Bank.close();
			lastcount = count;
		}

		public boolean validate() {
			return Inventory.getCount(1944) == 0;
		}

	}

	public class Summon extends Strategy implements Task {
		public Summon() {
			setSync(true);
			setLock(true);
		}


		public void run() {
			if (Inventory.getCount(12015) == 0) {
				Bank.open();
				Time.sleep(700);
				while (Inventory.getCount(12109) > 0) {
					Bank.deposit(12109, 0);
					Time.sleep(700);
				}
				while (Inventory.getCount(1944) > 0) {
					Bank.deposit(1944, 0);
					Time.sleep(700);
				}
				while (Inventory.getCount(12015) == 0) {
					Bank.withdraw(12015, 1);
					Time.sleep(700);
				}
				Bank.close();
			}
			while (Widgets.get(747, 0).getTextureId() == 1244) {
				Item item = Inventory.getItem(12015);
				if (item != null) {
					item.getWidgetChild().interact("Summon");
				}
				Time.sleep(100);
			}
		}

		public boolean validate() {
			return Widgets.get(747, 0).getTextureId() == 1244;
		}
	}

	public class Drink extends Strategy implements Task {
		public Drink() {
			setSync(true);
			setLock(true);
		}


		public void run() {
			Item item = Inventory.getItem(12146, 12144, 12142, 12140);
			if (item != null) {
				item.getWidgetChild().interact("Drink");
			}
		}

		public boolean validate() {
			return Settings.get(1177) < 30;
		}
	}

	public class Produce extends Strategy implements Task {
		public Produce() {
			setSync(true);
			setLock(true);
		}


		public void run() {
			Point p = Widgets.get(747, 2).getCentralPoint();
			Mouse.hop((int) p.getX(), (int) p.getY());
			Mouse.click(true);
			Time.sleep(80);
			p = Inventory.getItem(1944).getWidgetChild().getCentralPoint();
			Mouse.hop((int) p.getX(), (int) p.getY());
			Mouse.click(true);
			Time.sleep(80);
			p = Widgets.get(747, 2).getCentralPoint();
			Mouse.hop((int) p.getX(), (int) p.getY());

			count = lastcount + Inventory.getCount(12109);
		}

		public boolean validate() {
			return Inventory.getCount(1944) > 0 && Settings.get(1177) > 15;
		}

	}

	@Override
	public void onRepaint(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(5, 5, 110, 35);
		g.setColor(Color.BLACK);
		g.drawString("Time: " + timer.toElapsedString(), 7, 18);
		g.drawString("Eggs: " + count, 7, 33);
	}

}