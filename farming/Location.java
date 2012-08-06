package scripts.farming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.modules.RunMorytania;
import state.Module;
import state.State;

public class Location {

	public Location(String name_, Area area_, ModuleCreator moduleCreator_) {
		name = name_;
		area = area_;
		moduleCreator = moduleCreator_;
	}

	String name;
	Area area;
	ModuleCreator moduleCreator;

	public Area getArea() {
		return area;
	}

	public String toString() {
		return name;
	}

	public Set<Module> getTeleportOptions() {
		if (teleportOptions == null) {
			return teleportOptions = new HashSet<Module>(
					Arrays.asList(moduleCreator.createModules(new State(),
							new State(), new State())));
		} else {
			return teleportOptions;
		}
	}

	Set<Module> teleportOptions = null;
	public Module selectedTeleportOption = null;

	public List<Patch> getPatches() {
		List<Patch> patches = new ArrayList<Patch>();
		for (Patch patch : Patches.patches.values()) {
			if (patch.getLocation() == this)
				patches.add(patch);
		}
		return patches;
	}

	public static Location getLocation(String name) {
		for (Location location : locations) {
			if (location.toString().equals(name))
				return location;
		}
		return null;
	}

	public boolean activated = true;

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
			new Location("Morytania", new Area(new Tile(3594, 3533, 0),
					new Tile(3609, 3518, 0)), new ModuleCreator() {
				public Module[] createModules(State i, State s, State c) {
					return new Module[] { new RunMorytania(i, s, c) };
				}
			}),
			new Location("Falador", new Area(new Tile(3048, 3314, 0), new Tile(
					3063, 3300, 0)), new ModuleCreator() {
				public Module[] createModules(State i, State s, State c) {
					return new Module[] {};
				}
			}),
			new Location("Ardougne", new Area(new Tile(3048, 3314, 0),
					new Tile(3063, 3300, 0)), new ModuleCreator() {
				public Module[] createModules(State i, State s, State c) {
					return new Module[] {};
				}
			}),
			new Location("Catherby", new Area(new Tile(3048, 3314, 0),
					new Tile(3063, 3300, 0)), new ModuleCreator() {
				public Module[] createModules(State i, State s, State c) {
					return new Module[] {};
				}
			}),
			new Location("Trollheim", new Area(new Tile(3048, 3314, 0),
					new Tile(3063, 3300, 0)), new ModuleCreator() {
				public Module[] createModules(State i, State s, State c) {
					return new Module[] {};
				}
			}) };
}
