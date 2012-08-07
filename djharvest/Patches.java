package scripts.djharvest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.SceneObject;


public class Patches {
	
	public static final int Flower = 0;
	public static final int Herb = 1;
	public static final int Allotment = 2;
	public static final int Tree = 3;
	public static final int FruitTree = 4;
	public static final int Bush = 5;
	public static final int Hops = 6;
	public static final int Belladonna = 7;
	public static final int EvilTurnip = 8;
	public static final int Mushroom = 9;
	public static final int Cactus = 10;
	public static final int Calquat = 11;
	public static final int JadeVine = 12;
	public static final int COUNT_TYPES = 13;
	
	public static String getTypeName(int type) {
		switch(type) {
		case Allotment: return "Allotment";
		case Herb: return "Herb Patch";
		case Flower: return "Flower Patch";
		case Tree: return "Tree";
		case FruitTree: return "FruitTree";
		case Bush: return "Bush";
		case Hops: return "Hops";
		case Belladonna: return "Belladonna";
		case EvilTurnip: return "Evil turnip";
		case Mushroom: return "Mushroom";
		case Cactus: return "Cactus";
		case Calquat: return "Calquat";
		case JadeVine: return "Jade Vine";
		default: return "Unknown";
		}
	}
	
	public static Integer countAllWork() {
		Integer count = 0;
		for(Patch patch : patches.values()) {
			if(patch.isActivated() && (patch.isDead() || patch.isDiseased() || patch.getProgress() == 1.0
					|| patch.isEmpty())) count += getTypeRating(patch.getType());
		}
		return count;
	}
	
	static Map<Integer,Integer> TypeRating = new HashMap<Integer,Integer>();
	public static Integer getTypeRating(int type) {
		if(TypeRating.containsKey(type)) {
			return TypeRating.get(type);
		} else {
			TypeRating.put(type, 0);
			return 0;
		}
	}
	public static void setTypeRating(int type, Integer rating) {
		TypeRating.put(type,rating);
	}
	
	public static class BelongsToType implements Filter<SceneObject> {
		int t;
		public BelongsToType(int t_) {
			t = t_;
		}
		public boolean accept(SceneObject a) {
			int id = a.getId();
			switch(t) {
			case Flower: return 7847<=id && id<=7849;
			case Herb: return 8150<=id && id<=8153;
			case Allotment: return 8550<=id && id<=8556;
			default: return false;
			}
		}
		
	}
	
	private static HashMap<Integer, Patch> loadPatches() {
		HashMap<Integer,Patch> patches = new HashMap<Integer,Patch>();
		Patch[] patchesPlain = { 	new Patch(8550,Location.getLocation("Falador"),Allotment,504,0), 	// Fala West
									new Patch(8551,Location.getLocation("Falador"),Allotment,504,2), 	// Fala East 
									new Patch(7847,Location.getLocation("Falador"),Flower,508,0),
									new Patch(8150,Location.getLocation("Falador"),Herb,515,0),
									new Patch(8552,Location.getLocation("Catherby"),Allotment,504,4),	// Cath North
									new Patch(8553,Location.getLocation("Catherby"),Allotment,504,6),	// Cath South
									new Patch(7848,Location.getLocation("Catherby"),Flower,508,2),
									new Patch(8151,Location.getLocation("Catherby"),Herb,515,2),
									new Patch(8554,Location.getLocation("Ardougne"),Allotment,505,0),	// Adg West
									new Patch(8555,Location.getLocation("Ardougne"),Allotment,505,2),	// Adg East
									new Patch(7849,Location.getLocation("Ardougne"),Flower,508,4),
									new Patch(8152,Location.getLocation("Ardougne"),Herb,515,4),
									new Patch(8556,Location.getLocation("Morytania"),Allotment,505,4),	// Mory West
									new Patch(8557,Location.getLocation("Morytania"),Allotment,505,6),	// Mory East
									new Patch(7850,Location.getLocation("Morytania"),Flower,508,6),
									new Patch(8153,Location.getLocation("Morytania"),Herb,515,6),
									new Patch(18816,Location.getLocation("Trollheim"),Herb,830,0)
									
		};
		for(Patch patch : patchesPlain) {
			patches.put(patch.getId(), patch);
		}
		return patches;
	}
	
	public static HashMap<Integer, Patch> patches = loadPatches();
	
	public static List<Patch> getPatches(Filter<Patch> filter) {
		List<Patch> filtered = new ArrayList<Patch>();
		for(Entry<Integer, Patch> patch : patches.entrySet()) {
			if(filter.accept(patch.getValue()))
				filtered.add(patch.getValue());
		}
		return filtered;
	}


}
