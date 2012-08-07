package scripts.djharvest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.powerbot.game.api.util.Filter;

public class Seed {
	public Seed(String name_, int id_, int t_, int low_, int mid_) {
		this(name_,id_,t_,low_,mid_,mid_);
	}

	
	public Seed(String name_, int id_, int t_, int low_, int mid_, int high_) {
		name = name_;
		id = id_;
		t = t_;
		low = low_;
		mid = mid_;
		high = high_;
	}
	
	String name;
	public String toString() {
		return name;
	}
	
	
	public static final int Marigold = 5096;
	public static final int Rosemary = 5097;
	public static final int Nasturtium = 5098;
	public static final int WoadLeaf = 5099;
	public static final int Limpwurt = 5100;
	public static final int WhiteLily = 14589;
	
	public static final int Potato = 5318;
	public static final int Onion = 5319;
	public static final int Cabbage = 5324;
	public static final int Tomato = 5322;
	public static final int Sweetcorn = 5320;
	public static final int Strawberry = 5323;
	public static final int Watermelon = 5321;
			
	public static final int Guam = 5291;
	public static final int Marrentil = 5292;
	public static final int Tarromin = 5293;
	public static final int Harralander = 5294;
	public static final int Ranarr = 5295;
	public static final int SpiritWeed = 12176;
	public static final int Toadflax = 5296;
	public static final int Irit = 5297;
	public static final int Wergali = 14870;
	public static final int Avantoe = 5298;
	public static final int Kwuarm = 5299;
	public static final int Snapdragon = 5300;
	public static final int Cadantine = 5301;
	public static final int Lantadyme = 5302;
	public static final int DwarfWeed = 5303;
	public static final int Torstol = 5304;
	public static final int Fellstalk = 21621;
	public static final int GoutTuber = 6311;
	
	int low, mid, high;
	int id;
	int t;
	
	public boolean compost = false;
	public List<ProtectionItem> protectionItems = new ArrayList<ProtectionItem>();
	
	public class ProtectionItem {
		public int id;
		public int idNoted;
		public int amount;
	}
	
	public Seed addProtectionItem(int id,int amount) {
		return addProtectionItem(id,id+1,amount);
	}
	
	public Seed addProtectionItem(int id,int idNoted,int amount) {
		ProtectionItem pitem = new ProtectionItem();
		pitem.id = id;
		pitem.idNoted = idNoted;
		pitem.amount = amount;
		protectionItems.add(pitem);
		return this;
	}
	
	public int getLow() { return low; }
	public int getMid() { return mid; }
	public int getHigh() { return high; }
	public int getId() { return id; }
	public int getType() { return t; }
	
	public static List<Seed> getSeeds(Filter<Seed> filter) {
		List<Seed> filtered = new ArrayList<Seed>();
		for(Entry<Integer, Seed> seed : seeds.entrySet()) {
			if(filter.accept(seed.getValue()))
				filtered.add(seed.getValue());
		}
		return filtered;
	}
	
	private static HashMap<Integer,Seed> loadSeeds() {
		HashMap<Integer,Seed> seeds = new HashMap<Integer,Seed>();

		Seed[] seedsPlain = { 	new Seed("Disabled",-1,Patches.Flower,0,0),
								new Seed("Disabled",-2,Patches.Allotment,0,0),
								new Seed("Disabled",-3,Patches.Herb,0,0),
								new Seed("Marigold",Seed.Marigold,Patches.Flower,0x08,0x0c),
								new Seed("Rosemary",Seed.Rosemary,Patches.Flower,0x0d,0x11),
								new Seed("Nasturtium",Seed.Nasturtium,Patches.Flower,0x12,0x16),
								new Seed("Woad Leaf",Seed.WoadLeaf,Patches.Flower,0x17,0x1b),
								new Seed("Limpwurt",Seed.Limpwurt,Patches.Flower,0x1c,0x20),
								new Seed("White lily",Seed.WhiteLily,Patches.Flower,0x25,0x29),
								new Seed("Potato", Seed.Potato,Patches.Allotment,0x06,0x0a,0x0c).addProtectionItem(6032,2),
								new Seed("Onion", Seed.Onion,Patches.Allotment,0x0d,0x11,0x13).addProtectionItem(5438,1),
								new Seed("Cabbage",Seed.Cabbage,Patches.Allotment,0x14,0x18,0x1a).addProtectionItem(5458,1),
								new Seed("Tomato",Seed.Tomato,Patches.Allotment,0x1b,0x1f,0x21).addProtectionItem(5478,2),
								new Seed("Sweetcorn",Seed.Sweetcorn,Patches.Allotment,0x22,0x28,0x2a).addProtectionItem(5931,10),
								new Seed("Strawberry",Seed.Strawberry,Patches.Allotment,0x2b,0x32,0x33).addProtectionItem(5386,1),
								new Seed("Watermelon",Seed.Watermelon,Patches.Allotment,0x34,0x3c,0x3e).addProtectionItem(2011,10),
								new Seed("Guam",Seed.Guam,Patches.Herb,4,8,10),
								new Seed("Marrentil",Seed.Marrentil,Patches.Herb,11,15,17),
								new Seed("Tarromin",Seed.Tarromin,Patches.Herb,18,22,24),
								new Seed("Harralander",Seed.Harralander,Patches.Herb,25,29,31),
								new Seed("Ranarr",Seed.Ranarr,Patches.Herb,32,36,38),
								new Seed("Spirit weed",Seed.SpiritWeed,Patches.Herb,0xcc,0xd0,0xd7),
								new Seed("Toadflax",Seed.Toadflax,Patches.Herb,39,43,45),
								new Seed("Irit",Seed.Irit,Patches.Herb,46,50,52),
								new Seed("Wergali",Seed.Wergali,Patches.Herb,60,65,66),
								new Seed("Avantoe",Seed.Avantoe,Patches.Herb,53,57,59),
								new Seed("Kwuarm",Seed.Kwuarm,Patches.Herb,68,72,74),
								new Seed("Snapdragon",Seed.Snapdragon,Patches.Herb,75,79,81),
								new Seed("Cadantine",Seed.Cadantine,Patches.Herb,82,86,88),
								new Seed("Lantadyme",Seed.Lantadyme,Patches.Herb,89,93,95),
								new Seed("Dwarf weed",Seed.DwarfWeed,Patches.Herb,96,100,102),
								new Seed("Torstol",Seed.Torstol,Patches.Herb,103,107,109),
								new Seed("Fellstalk",Seed.Fellstalk,Patches.Herb,110,114,116), // ?!?!
								new Seed("Gout",Seed.GoutTuber,Patches.Herb,0xc0,0xc4,0xcb),
								//new Seed("Barley",5305,Patches.Hops,)
								//new Seed("Redberry",5101,Patches.Bush,0x05,0x09,0x0e),
								new Seed("Oak Tree",5370,Patches.Tree,0x08,0x0c,0x0e),
								new Seed("Willow Tree",5371,Patches.Tree,0x0f,0x15,0x17),
								new Seed("Maple Tree",5372,Patches.Tree,0x18,0x20,0x22),
								new Seed("Yew Tree",5373,Patches.Tree,0x23,0x2d,0x2f),
								new Seed("Magic Tree",5374,Patches.Tree,0x30,0x3c,0x3e)
								
		};
		for(Seed seed : seedsPlain) {
			seeds.put(seed.getId(),seed);
		}
		return seeds;
	}
	public static HashMap<Integer,Seed> seeds = loadSeeds();
}
