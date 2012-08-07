package scripts.djharvest;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;


public class Magic {
	public class Lunar {
		public static final int FertileSoil = 24;
		public static final int NPCContact = 26;
		public static final int EnergyTransfer = 27;
		public static final int Humidify = 29;
		public static final int MonsterExamine = 28;
		public static final int CureGroup = 25;
		public static final int CureOther = 23;
		public static final int TeleportBarbarian = 22;
		public static final int HunterKit = 30;
		public static final int StatSpy = 31;
		public static final int Dream = 32;
		public static final int PlankMake = 33;
		public static final int SpellbookSwap = 34;
		public static final int TuneBaneOre = 35;
		public static final int MagicImbue = 36;
		public static final int Vengeance = 37;
		public static final int BakePie = 38;
		public static final int HomeTeleport = 39;
		public static final int TeleportFishingGuild = 40;
		public static final int TeleportKhazardTeleport = 41;
		public static final int VengeanceOther = 42;
		public static final int TeleportMoonClan = 43;
		public static final int TeleportCatherby = 44;
		public static final int StringJewellery = 45;
		public static final int CureMe = 46;
		public static final int TeleportWaterbirth = 47;
		public static final int SuperglassMake = 48;		
		public static final int BoostPotionShare = 49;
		public static final int StatRestorePotShare = 50;
		public static final int TeleportIcePlateau = 51;
		public static final int HealOther = 52;
		public static final int HealGroup = 53;
		public static final int TeleportOurania = 54;
		public static final int CurePlant = 55;
		public static final int TeleGroupMoonClan = 56;
		public static final int TeleGroupWaterbirth = 57;
		public static final int TeleGroupBarbarian = 58;
		public static final int TeleGroupKhazard = 59;
		public static final int TeleGroupFishingGuild = 60;
		public static final int TeleGroupCatherby = 61;
		public static final int TeleGroupIcePlateau = 62;
		public static final int TeleportSouthFalador = 67;
		public static final int RepairRunePouch = 68;
		public static final int TeleportNorthArdougne = 69;
		public static final int RemoteFarm = 70;
		public static final int SpiritualiseFood = 71;
		public static final int MakeLeather = 72;
		public static final int DisruptionShield = 73;
		public static final int VengeanceGroup = 74;
		public static final int TeleportTrollheim = 75;
		public static final int TeleGroupTrollheim = 76;
		public static final int BorrowedPower = 77;
		
	}
	
	public static void cast(int id) {
		Tabs.MAGIC.open();
		WidgetChild spell = Widgets.get(430,id);
		int y = spell.getAbsoluteY();
		if(y<200 || y>420) {
			int adjust = 310 - y;
			Mouse.move(Widgets.get(430,63).getChild(1).getCentralPoint());
			Mouse.drag(Mouse.getX(),Mouse.getY()-adjust);
			Time.sleep(1300);
		}
		spell = Widgets.get(430,id);
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
