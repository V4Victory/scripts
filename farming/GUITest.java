package scripts.farming;

import java.io.File;

import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;

public class GUITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GUI gui = new GUI(new File("farming-settings.ini"));
		while(!gui.isDone()) Time.sleep(100);
	}

}
