package scripts.farming.modules;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.djharvest.Product;
import scripts.farming.Equipment;
import scripts.farming.FarmingProject;
import scripts.farming.Location;
import scripts.farming.Magic;
import scripts.farming.Patch;
import scripts.state.Condition;
import scripts.state.ConsecutiveState;
import scripts.state.Module;
import scripts.state.State;
import scripts.state.StateCreator;
import scripts.state.Value;
import scripts.state.edge.Animation;
import scripts.state.edge.Edge;
import scripts.state.edge.Either;
import scripts.state.edge.Equip;
import scripts.state.edge.InteractNPC;
import scripts.state.edge.InteractSceneObject;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Notification;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.UseItemWithSceneObject;
import scripts.state.edge.Walk;

public class DoPatches extends Module {

	static boolean locationNeedsSecateurs(Location loc) {
		for (Patch patch : loc.getPatches()) {
			if (patch.useSecateurs() && patch.activated)
				return true;
		}
		return false;
	}

	static boolean locationNeedsWater(Location loc) {
		for (Patch patch : loc.getPatches()) {
			if (patch.canWater() && patch.activated)
				return true;
		}
		return false;
	}

	public static Requirement getSeedRequirements(Location loc) {
		System.out.println("Seed requirements for " + loc + "?");
		Requirement req = new Requirement(0, 0); // neutral element
		for (final Patch patch : loc.getPatches()) {
			req = req.and(patch.getRequirement());
		}
		System.out.println("Seed requirements for " + loc + ":" + req);
		return req;
	}

	public DoPatches(Location loc, State initial, State success, State critical) {
		super("Do Patches", initial, success, critical, new Requirement[] {
				DoPatches.getSeedRequirements(loc),
				new Requirement(5, Constants.PlantCure, true),
				new Requirement(0, Constants.AstralRune, true),
				new Requirement(0, Constants.NatureRune, true),
				new Requirement(1,
						locationNeedsWater(loc) ? Constants.MagicWaterCan : 0,
						true),
				new Requirement(1,
						locationNeedsSecateurs(loc) ? Constants.MagicSecateurs
								: 0, true),
				new Requirement(1, Constants.MudBattleStaff, true)
						.or(new Requirement(1, Constants.MysticMudBattleStaff,
								true)) });

		initial.add(new Edge(Condition.TRUE, new ConsecutiveState<Patch>(loc
				.getPatches(), success, new StateCreator<Patch>() {
			public State getState(final Patch patch, State nextState) {
				return doPatch(patch, nextState).add(
						new Timeout(nextState, 5000));
			}
		})));
	}

	public State doPatch(final Patch patch, State nextState) {
		Value<SceneObject> sceneObject = new Value<SceneObject>() {
			public SceneObject get() {
				return patch.getSceneObject();
			}
		};

		State state = new State("PATCH");
		state.add(new Edge(new Condition() {
			public boolean validate() {
				return patch.isGrowing() || !patch.activated;
			}
		}, nextState));
		State processProducts = new State("PROC");

		// after idling for 25 seconds, stop script
		state.add(new Timeout(getCriticalState(), 25000));

		// Rake the patch
		State raking = new State("RAK");
		State rakingFailed = new State("RAKF");
		state.add(new InteractSceneObject(new Condition() {
			public boolean validate() {
				return patch.countWeeds() > 0;
			}
		}, raking, sceneObject, "Rake", true));
		raking.add(new Animation(Condition.TRUE, 2273, processProducts,
				new Timeout(rakingFailed, 7000)));
		raking.add(new Timeout(rakingFailed,15000));
		rakingFailed.add(new Notification(Condition.TRUE, processProducts,
				"Raking failed"));

		/** Curing disease **/

		State cureCasted = new State("CURC");
		State curing = new State("CUR");
		State curingFailed = new State("CURF");
		state.add(new MagicCast(new Condition() {
			public boolean validate() {
				return patch.isDiseased();
			}
		}, cureCasted, state, Magic.Lunar.CurePlant));
		state.add(new UseItemWithSceneObject(new Condition() {
			public boolean validate() {
				return patch.isDiseased();
			}
		}, curing, Constants.PlantCure, sceneObject));
		Value<NPC> leprechaun = new Value<NPC>() {
			public NPC get() {
				return NPCs.getNearest(7569, 3021, 5808, 7557, 4965);
			}
		};
		State exchanging = new State("EXCHG");
		if (patch.getLocation() == Location.getLocation("Catherby")) {
			State inShop = new State("INSHOP");
			State shopOpened = new State("SHOPOPENED");
			State shopped = new State("SHOPPED");
			State shoppingFinished = new State("SHOPF");
			State storing = new State("STORING");
			state.add(new Walk(new Condition() {
				public boolean validate() {
					return patch.isDiseased();
				}
			}, new Tile(2819, 3461, 0), inShop, new Timeout(state, 5000)));
			inShop.add(new InteractNPC(Condition.TRUE, shopOpened,
					new Value<NPC>() {
						public NPC get() {
							return NPCs.getNearest(2305);
						}
					}, "Trade"));
			shopOpened.add(new Task(new Condition() {
				public boolean validate() {
					return Widgets.get(1265, 20).validate();
				}
			}, shopped) {
				public void run() {
					if (Widgets.scroll(Widgets.get(1265, 20).getChild(27),
							Widgets.get(1265, 57))) {
						Widgets.get(1265, 20).getChild(27).interact("Buy 10");
					}
				}
			});
			shopped.add(new Walk(Condition.TRUE, new Tile(2811, 3462, 0),
					shoppingFinished, new Timeout(shoppingFinished, 10000)));
			shoppingFinished.add(new InteractNPC(new Condition() {
				public boolean validate() {
					return Inventory.getCount(Constants.PlantCure) > 2;
				}
			}, storing, leprechaun, "Exchange"));
			storing.add(new Task(new Condition() {
				public boolean validate() {
					return Widgets.get(126, 29).validate();
				}
			}, exchanging) {
				public void run() {
					Widgets.get(126, 29).interact("Store-All");
				}
			});
		}
		state.add(new InteractNPC(new Condition() {
			public boolean validate() {
				return patch.isDiseased();
			}
		}, exchanging, leprechaun, "Exchange"));
		exchanging.add(new Task(new Condition() {
			public boolean validate() {
				return Widgets.get(125, 30).validate();
			}
		}, state) {
			public void run() {
				Widgets.get(125, 30).interact("Remove-1");
				Widgets.get(125, 30).interact("Remove-1");
				Widgets.get(125, 37).click(true);
			}
		});

		cureCasted.add(new InteractSceneObject(Condition.TRUE, curing,
				sceneObject, "Cast", true));
		curing.add(new Animation(Condition.TRUE, 4432, state, new Timeout(
				curingFailed, 7000)));
		curing.add(new Animation(Condition.TRUE, 2273, state, new Timeout(
				curingFailed, 3000)));
		curing.add(new Animation(Condition.TRUE, 2288, state, new Timeout(
				curingFailed, 3000)));
		curing.add(new Timeout(state,2000));
		curingFailed.add(new Notification(Condition.TRUE, state,
				"Curing failed"));

		/** End curing disease **/

		// Clear dead
		State clearing = new State("CLR");
		State clearingFailed = new State("CLRF");
		state.add(new InteractSceneObject(new Condition() {
			public boolean validate() {
				return patch.isDead();
			}
		}, clearing, sceneObject, "Clear", true));
		// state.add(new
		// UseItem(Condition.TRUE,curing,123456789,patch.getSceneObject()));
		clearing.add(new Animation(Condition.TRUE, 830, clearing, new Timeout(
				processProducts, 1000)));
		clearing.add(new Animation(Condition.TRUE, -1, clearing, new Timeout(
				processProducts, 2000)));
		clearingFailed.add(new Notification(Condition.TRUE, processProducts,
				"Clearing failed"));

		// harvest
		State preharvestingeqp = new State("PRHRVEQP");
		State checksecateurs = new State("CHECKSEC");
		State preharvesting = new State("PRHRV");
		State harvesting = new State("HARV");
		State harvestingFailed = new State("HARVF");
		state.add(new Edge(new Condition() {
			public boolean validate() {
				return patch.getProgress() >= 1.0;
			}
		}, checksecateurs));
		checksecateurs.add(new Either(new Condition() {
			public boolean validate() {
				return patch.useSecateurs();
			}
		}, preharvestingeqp, preharvesting));
		// check if you should wear secateurs
		preharvestingeqp.add(new Equip(Condition.TRUE, preharvesting,
				preharvesting, Constants.MagicSecateurs, Equipment.WEAPON,
				new Timeout(preharvesting, 5000)));
		preharvesting.add(new InteractSceneObject(Condition.TRUE, harvesting,
				sceneObject, patch.getHarvestingInteraction(), true));

		harvesting.add(new Animation(Condition.TRUE, 2292, harvesting,
				new Timeout(harvestingFailed, 10000)));
		harvesting.add(new Animation(Condition.TRUE, 830, clearing,
				new Timeout(harvestingFailed, 10000)));
		harvesting.add(new Animation(Condition.TRUE, 2282, harvesting,
				new Timeout(harvestingFailed, 10000)));
		harvesting.add(new Edge(new Condition() {
			public boolean validate() {
				return patch.isEmpty();
			}
		}, processProducts));
		harvesting.add(new Timeout(harvestingFailed, 4000));

		harvestingFailed.add(new Notification(Condition.TRUE, processProducts,
				"Harvesting failed"));

		// Plant a seed
		State planting = new State("PLANT");
		State plantingFailed = new State("PLANTF");
		State plantedPre = new State();
		State planted = new State("PLANTED");
		state.add(new Task(new Condition() {
			public boolean validate() {
				return !patch.getRequirement().validate();
			}
		}, state) {
			public void run() {
				System.out.println(patch + " deactivated");
				patch.activated = false;
				FarmingProject.gui.saveSettings();
			}
		});
		state.add(new UseItemWithSceneObject(new Condition() {
			public boolean validate() {
				System.out.println("PLANT!!!!");
				boolean b = false;
				try {
					System.out.println("Empty = " + patch.isEmpty());
					System.out.println("Weeds = " + patch.countWeeds());
					 b = patch.isEmpty() && patch.countWeeds() == 0;
				} catch(Exception e) {
					e.printStackTrace();
				}
				return b;
			}
		}, planting, new Value<Integer>() {
			public Integer get() {
				return patch.selectedSeed.getId();
			}
		}, new Value<SceneObject>() {
			public SceneObject get() {
				return patch.getSceneObject();
			}
		}));
		planting.add(new Animation(Condition.TRUE, 2291, plantedPre,
				new Timeout(plantingFailed, 3000)));
		planting.add(new Timeout(state,3000));
		//planting.add(e)
		plantingFailed.add(new Notification(Condition.TRUE, state,
				"Planting failed"));
		plantedPre.add(new Task(Condition.TRUE, planted) {
			public void run() {
				patch.compost = false;
			}
		});

		// Water patch

		State watering = new State("WATER");
		State wateringFailed = new State("WATERF");
		planted.add(new UseItemWithSceneObject(new Condition() {
			public boolean validate() {
				return patch.canWater() && !patch.isWatered();
			}
		}, watering, Constants.MagicWaterCan, sceneObject));
		watering.add(new Edge(new Condition() {
			public boolean validate() {
				return patch.isWatered();
			}
		}, planted));
		watering.add(new Animation(Condition.TRUE, 2293, planted, new Timeout(
				wateringFailed, 3000)));
		watering.add(new Timeout(wateringFailed, 6000));
		wateringFailed.add(new Notification(Condition.TRUE, planted,
				"Watering failed"));

		// Composting patch and return to 'state'

		State compostCasted = new State("COMPC");
		State composting = new State("COMP");
		State compostingFailed = new State("COMPF");
		State composted = new State("COMPED");
		planted.add(new UseItemWithSceneObject(new Condition() {
			public boolean validate() {
				return !patch.compost;
			}
		}, composting, 6034, sceneObject));
		planted.add(new UseItemWithSceneObject(new Condition() {
			public boolean validate() {
				return !patch.compost;
			}
		}, composting, 6032, sceneObject));
		planted.add(new MagicCast(new Condition() {
			public boolean validate() {
				return !patch.compost;
			}
		}, compostCasted, state, Magic.Lunar.FertileSoil));
		planted.add(new Edge(Condition.TRUE, state));

		composting.add(new Animation(Condition.TRUE, 4413, composted,
				new Timeout(compostingFailed, 8000)));
		composting.add(new Animation(Condition.TRUE, 2283, composted,
				new Timeout(compostingFailed, 8000)));
		composting.add(new Timeout(compostingFailed,5000));
		composted.add(new Task(Condition.TRUE, state) {
			public void run() {
				patch.compost = true;
			}
		});
		compostingFailed.add(new Notification(Condition.TRUE, state,
				"Composting failed"));
		compostCasted.add(new InteractSceneObject(Condition.TRUE, composting,
				sceneObject, "Cast", true));

		processProducts.add(new Task(Condition.TRUE, state) {
			public void run() {
				for (Product product : Product.products.values()) {
					if (product.selectedProcessOption != null && Inventory.getCount(product.getId())>0) {
						product.selectedProcessOption.run(product);
					}
				}
			}
		});

		System.out.println("*** " + patch.getLocation().toString() + "->"
				+ patch.toString() + ":" + state.id);
		System.out.println("Raking:" + raking.id + ", Curing:" + curing.id);
		System.out.println("Clearing:" + clearing.id + ",Harvesting: "
				+ harvesting.id);
		System.out.println("Planting: " + planting.id + ",Composting:"
				+ composting.id);

		return state;
	}

}
