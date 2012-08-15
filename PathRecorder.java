package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "djabby" }, name = "PathRecorder", description = "s=start/stop,w=write,r=read,r=run", version = 1.37)
public class PathRecorder extends ActiveScript implements KeyListener,
		PaintListener {

	public class Path {
		List<Tile> nodes;
		int i;

		public Path(List<Tile> nodes_) {
			nodes = nodes_;
			i = 0;
		}

		public boolean isFinished() {
			return i >= nodes.size();
		}

		public void run() {
			Tile current = nodes.get(i);
			if (!tilesEqual(current, Walking.getDestination()))
				Walking.walk(current);
			if (current.distance(Players.getLocal().getLocation()) <= inRange)
				i++;
		}
	}

	Path path = null;
	List<Tile> currentPath;
	Tile getLatest = null;

	enum State {
		NIL, COLLECT, STOPPED, RUNNING
	};

	int minimalDistance = 7;
	int inRange = 6;
	State state = State.NIL;

	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 's':
			switch (state) {
			case NIL:
			case STOPPED:
				currentPath = new ArrayList<Tile>();
				state = State.COLLECT;
				break;
			case COLLECT:
			case RUNNING:
				state = State.STOPPED;
				getLatest = null;
				path = null;
				break;
			}
			break;
		case 'w':
			if (state != State.NIL) {
				System.out.println("new Tile[] {");
				boolean b = false;
				for (Tile tile : currentPath) {
					System.out.print(b ? "," : "");
					System.out.println("new Tile(" + tile.getX() + ","
							+ tile.getY() + "," + tile.getPlane() + ")");
					b = true;
				}
				System.out.println("};");
			}
			break;
		case 'r':
			if (state == State.STOPPED) {
				state = State.RUNNING;
				path = new Path(currentPath);
			}
		}

	}

	public void setup() {
		provide(new Collect());
		provide(new Running());
	}

	public class Collect extends Strategy implements Task, Condition {
		public void run() {
			if (currentPath.size() == 0) {
				getLatest = Players.getLocal().getLocation();
				currentPath.add(getLatest);
			} else if (!tilesEqual(Players.getLocal().getLocation(), getLatest)
					&& getLatest.distance(Players.getLocal().getLocation()) > minimalDistance) {
				getLatest = Players.getLocal().getLocation();
				currentPath.add(getLatest);
			}
		}

		public boolean validate() {
			return state == State.COLLECT;
		}
	}

	public class Running extends Strategy implements Task, Condition {
		public void run() {
			path.run();
		}

		public boolean validate() {
			return state == State.RUNNING && !path.isFinished();
		}
	}

	public static boolean tilesEqual(Tile tile1, Tile tile2) {
		return tile1.getX() == tile2.getX() && tile1.getY() == tile2.getY()
				&& tile1.getPlane() == tile2.getPlane();
	}

	@Override
	public void onRepaint(Graphics g) {
		if (state == State.NIL)
			return;
		for (Tile t : currentPath) {

			Polygon[] bounds = t.getBounds();
			if (bounds.length == 1) {
				g.setColor(Color.RED);
				g.fillPolygon(bounds[0]);

			}
		}
	}

}
