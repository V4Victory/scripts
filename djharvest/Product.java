package djharvest;

import java.util.HashMap;

import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;

public class Product {
	
	int id;
	public int getId() { return id; }
	String name;
	public String getName() { return name; }
	ProcessOption[] processOptions;
	public ProcessOption[] getProcessOptions() { return processOptions; }
	public ProcessOption selectedProcessOption;
	
	public Product(int id_, String name_) {
		this(id_,name_,Drop);
	}
	
	public Product(int id_, String name_, ProcessOption... options) {
		id = id_;
		name = name_;
		processOptions = options;
		selectedProcessOption = processOptions[0];
	}
	
	public static ProcessOption DontProcess = new ProcessOption() {
		public String toString() { return "Don't process"; }
		public void run(Product p) {}
	};
	
	public static ProcessOption Drop = new ProcessOption() {
		public String toString() { return "Drop"; }
		public void run(Product p) {
			Item item;
			while((item = Inventory.getItem(p.id)) != null) {
				item.getWidgetChild().interact("Drop");
				Time.sleep(250);
			}			
		}
	};
	
	public static ProcessOption GiveToLeprechaun = new ProcessOption() {
		public String toString() { return "Give to leprechaun for notes"; }
		public void run(final Product p) {
			Camera.setPitch(89);
			final Item item = Inventory.getItem(p.id);
			if(item == null) return;			
			final Timer timer = new Timer(2000);
			waitFor(new Condition() { 
				public boolean validate() {
					if(!timer.isRunning()) {
						
						Mouse.click(item.getWidgetChild().getCentralPoint(),false);
						Menu.select("Use");
						Time.sleep(700);
						// 3021, 5808, 4965
						NPC leprechaun = NPCs.getNearest(7569,3021,5808,7557,4965);
						if(leprechaun == null) {
							Camera.setAngle((540-Camera.getYaw())%360);
							leprechaun = NPCs.getNearest(7569,3021,5808,7557,4965);
						}
						if(leprechaun != null) {
							Camera.turnTo(leprechaun);
							leprechaun.interact("Use");
						}
						timer.setEndIn(2000);
					}
					return Inventory.getCount(p.id) == 0; }
				});
		}
	};
	
	public static class Interact implements ProcessOption {
		String interaction;
		public Interact(String interaction_) {
			interaction = interaction_;
		}
		public String toString() { return interaction; }
		public void run(Product p) {
			Item item;
			while((item = Inventory.getItem(p.id)) != null) {
				item.getWidgetChild().interact(interaction);
				Time.sleep(250);
			}			
		}
	}
	
	public static void waitFor(Condition c) {
		while(!c.validate()) Time.sleep(1);
	}
	
	public static ProcessOption Clean = new ProcessOption() {
		public String toString() { return "Clean the herb"; }
		public void run(Product p) {
			for(Item item : Inventory.getItems()) {
				if(item.getId() == p.id) {
					item.getWidgetChild().interact("Clean");
					Time.sleep(200);
				}
			}
		}
	};
	
	public static ProcessOption Burn = new ProcessOption() {
		public String toString() { return "Burn the logs"; }
		public void run(Product p) {
			/*Item item;
			while((item = Inventory.getItem(p.id)) != null) {
				item.getWidgetChild().interact("Clean");
			}*/
		}
	};
	
	private static HashMap<Integer, Product> loadProducts() {
		HashMap<Integer,Product> products = new HashMap<Integer,Product>();
		Product[] productsPlain = { 	
				new Product(24154,"Spin ticket",new Interact("Claim spin")),
				new Product(24155,"Double spin ticket",new Interact("Claim spin")),
				new Product(14664,"Mini-event gift",Drop),
				new Product(6055,"Weed",Drop),
				new Product(199,"Grimy Guam",Drop,GiveToLeprechaun,Clean),
				new Product(249,"Clean Guam",Drop,GiveToLeprechaun),
				new Product(201,"Grimy Marrentil",Drop,GiveToLeprechaun,Clean),
				new Product(251,"Clean Marrentil",Drop,GiveToLeprechaun),
				new Product(203,"Grimy Tarromin",Drop,GiveToLeprechaun,Clean),
				new Product(253,"Clean Tarromin",Drop,GiveToLeprechaun),
				new Product(205,"Grimy Harralander",Drop,GiveToLeprechaun,Clean),
				new Product(255,"Clean Harralander",Drop,GiveToLeprechaun),
				new Product(207,"Grimy Ranarr",Drop,GiveToLeprechaun,Clean),
				new Product(257,"Clean Ranarr",Drop,GiveToLeprechaun),
				new Product(2998,"Grimy Toadflax",Drop,GiveToLeprechaun,Clean),
				new Product(2999,"Clean Toadflax",Drop,GiveToLeprechaun),
				new Product(209,"Grimy Irit",Drop,GiveToLeprechaun,Clean),
				new Product(259,"Clean Irit",Drop,GiveToLeprechaun),
				new Product(14836,"Grimy Wergali",Drop,GiveToLeprechaun,Clean),
				new Product(14854,"Clean Wergali",Drop,GiveToLeprechaun),
				new Product(211,"Grimy Avantoe",Drop,GiveToLeprechaun,Clean),
				new Product(261,"Clean Avantoe",Drop,GiveToLeprechaun),
				new Product(213,"Grimy Kwuarm",Drop,GiveToLeprechaun,Clean),
				new Product(263,"Clean Kwuarm",Drop,GiveToLeprechaun),
				new Product(3051,"Grimy Snapdragon",Drop,GiveToLeprechaun,Clean),
				new Product(3000,"Clean Snapdragon",Drop,GiveToLeprechaun),
				new Product(215,"Grimy Cadantine",Drop,GiveToLeprechaun,Clean),
				new Product(265,"Clean Cadantine",Drop,GiveToLeprechaun),
				new Product(2485,"Grimy Lantadyme",Drop,GiveToLeprechaun,Clean),
				new Product(2481,"Clean Lantadyme",Drop,GiveToLeprechaun),
				new Product(217,"Grimy Dwarf weed",Drop,GiveToLeprechaun,Clean),
				new Product(267,"Clean Dwarf weed",Drop,GiveToLeprechaun),
				new Product(12174,"Grimy Spirit weed",Drop,GiveToLeprechaun,Clean),
				new Product(12172,"Clean Spirit weed",Drop,GiveToLeprechaun),
				new Product(219,"Grimy Torstol",Drop,GiveToLeprechaun,Clean),
				new Product(269,"Clean Torstol",Drop,GiveToLeprechaun),
				new Product(21626,"Grimy Fellstalk",Drop,GiveToLeprechaun,Clean),
				new Product(21624,"Clean Fellstalk",Drop,GiveToLeprechaun),
				new Product(1942,"Raw potato",Drop,GiveToLeprechaun),
				new Product(1957,"Onion",GiveToLeprechaun),
				new Product(1965,"Cabbage",Drop,GiveToLeprechaun),
				new Product(1982,"Tomato",Drop,GiveToLeprechaun),
				new Product(5986,"Sweetcorn",Drop,GiveToLeprechaun),
				new Product(5504,"Strawberry",Drop,GiveToLeprechaun),
				new Product(5982,"Watermelon",Drop,GiveToLeprechaun),
				new Product(6010,"Marigold",Drop,GiveToLeprechaun),
				new Product(1793,"Woad leaf",Drop,GiveToLeprechaun),
				new Product(225,"Limpwurt root",Drop,GiveToLeprechaun),
				new Product(14583,"White lily",Drop,GiveToLeprechaun),
				new Product(6006,"Barley",Drop,GiveToLeprechaun),
				new Product(5994,"Hammerstone",Drop,GiveToLeprechaun),
				new Product(5996,"Asgarnian",Drop,GiveToLeprechaun),
				new Product(5931,"Jute fibre",Drop,GiveToLeprechaun),
				new Product(5998,"Yanillian",Drop,GiveToLeprechaun),
				new Product(6000,"Krandorian",Drop,GiveToLeprechaun),
				new Product(6002,"Wildblood",Drop,GiveToLeprechaun),
				new Product(1951,"Redberries",Drop,GiveToLeprechaun),
				new Product(753,"Cadava berries",Drop,GiveToLeprechaun),
				new Product(2126,"Dwellberries",Drop,GiveToLeprechaun),
				new Product(247,"Jangerberries",Drop,GiveToLeprechaun),
				new Product(239,"White berries",Drop,GiveToLeprechaun),
				new Product(6018,"Poisonivy berries",Drop,GiveToLeprechaun),
				new Product(1955,"Cooking apple",Drop,GiveToLeprechaun),
				new Product(1963,"Banana",Drop,GiveToLeprechaun),
				new Product(2108,"Orange",Drop,GiveToLeprechaun),
				new Product(2011,"Curry",Drop,GiveToLeprechaun),
				new Product(2114,"Pineapple",Drop,GiveToLeprechaun),
				new Product(5972,"Papaya",Drop,GiveToLeprechaun),
				new Product(5974,"Coconut",Drop,GiveToLeprechaun),
				new Product(12134,"Evil turnip",Drop,GiveToLeprechaun),
				new Product(6004,"Bittercap mushroom",Drop,GiveToLeprechaun),
				new Product(6016,"Cactus spine",Drop,GiveToLeprechaun),
				new Product(2398,"Nightshade",Drop,GiveToLeprechaun),
				new Product(5980,"Calquat fruit",Drop,GiveToLeprechaun),
				new Product(1521,"Oak logs",Drop,Burn),
				new Product(1519,"Willow logs",Drop,Burn),
				new Product(1517,"Maple logs",Drop,Burn),
				new Product(1515,"Yew logs",Drop,Burn),
				new Product(1513,"Magic logs",Drop,Burn),
				new Product(6043,"Oak roots",Drop),
				new Product(6045,"Willow roots",Drop),
				new Product(6047,"Maple roots",Drop),
				new Product(6049,"Yew roots",Drop),
				new Product(6051,"Magic roots",Drop)
									
		};
		for(Product product : productsPlain) {
			products.put(product.getId(), product);
		}
		return products;
	}
	
	public static HashMap<Integer, Product> products = loadProducts();
}
