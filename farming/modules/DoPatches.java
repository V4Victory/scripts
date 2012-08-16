package scripts.farming.modules;

import org.powerbot.game.api.wrappers.node.SceneObject;

import scripts.djharvest.Product;
import scripts.farming.Equipment;
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
import scripts.state.edge.Equip;
import scripts.state.edge.InteractItem;
import scripts.state.edge.InteractSceneObject;
import scripts.state.edge.MagicCast;
import scripts.state.edge.Notification;
import scripts.state.edge.Task;
import scripts.state.edge.Timeout;
import scripts.state.edge.UseItemWithSceneObject;

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
			req.and(new Requirement(0, new Value<Integer>() {
				public Integer get() {
					return (patch.activated && patch.selectedSeed != null) ? patch.selectedSeed
							.getId() : 0;
				}
			}));
		}
		System.out.println("Seed requirements for " + loc + ":" + req);
		return req;
	}

	public DoPatches(Location loc, State initial, State success, State critical) {
		super("Do Patches", initial, success, critical, new Requirement[] {
				DoPatches.getSeedRequirements(loc),
				new Requirement(0, Constants.AstralRune,true),
				new Requirement(0, Constants.NatureRune,true),
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
		rakingFailed.add(new Notification(Condition.TRUE, processProducts,
				"Raking failed"));

		// Cure disease
		state.add(new InteractItem(new Condition() {
			public boolean validate() {
				return patch.isDiseased();
			}
		}, state, Constants.MudBattleStaff, "Wield"));
		State cureCasted = new State("CURC");
		State curing = new State("CUR");
		State curingFailed = new State("CURF");
		state.add(new MagicCast(new Condition() {
			public boolean validate() {
				return patch.isDiseased();
			}
		}, cureCasted, state, Magic.Lunar.CurePlant));
		cureCasted.add(new InteractSceneObject(Condition.TRUE, curing,
				sceneObject, "Cast", true));
		curing.add(new Animation(Condition.TRUE, 2273, state, new Timeout(
				curingFailed, 3000)));
		curingFailed.add(new Notification(Condition.TRUE, state,
				"Curing failed"));

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
		clearingFailed.add(new Notification(Condition.TRUE, processProducts,
				"Clearing failed"));

		// harvest
		State preharvesting = new State();
		State harvesting = new State("HARV");
		State harvestingFailed = new State("HARVF");
		state.add(new Edge(new Condition() {
			public boolean validate() {
				return patch.getProgress() >= 1.0;
			}
		}, preharvesting));
		// check if you should wear secateurs
		preharvesting.add(new Equip(new Condition() {
			public boolean validate() {
				return patch.useSecateurs();
			}
		}, preharvesting, Constants.MagicSecateurs, Equipment.WEAPON,
				new Timeout(preharvesting, 5000)));
		preharvesting.add(new InteractSceneObject(Condition.TRUE, harvesting,
				sceneObject, patch.getHarvestingInteraction(), true));

		harvesting.add(new Animation(Condition.TRUE, 2292, processProducts,
				new Timeout(harvestingFailed, 10000)));
		harvesting.add(new Animation(Condition.TRUE, 830, clearing,
				new Timeout(harvestingFailed, 3000)));
		harvesting.add(new Animation(Condition.TRUE, 2282, processProducts,
				new Timeout(harvestingFailed, 10000)));

		harvestingFailed.add(new Notification(Condition.TRUE, processProducts,
				"Harvesting failed"));

		// Plant a seed
		State planting = new State("PLANT");
		State plantingFailed = new State("PLANTF");
		State plantedPre = new State();
		State planted = new State("PLANTED");
		state.add(new UseItemWithSceneObject(new Condition() {
			public boolean validate() {
				return patch.isEmpty() && patch.countWeeds() == 0;
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
		watering.add(new Animation(Condition.TRUE, 2293, planted, new Timeout(
				wateringFailed, 3000)));
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
		planted.add(new Edge(Condition.TRUE,state));

		composting.add(new Animation(Condition.TRUE, 4413, composted,
				new Timeout(compostingFailed, 8000)));
		composting.add(new Animation(Condition.TRUE, 2283, composted,
				new Timeout(compostingFailed, 8000)));
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
					if (product.selectedProcessOption != null) {
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
