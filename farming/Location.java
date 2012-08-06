package scripts.farming;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Area;

import state.Module;

public class Location {

	public Location(String name_, Area area_, Module... tele_) {
		name = name_;
		area = area_;
		teleportOptions = tele_;
		if (teleportOptions.length > 0)
			selectedTeleportOption = teleportOptions[0];
	}

	String name;
	Area area;

	public Area getArea() {
		return area;
	}

	public String toString() {
		return name;
	}

	Module[] teleportOptions;

	public Module[] getTeleportOptions() {
		return teleportOptions;
	}

	public Module selectedTeleportOption = null;

	public static Option Disabled = new Option() {
		public String toString() {
			return "Disabled";
		}

		public void run() {
		}
	};
	
	public List<Patch> getPatches() {
		List<Patch> patches = new ArrayList<Patch>();
		for(Patch patch : Patches.patches.values()) {
			if(patch.getLocation() == this) patches.add(patch);
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

	public static final Location[] locations = {};
	// public static final int y

}
