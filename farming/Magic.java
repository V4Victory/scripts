package scripts.farming;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;


public class Magic {
	
	enum Spellbook{ Standard, Ancient, Lunar, UNKNOWN }; 
	
	public static int getSpellbookWidgetID(Spellbook book) {
		switch(book) {
		case Standard: return 192;
		case Lunar: return 430;
		default: return 0;
		}
	}
	
	public static Spellbook getCurrentSpellbook() {
		Tabs.MAGIC.open();
		if(Widgets.get(getSpellbookWidgetID(Spellbook.Standard),0).isOnScreen()) return Spellbook.Standard;
		if(Widgets.get(getSpellbookWidgetID(Spellbook.Lunar),0).isOnScreen()) return Spellbook.Lunar;
		if(Widgets.get(getSpellbookWidgetID(Spellbook.Ancient),0).isOnScreen()) return Spellbook.Ancient;
		return Spellbook.UNKNOWN;
	}
	
	public static class Standard {
		public static final Spell HomeTeleport = new Spell("Home Teleport",24,Spellbook.Standard,0);
	}
	
	public static class Lunar {
		public static final Spell FertileSoil = new Spell("Fertile Soil",24,Spellbook.Lunar,83);
		public static final Spell NPCContact = new Spell("NPC Contact",26,Spellbook.Lunar,67);
		public static final Spell EnergyTransfer = new Spell("Energy Transfer",27,Spellbook.Lunar,91);
		public static final Spell Humidify = new Spell("Humidify",29,Spellbook.Lunar,68);
		public static final Spell MonsterExamine = new Spell("Monster Examine",28,Spellbook.Lunar,66);
		public static final Spell CureGroup = new Spell("Cure Group",25,Spellbook.Lunar,74);
		public static final Spell CureOther = new Spell("Cure Other",23,Spellbook.Lunar,68);
		public static final Spell TeleportBarbarian = new Spell("Barbarian Teleport",22,Spellbook.Lunar,75);
		public static final Spell HunterKit = new Spell("Hunter Kit",30,Spellbook.Lunar,71);
		public static final Spell StatSpy = new Spell("Stat Spy",31,Spellbook.Lunar,75);
		public static final Spell Dream = new Spell("Dream",32,Spellbook.Lunar,79);
		public static final Spell PlankMake = new Spell("Plank Make",33,Spellbook.Lunar,86);
		public static final Spell SpellbookSwap = new Spell("Spellbook Swap",34,Spellbook.Lunar,96);
		public static final Spell TuneBaneOre = new Spell("Tune Bane Ore",35,Spellbook.Lunar,87);
		public static final Spell MagicImbue = new Spell("Magic Imbue",36,Spellbook.Lunar,82);
		public static final Spell Vengeance = new Spell("Vengeance",37,Spellbook.Lunar,94);
		public static final Spell BakePie = new Spell("Bake Pie",38,Spellbook.Lunar,65);
		public static final Spell HomeTeleport = new Spell("Home Teleport",39,Spellbook.Lunar,0);
		public static final Spell TeleportFishingGuild = new Spell("Fishing Guild Teleport",40,Spellbook.Lunar,85);
		public static final Spell TeleportKhazardTeleport = new Spell("Khazard Teleport",41,Spellbook.Lunar,78);
		public static final Spell VengeanceOther = new Spell("Vengeance Other",42,Spellbook.Lunar,93);
		public static final Spell TeleportMoonClan = new Spell("Moonclan Teleport",43,Spellbook.Lunar,69);
		public static final Spell TeleportCatherby = new Spell("Catherby Teleport",44,Spellbook.Lunar,87);
		public static final Spell StringJewellery = new Spell("String Jewellery",45,Spellbook.Lunar,80);
		public static final Spell CureMe = new Spell("Cure Me",46,Spellbook.Lunar,71);
		public static final Spell TeleportWaterbirth = new Spell("Waterbirth Teleport",47,Spellbook.Lunar,72);
		public static final Spell SuperglassMake = new Spell("Superglass Make",48,Spellbook.Lunar,77);		
		public static final Spell BoostPotionShare = new Spell("Boost Potion Share",49,Spellbook.Lunar,84);
		public static final Spell StatRestorePotShare = new Spell("State Restore Pot Share",50,Spellbook.Lunar,81);
		public static final Spell TeleportIcePlateau = new Spell("Ice Plateau Teleport",51,Spellbook.Lunar,89);
		public static final Spell HealOther = new Spell("Heal Other",52,Spellbook.Lunar,92);
		public static final Spell HealGroup = new Spell("Heal Group",53,Spellbook.Lunar,95);
		public static final Spell TeleportOurania = new Spell("Ourania Teleport",54,Spellbook.Lunar,71);
		public static final Spell CurePlant = new Spell("Cure Plant",55,Spellbook.Lunar,66);
		public static final Spell TeleGroupMoonClan = new Spell("Tele Group Moonclan",56,Spellbook.Lunar,70);
		public static final Spell TeleGroupWaterbirth = new Spell("Tele Group Waterbirth",57,Spellbook.Lunar,73);
		public static final Spell TeleGroupBarbarian = new Spell("Tele Group Barbarian",58,Spellbook.Lunar,76);
		public static final Spell TeleGroupKhazard = new Spell("Tele Group Khazard",59,Spellbook.Lunar,79);
		public static final Spell TeleGroupFishingGuild = new Spell("Tele Group Fishing Guild",60,Spellbook.Lunar,86);
		public static final Spell TeleGroupCatherby = new Spell("Tele Group Catherby",61,Spellbook.Lunar,88);
		public static final Spell TeleGroupIcePlateau = new Spell("Tele Group Ice Plateau",62,Spellbook.Lunar,90);
		public static final Spell TeleportSouthFalador = new Spell("South Falador Teleport",67,Spellbook.Lunar,72);
		public static final Spell RepairRunePouch = new Spell("Repair Rune Pouch",68,Spellbook.Lunar,75);
		public static final Spell TeleportNorthArdougne = new Spell("North Ardougne Teleport",69,Spellbook.Lunar,76);
		public static final Spell RemoteFarm = new Spell("Remote Farm",70,Spellbook.Lunar,78);
		public static final Spell SpiritualiseFood = new Spell("Spiritualise Food",71,Spellbook.Lunar,80);
		public static final Spell MakeLeather = new Spell("Make Leather",72,Spellbook.Lunar,83);
		public static final Spell DisruptionShield = new Spell("Disruption Shield",73,Spellbook.Lunar,90);
		public static final Spell VengeanceGroup = new Spell("Vengeance Group",74,Spellbook.Lunar,95);
		public static final Spell TeleportTrollheim = new Spell("Trollheim Teleport",75,Spellbook.Lunar,92);
		public static final Spell TeleGroupTrollheim = new Spell("Tele Group to Trollheim",76,Spellbook.Lunar,93);
		public static final Spell BorrowedPower = new Spell("Borrowed Power",77,Spellbook.Lunar,99);
		
	}

	
	public static void cast(int id) {
		Spellbook book = getCurrentSpellbook();
		WidgetChild spell = Widgets.get(getSpellbookWidgetID(book),id);
		switch(book) {
		case Standard: Widgets.scroll(spell, Widgets.get(192,94)); break;
		case Lunar: break;
		case Ancient: break;
		}
		int y = spell.getAbsoluteY();
		if(y<200 || y>420) {
			int adjust = 310 - y;
			Mouse.move(Widgets.get(getSpellbookWidgetID(book),63).getChild(1).getCentralPoint());
			Mouse.drag(Mouse.getX(),Mouse.getY()-adjust);
			Time.sleep(1300);
		}
		spell = Widgets.get(getSpellbookWidgetID(book),id);
		spell.interact("Cast");
	}
	
	public static void cureAllDiseased() {
		Widget widget = Widgets.get(1082);
		for(WidgetChild patch : widget.getChildren()) {
			if(patch.getText().equals("Is Diseased!") || patch.getTextColor() == 16711680) {
				System.out.println(patch.getId() + " is diseased");
				int y = patch.getAbsoluteY();
				if(y<170 || y>350) {
					int adjust = (260 - y) / 5;
					Mouse.move(Widgets.get(1082,44).getChild(1).getCentralPoint());
					System.out.println(adjust);
					Mouse.drag(Mouse.getX(),Mouse.getY()-adjust);
					Time.sleep(1300);
				}
				patch.interact("Cure patch");
				Time.sleep(1300);
			}
		}
	}
}
