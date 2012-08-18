package scripts.farming;

import java.util.Map;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.farming.modules.Requirement;
import scripts.state.Value;

public class Patch {

	public SceneObject getSceneObject() {
		return SceneEntities.getNearest(id);
	}

	public String getHarvestingInteraction() {
		switch (getType()) {
		case Patches.Herb:
		case Patches.Flower:
			return "Pick";
		case Patches.Allotment:
			return "Harvest";
		default:
			return "";
		}
	}

	private int getState() {
		return Settings.get(setting, shift, 0xff);
	}

	public boolean activated = true;

	public boolean isDead() {
		if (getType() == Patches.Herb) {
			if (getState() >= 0xc0 && getState() <= 0xcb) {
				return getState() >= 0xc9 && getState() <= 0xcb;
			} else if (getState() >= 0xcc && getState() <= 0xd7) {
				return getState() >= 0xd5 && getState() <= 0xd7;
			} else {
				return getState() >= 0xaa && getState() <= 0xac;
			}
		} else if (getType() == Patches.Tree) {
			return (getState() & 0xc0) == 0x80;
		} else {
			return (getState() & 0xc0) == 0xc0;
		}
	}

	public int countWeeds() {
		return getState() < 4 ? 3 - (getState() & 0x3) : 0;
	}

	public double getProgress() {
		int state = getState() & ((getType() == Patches.Herb) ? 0x7f : 0x3f);
		Seed seed = getCorrespondingSeed();
		if (seed == null)
			return 0;
		if (seed.getMid() - seed.getLow() == 0)
			return 0;
		return (double) (state - seed.getLow())
				/ (seed.getMid() - seed.getLow());
	}

	public boolean isEmpty() {
		return (getState() < 4);
	}

	public boolean isGrowing() {
		return !isDead() && !isDiseased() && !isEmpty() && getProgress() < 1.0;
	}

	public boolean isDiseased() {
		if (getType() == Patches.Herb) {
			if (getState() >= 0xc0 && getState() <= 0xcb) {
				return getState() >= 0xc6 && getState() <= 0xc8;
			} else if (getState() >= 0xcc && getState() <= 0xd7) {
				return getState() >= 0xd2 && getState() <= 0xd4;
			} else {
				return (getState() & 0x80) == 0x80 && !isDead();
			}
		} else if (getType() == Patches.Tree) {
			return (getState() & 0xc0) == 0x40;
		} else {
			return (getState() & 0xc0) == 0x80;
		}
	}

	public boolean isWatered() {
		return getType() == Patches.Herb ? false : (getState() & 0xc0) == 0x40;
	}

	public boolean useSecateurs() {
		return getType() == Patches.Herb || getType() == Patches.Allotment
				|| getType() == Patches.Hops;
	}

	public boolean canWater() {
		return getType() != Patches.Herb && getType() != Patches.Tree;
	}

	public Seed getCorrespondingSeed() {
		int s = getState();
		for (Map.Entry<Integer, Seed> seed : Seed.seeds.entrySet()) {
			if (seed.getValue().getType() == getType()
					&& seed.getValue().getLow() <= s
					&& s <= seed.getValue().getHigh()) {
				return seed.getValue();
			}
		}
		return null;
	}

	public Patch(int id_, Location location_, int type_, int setting_,
			int shift_) {
		id = id_;
		setType(type_);
		setting = setting_;
		shift = shift_ * 4;
		location = location_;
	}

	public String toString() {
		return Patches.getTypeName(getType());
	}

	public boolean compost = false;
	Location location;

	public Location getLocation() {
		return location;
	}

	public Seed selectedSeed = null;
	private int id, setting, shift;
	private int type;

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	private Timer timer = new Timer(0);

	public Timer getTimer() {
		return timer;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Requirement getRequirement() {
		return new Requirement(0, new Value<Integer>() {
			public Integer get() {
				return (activated && selectedSeed != null) ? selectedSeed
						.getId() : 0;
			}
		});
	}
}
