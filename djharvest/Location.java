package scripts.djharvest;

import java.util.Random;

import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class Location {

	public static int energyToTurnOn = 20;
	public static Random random = new Random();

	public static class PathNode {
		Tile tile = null;
		Integer id = null;
		String interaction = null;
		boolean useItem = false;
		int sleep = 1900;
		public PathNode(Tile tile_) {
			tile = tile_;
		}
		public PathNode(Integer id_,String interaction_) {
			id = id_;
			interaction = interaction_;
		}
		public static PathNode interactItem(Integer id_, String interaction_,int sleep_) {
			PathNode node = new PathNode(id_,interaction_);
			node.useItem = true;
			node.sleep = sleep_;
			return node;
		}
		public void run() {
			if(tile != null) {
				Timer timer = new Timer(15000);
				while(Players.getLocal().getLocation().distance(tile)>2 &&
						Players.getLocal().getLocation().distance(tile)<50 && timer.isRunning()) {
					Walking.walk(Walking.getClosestOnMap(tile));
					Time.sleep(700);
					while(Players.getLocal().isMoving()) Time.sleep(20);
				}
			} else if(useItem) {
				Item item = Inventory.getItem(id);
				if(item != null)
					item.getWidgetChild().interact(interaction);
				Time.sleep(sleep);
			} else {
				SceneObject obj = SceneEntities.getNearest(id);
				if (obj != null)
					obj.interact(interaction);
				Time.sleep(1900);
			}
		}
	}
	
	public static class Path {
		PathNode[] nodes;
		public Path(PathNode[] nodes_) {
			nodes = nodes_;
		}
		public void traverse() {
			for(PathNode node : nodes) node.run();
		}
	}

	public Location(String name_, Area area_, Option... tele_) {
		name = name_;
		area = area_;
		teleportOptions = tele_;
		if (teleportOptions.length > 0)
			selectedTeleportOption = teleportOptions[0];
	}

	public String name;
	public Area area;

	public Area getArea() {
		return area;
	}

	public String toString() {
		return name;
	}

	Option[] teleportOptions;

	public Option[] getTeleportOptions() {
		return teleportOptions;
	}

	public Option selectedTeleportOption = null;

	public static Option Disabled = new Option() {
		public String toString() {
			return "Disabled";
		}

		public void run() {
		}
	};

	public static Location getLocation(String name) {
		for (Location location : locations) {
			if (location.toString().equals(name))
				return location;
		}
		return null;
	}

	public boolean isActivated() {
		return selectedTeleportOption != Location.Disabled
				&& selectedTeleportOption != null;
	}

	public int countWork(final boolean diseasedToo) {
		return Patches.getPatches(new Filter<Patch>() {
			public boolean accept(Patch p) {
				if (!p.isActivated() || p.getLocation() != Location.this)
					return false;
				return p.isDead() || (diseasedToo && p.isDiseased())
						|| p.getProgress() == 1.0 || p.isEmpty();
			}
		}).size();
	}

	public static final Location[] locations = {
			new Location("Falador", new Area(new Tile(3048, 3314, 0), new Tile(
					3063, 3300, 0)), Disabled, new Option() {
				public String toString() {
					return "Cabbage port";
				}

				public void run() {
					Path path = new Path(new PathNode[] {
							new PathNode(new Tile(3052, 3299, 0)),
							new PathNode(7049,"Open"),
							new PathNode(new Tile(3056,3309,0))});
					
					Item ring = Inventory.getItem(19760);
					if (ring != null)
						ring.getWidgetChild().interact("Cabbage-port");
					Time.sleep(4300);
					path.traverse();
				}
			}),
			new Location("Catherby", new Area(new Tile(2800, 3471, 0),
					new Tile(2816, 3458, 0)), Disabled, 
					
			new Option() {
				public String toString() {
					return "Lunar: Catherby teleport";
				}

				public void run() {
					Path path = new Path(new PathNode[] {
							new PathNode(new Tile(2807, 3463, 0)) });

					Magic.cast(Magic.Lunar.TeleportCatherby);
					Time.sleep(1900);
					while (Players.getLocal().getAnimation() == 9606)
						Time.sleep(20);
					path.traverse();
				}
			}, new Option() {
				public String toString() {
					return "Lunar: Catherby group teleport";
				}

				public void run() {
					Magic.cast(Magic.Lunar.TeleGroupCatherby);

				}
			}

			),
			new Location("Ardougne", new Area(new Tile(2661, 3380, 0),
					new Tile(2672, 3369, 0)), Disabled, new Option() {
				public String toString() {
					return "Lunar: North-ardougne teleport";
				}

				public void run() {
					Path path = new Path(new PathNode[] {
							new PathNode(new Tile(2620, 3366, 0)),
							new PathNode(new Tile(2636, 3373, 0)), 
							new PathNode(new Tile(2652, 3381, 0)), 
							new PathNode(new Tile(2664, 3375, 0)) });
					
					Magic.cast(Magic.Lunar.TeleportNorthArdougne);
					Time.sleep(2500);
					path.traverse();
				}
			}),
			new Location("Morytania", new Area(new Tile(3594, 3533, 0),
					new Tile(3609, 3518, 0)), Disabled, new Option() {
				public String toString() {
					return "Ectovial";
				}

				public void run() {
					Path path = new Path(new PathNode[] {
							new PathNode(new Tile(3643, 3531, 0)),
							new PathNode(new Tile(3629, 3535, 0)), 
							new PathNode(new Tile(3611, 3535, 0)),
							new PathNode(new Tile(3603, 3527, 0)) });
					Item vial = Inventory.getItem(4251);
					Walking.walk(Players.getLocal().getLocation());
					DJHarvest.waitFor(new Condition() { public boolean validate() { return !Players.getLocal().isMoving(); }});
					if (vial != null)
						vial.getWidgetChild().interact("Empty");
					Time.sleep(700);
					DJHarvest.waitFor(new Condition() { public boolean validate() { return Inventory.getCount(4251)>0; }});
					path.traverse();
					
				}
			}),
			new Location("Trollheim", new Area(new Tile(2805, 3687, 0),
					new Tile(2820, 3673, 0)), Disabled, new Option() {
				public String toString() {
					return "Teletab";
				}

				public void run() {
					Path path = new Path(new PathNode[] {
							new PathNode(new Tile(2889,3669,0)),
							new PathNode(new Tile(2874,3663,0)),
							new PathNode(9304,"Climb"),
							new PathNode(new Tile(2858,3664,0)),
							new PathNode(9303,"Climb"),
							new PathNode(new Tile(2846,3668,0)),
							new PathNode(new Tile(2848,3687,0)),
							new PathNode(34395,"Enter"),
							new PathNode(new Tile(2836, 10069, 0)),
							new PathNode(new Tile(2839, 10057, 0)),
							new PathNode(3776,"Open"),
							new PathNode(new Tile(2832, 10066, 0)),
							new PathNode(new Tile(2831, 10076, 0)),
							new PathNode(18834,"Climb-up"),
							new PathNode(new Tile(2813, 3678, 0))});
					
					Item tab = Inventory.getItem(20175);
					if (tab != null)
						tab.getWidgetChild().interact("Break");
					Time.sleep(2100);
					path.traverse();
				}
			}, new Option() {
				public String toString() {
					return "Lunar: Trollheim teleport";
				}

				public void run() {
					Magic.cast(Magic.Lunar.TeleportTrollheim);
				}
			}, new Option() {
				public String toString() {
					return "Lunar: Trollheim group teleport";
				}

				public void run() {
					Magic.cast(Magic.Lunar.TeleGroupTrollheim);
				}
			}) };
	// public static final int y

}
