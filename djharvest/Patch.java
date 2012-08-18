package scripts.djharvest;
import java.util.Map;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class Patch {
	

	public SceneObject getSceneObject() {
		return SceneEntities.getNearest(id); 
	}

	private int getState() {
		return Settings.get(setting,shift,0xff);
	}
	public boolean isActivated() {
		return selectedSeed != null && selectedSeed.getId()>0;
	}
	public boolean isDead() {
		if(type == Patches.Herb){
			if(getState()>=0xc0 && getState()<=0xcb) {
				return getState() >= 0xc9 && getState() <= 0xcb;
			} else if(getState()>=0xcc && getState()<=0xd7) {
				return getState() >= 0xd5 && getState() <= 0xd7;
			} else {
				return getState() >= 0xaa && getState() <= 0xac;
			}
		} else if(type == Patches.Tree) {
			return (getState() & 0xc0) == 0x80;
		}	else {
			return (getState() & 0xc0) == 0xc0;
		}
	}
	public int countWeeds() {
		return getState()<4 ? 3-(getState() & 0x3) : 0;
	}
	public double getProgress() {
		int state = getState() & ((type==Patches.Herb) ? 0x7f : 0x3f);
		Seed seed = getCorrespondingSeed();
		if(seed == null) return 0;
		if(seed.getMid()-seed.getLow() == 0) return 0;
		return (double)(state-seed.getLow())/(seed.getMid()-seed.getLow());
	}
	public boolean isEmpty() {
		return (getState() < 4);
	}
	public boolean isDiseased() {
		if(type == Patches.Herb){
			if(getState()>=0xc0 && getState()<=0xcb) {
				return getState() >= 0xc6 && getState() <= 0xc8;
			} else if(getState()>=0xcc && getState()<=0xd7) {
				return getState() >= 0xd2 && getState() <= 0xd4;
			} else {
				return (getState() & 0x80) == 0x80;
			}
		} else if(type == Patches.Tree) {
			return (getState() & 0xc0) == 0x40;
		}	else {
			return (getState() & 0xc0) == 0x80;
		}
	}
	public boolean isWatered() {
		return type==Patches.Herb ? false : (getState() & 0xc0) == 0x40;
	}
	public boolean useSecateurs() {
		return type==Patches.Herb || type==Patches.Allotment || type==Patches.Hops;
	}
	public boolean canWater() {
		return type!=Patches.Herb && type!=Patches.Tree;
	}
	public Seed getCorrespondingSeed() {
		int s = getState() & 0x3f;
		for(Map.Entry<Integer,Seed> seed : Seed.seeds.entrySet()) {
			if(seed.getValue().getType() == type && seed.getValue().getLow()<=s && s<=seed.getValue().getHigh()) {
				return seed.getValue();
			}
		}
		return null;
	}
	
	public Patch(int id_, Location location_, int type_, int setting_, int shift_) {
		id = id_;
		type = type_;
		setting = setting_;
		shift = shift_*4;
		location = location_;
	}
	
	public String toString() {
		return Patches.getTypeName(type);
	}
	
	PatchOption diseaseOption = null;
	PatchOption compostOption = null;
	Location location;
	public Location getLocation() { return location; }
	public Seed selectedSeed = null;
	private int id, setting, shift;
	private int type;
	public int getId() { return id; }
	public int getType() {
		return type;
	}
	
	
	private Timer timer = new Timer(0);
	public Timer getTimer() {
		return timer;
	}
}
