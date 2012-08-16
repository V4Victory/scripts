package scripts.farming;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;

import scripts.farming.modules.Requirement;
import scripts.farming.modules.Target;
import scripts.state.Condition;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.edge.Edge;

public class Location {

	public Location(String name_, Area area_) {
		name = name_;
		area = area_;
	}

	String name;
	Area area;
	Module module = null;

	public Area getArea() {
		return area;
	}

	public String toString() {
		return name;
	}

	// m works at this location
	public void setModule(Module m) {
		module = m;
	}

	public Module getModule() {
		return module;
	}

	public boolean isBank() {
		return Location.this.area == null;
	}

	public Set<Module> getTeleportOptions() {
		if (teleportOptions == null) {
			teleportOptions = new HashSet<Module>();
			Set<Class<?>> teleportModules = ClassHelper.getAnnotatedClasses(
					"scripts.farming.modules", Target.class);
			for (Class<?> clazz : teleportModules) {
				if (clazz.getAnnotation(Target.class).value().equals(name)) {
					try {
						Constructor<?> create = clazz.getDeclaredConstructor(
								State.class, State.class, State.class);
						State initial = new State();
						State success = new State();
						initial.add(new Edge(new Condition() {
							public boolean validate() {
								return !isBank()
										&& Location.this.area.contains(Players
												.getLocal());
							}
						}, success));
						Module mod = (Module) create.newInstance(initial,
								success, new State());
						teleportOptions.add(mod);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return teleportOptions;
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

	public List<Patch> getActivePatches() {
		List<Patch> patches = new ArrayList<Patch>();
		for (Patch patch : Patches.patches.values()) {
			if (patch.getLocation() == this && patch.activated)
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
		int count = 0;
		for (Patch p : getPatches()) {
			if (p.activated
					&& ((diseasedToo && p.isDiseased()) || p.isDead()
							|| p.getProgress() >= 1.0 || p.isEmpty())) {
				count += Patches.getTypeRating(p.getType());
			}
		}
		return count;
	}

	public static final Location[] locations = {
			new Location("Bank", null),
			new Location("Morytania", new Area(new Tile(3594, 3533, 0),
					new Tile(3609, 3518, 0))),
			new Location("Falador", new Area(new Tile(3048, 3314, 0), new Tile(
					3063, 3300, 0))),
			new Location("Ardougne", new Area(new Tile(2661, 3380, 0),
					new Tile(2672, 3369, 0))),
			new Location("Catherby", new Area(new Tile(2800, 3471, 0),
					new Tile(2816, 3458, 0))),
			new Location("Trollheim", new Area(new Tile(2805, 3687, 0),
					new Tile(2820, 3673, 0))) };

	public boolean checkRequirements() {
		if(module == null) return true;
		Requirement[] reqs = module.getRequirements();
		for(Requirement req : reqs) if(!req.validate()) return false;
		return true;
	}
}
