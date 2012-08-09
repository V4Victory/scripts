package scripts.djharvest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(name = "DJHarvest", authors = "djabby", version = 1.00, description = "does farming")
public class DJHarvest extends ActiveScript implements PaintListener {
	boolean setupGUI = false,setup = false;
	Timer timer;
	public File settingsPath;
	public Properties settings = new Properties();
	GUI gui;
	Class<?> alternateScript = null;
	List<Strategy> newStrategies;
	String loadedScriptName;
	ActiveScript otherScript = null;
	RunScriptWhileWaiting scriptStrategy = new RunScriptWhileWaiting();
	boolean setupOtherScript = false;

	
	protected void setup() {
		provide(new Teleport());
		provide(new Work());
		provide(new RemoteFarm());
		provide(new SetupItems());
		provide(scriptStrategy);


		settingsPath = new File(Environment.getStorageDirectory(), Players
				.getLocal().getName() + "-djharvest-settings.ini");
		System.out.println(settingsPath);

		/* load and save settings */
		loadSettings();
		gui = new GUI();
	}

	/** processes farming products, examples:
	 *  - clean herbs and let the leprechaun note them
	 *  - burn logs
	 *  - ...
	 */
	public void processProducts() {
		for (Product product : Product.products.values()) {
			if (product.selectedProcessOption != null) {
				product.selectedProcessOption.run(product);
			}
		}
	}

	/** teleport and run to the next farming location that has work **/
	public class Teleport extends Strategy implements Task {

		public void run() {
			Location loc = null;
			Integer mostWorkCount = 0;
			// select the location with the most work to do
			for (Location location : Location.locations) {
				if (location.isActivated()
						&& mostWorkCount < location.countWork(false)) {
					mostWorkCount = location.countWork(false);
					loc = location;
				}
			}
			System.out.print("Most work (" + mostWorkCount + ") in ");
			if (loc != null) {
				System.out.println(loc.toString());
				Option teleport = loc.selectedTeleportOption;
				System.out.println("Use teleport: "
						+ teleport
						+ !loc.getArea().contains(
								Players.getLocal().getLocation()));
				Item mudstaff = Inventory.getItem(6562);
				if (mudstaff != null)
					mudstaff.getWidgetChild().click(true);
				Camera.setAngle('n');
				if (!loc.getArea().contains(Players.getLocal().getLocation()))
					teleport.run();
			}
		}

		public boolean validate() {
			if (!setup || (new Work().validate()) || scriptStrategy.isRunning)
				return false;
			for (Location location : Location.locations) {
				if (location.isActivated() && location.countWork(false) > 0)
					return true;
			}
			return false;
		}

	}


	/** Works at one patch: rakes, clears, cures, harvests, plants **/
	public class Work extends Strategy implements Task, Condition {
		public void run() {
			System.out.println("Work");
			for (Location location : Location.locations) {
				if (location.isActivated()
						&& location.getArea().contains(
								Players.getLocal().getLocation())) {
					System.out.println("Work in " + location.toString());
					for (Patch patch : Patches.patches.values()) {
						if (patch.getLocation() == location
								&& patch.isActivated()) {
							if (patch.isDead() || patch.isDiseased()
									|| patch.getProgress() >= 1.0
									|| patch.isEmpty()) {
								Walking.walk(patch.getSceneObject()
										.getLocation());
								Time.sleep(1300);
								while (Players.getLocal().isMoving())
									Time.sleep(20);
								/** Clean up **/
								final SceneObject object = patch.getSceneObject();
								Camera.turnTo(object);
								if (object == null)
									continue;
								if (patch.isDead()) {
									System.out.println("Patch "
											+ patch.toString() + " is dead");
									Mouse.move(object.getCentralPoint());
									object.interact("Clear");
									Time.sleep(1900);
								} else if (patch.isDiseased()) {
									System.out
											.println("Patch "
													+ patch.toString()
													+ " is diseased");
									Item mudstaff = Inventory.getItem(6562);
									if (mudstaff != null)
										mudstaff.getWidgetChild().click(true);
									Time.sleep(700);
									Magic.cast(Magic.Lunar.CurePlant);
									Time.sleep(700);
									Mouse.click(object.getCentralPoint(),true);
									Time.sleep(1300);
									
									// if(patch.diseaseOption != null)
									// patch.diseaseOption.run(patch);
								} else if (patch.countWeeds() > 0) {
									System.out.println("Patch "
											+ patch.toString()
											+ " is full of weeds");
									Mouse.click(object.getCentralPoint(),true);
									Time.sleep(700 + 1800 * patch.countWeeds());
									processProducts();
								}
								if (!patch.isEmpty()
										&& patch.getProgress() >= 1.0) {
									if (patch.useSecateurs()) {
										Item secateurs = Inventory
												.getItem(7409);
										if (secateurs != null)
											secateurs.getWidgetChild()
													.interact("Wield");
										Time.sleep(1900);
									}
									switch (patch.getType()) {
									case Patches.Herb:
									case Patches.Flower:
										Mouse.click(object.getCentralPoint(),true);
										Time.sleep(1300);
										while (!patch.isEmpty()
												&& Inventory.getCount() < 28) {
											Time.sleep(20);
										}
										break;
									case Patches.Allotment:
										Mouse.click(object.getCentralPoint(),true);
										Time.sleep(1300);
										while (!patch.isEmpty()
												&& Inventory.getCount() < 28) {
											Time.sleep(20);
										}
										break;
									case Patches.Tree:
										Mouse.click(object.getCentralPoint(),true);
										Time.sleep(700);
										while (Players.getLocal()
												.getAnimation() == 879)
											Time.sleep(20);
										Mouse.click(object.getCentralPoint(),true);
										break;
									}
								}
								if (patch.isEmpty()) {
									if (patch.selectedSeed != null) {
										if (patch.selectedSeed.compost || true) {
											Item mudstaff = Inventory
													.getItem(6562);
											if (mudstaff != null)
												mudstaff.getWidgetChild()
														.click(true);
											Time.sleep(1300);
											Magic.cast(Magic.Lunar.FertileSoil);
											Time.sleep(700);
											Mouse.click(object.getCentralPoint(),false);
											waitFor(new Condition() { 
												public boolean validate() { return Menu.isOpen(); }
											});
											Menu.select("Cast");
											Time.sleep(1900);
										}
										Item seed = Inventory
												.getItem(patch.selectedSeed
														.getId());
										if (seed != null) {
											seed.getWidgetChild().click(true);
											Time.sleep(1300);
											Mouse.click(object.getCentralPoint(),true);

											Time.sleep(2300);
											if (patch.canWater()) {
												Item watercan = Inventory
														.getItem(18682);
												if (watercan != null) {
													watercan.getWidgetChild()
															.click(true);
													Time.sleep(1300);
													Mouse.click(object.getCentralPoint(),true);										
													Menu.select("Use");
													Time.sleep(1900);
												}
											}
										}
									}
								}
								processProducts();
								return;
							}
						}
					}
					break;
				}
			}
		}

		public boolean validate() {
			if (!setup || scriptStrategy.isRunning)
				return false;
			for (Location location : Location.locations) {
				if (location.isActivated()
						&& location.getArea().contains(
								Players.getLocal().getLocation())) {
					for (Patch patch : Patches.patches.values()) {
						if (patch.getLocation() == location) {
							if (patch.isActivated()
									&& (patch.isDead()
											|| patch.getProgress() >= 1.0 || patch
												.isEmpty()))
								return true;
						}
					}
					break;
				}
			}
			return false;
		}
	}

	/** cure patchs via remote farm */
	public class RemoteFarm extends Strategy implements Task, Condition {
		public void run() {
			System.out.println("Start remote farm");
			if (!Widgets.get(1082, 4).isOnScreen())
				Magic.cast(Magic.Lunar.RemoteFarm);
			Time.sleep(4500);
			System.out.println("Cure all diseased");
			Magic.cureAllDiseased();
			System.out.println("End remote farm");
		}

		public boolean validate() {
			if (!setup || (new Work().validate()))
				return false;
			boolean b = false;
			for (Location location : Location.locations) {
				if (location.isActivated()) {
					if (location.countWork(false) > 0)
						return false;
					else if (location.countWork(true) > 0)
						b = true;
				}
			}
			return b;
		}
	}

	/** interface for the user to choose settings:
	 * - activate farming locations and patchs
	 * - choose teleport options
	 * - choose seeds
	 * - assign to each patch type a rating (e.g. herb->4,flower->1,...)
	 *   when a summed rating of 10 is reached (e.g. 2 herb patchs and 3 flower patchs have work),
	 *   then interrupt the alternative script and do the farming run
	 * - chooose an alternative script (wrapper)
	 * @author David Jablonski
	 *
	 */
	public class GUI extends JFrame {

		JComboBox<Location> locations = new JComboBox<Location>(
				Location.locations);
		JComboBox<Option> teleports = new JComboBox<Option>();
		JComboBox<Patch> patches = new JComboBox<Patch>();
		JComboBox<Seed> seeds = new JComboBox<Seed>();
		JComboBox<String> patchTypes = new JComboBox<String>();
		JFormattedTextField patchTypeRating = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		JComboBox<String> scripts = new JComboBox<String>();
		JButton button = new JButton("Start");

		public GUI() {
			try {
				for (int i = 0; i < Patches.COUNT_TYPES; i++)
					patchTypes.addItem(Patches.getTypeName(i));
				final List<Class<?>> classes = DJHarvest.getClassesForPackage("");
				final Set<Class<?>> subTypes = new HashSet<Class<?>>();
				for(Class<?> clazz : classes) {
					if(clazz.getAnnotation(ScriptWrapper.class) != null) {
						subTypes.add(clazz);
					}
				}
				for (Class<?> myClass : subTypes) {
					System.out.println(myClass.getName());
					scripts.addItem(myClass.getName());
				}
				if (loadedScriptName != null && loadedScriptName.length() > 0)
					scripts.setSelectedItem(loadedScriptName);
				System.out.println("Finished loading script wrappers");
				locations.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						final Location location = (Location) locations
								.getSelectedItem();

						int si = patches.getSelectedIndex();
						teleports.removeAllItems();
						patches.removeAllItems();
						if (location == null)
							return;
						Option[] options = location.getTeleportOptions();
						if (location.selectedTeleportOption != null) {
							teleports.addItem(location.selectedTeleportOption);
						}
						for (Option option : options) {
							if (option != location.selectedTeleportOption)
								teleports.addItem(option);
						}
						for (Patch patch : Patches.patches.values()) {
							if (patch.getLocation() == location)
								patches.addItem(patch);
						}
						if (patches.getItemCount() > si && si >= 0)
							patches.setSelectedIndex(si);
					}
				});
				teleports.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							if (locations.getSelectedItem() == null)
								return;
							((Location) locations.getSelectedItem()).selectedTeleportOption = (Option) teleports
									.getSelectedItem();
						}
					}
				});
				patches.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							Patch patch = (Patch) patches.getSelectedItem();
							int si = seeds.getSelectedIndex();
							seeds.removeAllItems();
							if (patch == null)
								return;
							if (patch.selectedSeed != null) {
								seeds.addItem(patch.selectedSeed);
							}
							for (Seed seed : Seed.seeds.values()) {
								if (seed.getType() == patch.getType()
										&& seed != patch.selectedSeed) {
									seeds.addItem(seed);
								}
							}
							if (patch.selectedSeed == null
									&& seeds.getItemCount() > si && si >= 0) {
								seeds.setSelectedIndex(si);
								patch.selectedSeed = seeds.getItemAt(si);
								System.out.println(">>>" + patch + ":"
										+ (Seed) seeds.getSelectedItem());
							}
						}
					}
				});
				seeds.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							Patch patch = (Patch) patches.getSelectedItem();
							if (patch == null)
								return;
							System.out.println(patch + ":"
									+ (Seed) seeds.getSelectedItem());
							patch.selectedSeed = (Seed) seeds.getSelectedItem();
						}
					}
				});
				patchTypes.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							int type = patchTypes.getSelectedIndex();
							if (type >= 0) {
								patchTypeRating.setText(String.valueOf(Patches
										.getTypeRating(type)));
							}
						}
					}
				});
				patchTypeRating.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int type = patchTypes.getSelectedIndex();
						if (type >= 0) {
							System.out.println("Set rating for "
									+ Patches.getTypeName(type)
									+ " = "
									+ Integer.parseInt(patchTypeRating
											.getText()));
							Patches.setTypeRating(type,
									Integer.parseInt(patchTypeRating.getText()));
						}
					}
				});

				button.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						try {
							alternateScript = Class.forName((String) scripts
									.getSelectedItem());
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
						saveSettings();
						GUI.this.setVisible(false);
						setupGUI = true;
						timer = new Timer(0);
					}
				});

				if (locations.getItemCount() > 0)
					locations.setSelectedItem(0);
				System.out.println("Load Panels");
				JPanel panel = new JPanel();
				JPanel panel2 = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
				panel.add(locations);
				panel.add(teleports);
				panel.add(patches);
				panel.add(seeds);
				panel.add(button);
				panel2.add(patchTypes);
				panel2.add(patchTypeRating);
				panel2.add(scripts);

				System.out.println("Set Layouts");
				JPanel mainpanel = new JPanel();
				mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.X_AXIS));
				mainpanel.add(panel);
				mainpanel.add(panel2);
				this.add(mainpanel);
				this.pack();
				this.setVisible(true);
				this.setResizable(false);
				this.setLocation(50, 50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void saveSettings() {
		try {
			if (!settingsPath.exists())
				settingsPath.createNewFile();
			System.out.println("Start saving...");
			FileOutputStream fos = new FileOutputStream(settingsPath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(Location.locations.length);
			System.out.print("Save locations");
			for (Location location : Location.locations) {
				oos.writeObject(location.name);
				oos.writeObject(location.selectedTeleportOption == null ? ""
						: location.selectedTeleportOption.toString());
				System.out.print(".");
			}
			oos.writeInt(Patches.patches.size());
			System.out.println("");
			System.out.print("Save patches");
			for (Patch patch : Patches.patches.values()) {
				oos.writeInt(patch.getId());
				if (patch.selectedSeed != null)
					oos.writeInt(patch.selectedSeed.getId());
				else
					oos.writeInt(0);
				System.out.print(".");
			}
			oos.writeInt(Patches.COUNT_TYPES);
			for (int i = 0; i < Patches.COUNT_TYPES; i++) {
				oos.writeInt(Patches.getTypeRating(i));
			}
			if (alternateScript != null)
				oos.writeObject(alternateScript.getName());
			else
				oos.writeObject(new String(""));
			oos.close();
			fos.close();
			System.out.println(">Finished!");
		} catch (IOException e) {
			System.out.print("Unable to save settings");
			e.printStackTrace();
		}
	}

	public void loadSettings() {
		try {
			if (!settingsPath.exists())
				return;
			System.out.println("Start loading...");
			FileInputStream fis = new FileInputStream(settingsPath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			int l = ois.readInt();
			System.out.print("Load locations");
			for (int i = 0; i < l; i++) {
				String lname = (String) ois.readObject();
				String ltele = (String) ois.readObject();
				Location location = Location.getLocation(lname);
				System.out.println(lname);
				if (location != null) {
					System.out.println(ltele);
					for (Option option : location.getTeleportOptions()) {
						System.out.println(option.toString());
						if (option.toString().equals(ltele))
							location.selectedTeleportOption = option;
					}
				}
				System.out.print(".");
			}
			System.out.println("");
			System.out.print("Load patches");
			l = ois.readInt();
			for (int i = 0; i < l; i++) {
				int id = ois.readInt();
				int seedId = ois.readInt();
				Patch patch = Patches.patches.get(id);
				if (patch != null && seedId != 0) {
					patch.selectedSeed = Seed.seeds.get(seedId);
				}
				System.out.print(".");
			}
			l = ois.readInt();
			for (int i = 0; i < l; i++) {
				Patches.setTypeRating(i, ois.readInt());
			}
			loadedScriptName = (String) ois.readObject();
			ois.close();
			fis.close();
			System.out.println(">Finished!");
		} catch (IOException | ClassNotFoundException e) {
			System.out.print("Unable to load settings");
			e.printStackTrace();
		}
	}

	/** when there is no farming work anymore,
	 * start the alternative script until it 
	 * receives an interrupt (when summed work rating > 10)
	 *
	 */
	private final class RunScriptWhileWaiting extends Strategy implements Task, Condition {
		
		/** a dual condition, necessary to inject our interrupting condition into the
		 * strategies of the alternative script
		 *
		 */
		public abstract class DelegateCondition implements Condition {
			Condition policy_;
			public DelegateCondition(Condition policy) {
				policy_ = policy;
			}
			public boolean validate() {
				return customValidate() && policy_.validate();
			}
			public abstract boolean customValidate();
		}

		boolean isRunning = false;

		@Override
		public void run() {

			try {
				if (!isRunning) {
					isRunning = true;

					ScriptWrapper a = alternateScript
							.getAnnotation(ScriptWrapper.class);



					System.out.println("Prepare wrapper");
					alternateScript.getDeclaredMethod("prepare").invoke(null);

					
					otherScript = (ActiveScript) alternateScript
							.getDeclaredMethod("getInstance").invoke(null);
					
					
					Field contextField = ActiveScript.class
							.getDeclaredField("context");

					contextField.setAccessible(true);
					Context context = (Context) contextField
							.get(DJHarvest.this);
					Field botField = Context.class.getDeclaredField("bot");
					botField.setAccessible(true);
					Bot bot = (Bot) botField.get(context);
					Context newContext = new Context(bot);
					otherScript.init(newContext);
					
					if(setupOtherScript == false || true) {
						Method setupMethod = otherScript.getClass().getDeclaredMethod(
								"setup");
	
						setupMethod.setAccessible(true);
						setupMethod.invoke(otherScript);
						setupOtherScript = true;
					}
					Field executorField = ActiveScript.class
							.getDeclaredField("executor");
					executorField.setAccessible(true);
					StrategyDaemon sd = (StrategyDaemon) executorField
							.get(otherScript);
					Field strategiesField = StrategyDaemon.class
							.getDeclaredField("strategies");
					strategiesField.setAccessible(true);

					List<Strategy> strategies = (List<Strategy>) strategiesField
							.get(sd);

					newStrategies = new ArrayList<Strategy>();

					for (Strategy strategy : strategies) {

						Field policyField = Strategy.class
								.getDeclaredField("policy");
						policyField.setAccessible(true);
						final Condition condition = (Condition) policyField
								.get(strategy);
						Field tasksField = Strategy.class
								.getDeclaredField("tasks");
						tasksField.setAccessible(true);
						final Task[] tasks = (Task[]) tasksField.get(strategy);
						Strategy newStrategy;
						DJHarvest.this.provide(newStrategy = new Strategy(
								new DelegateCondition(condition) {
									@Override
									public boolean customValidate() {
										return Patches.countAllWork() < 10;
									}
								}, tasks));
						newStrategies.add(newStrategy);
					}
				} else {
					System.out.println("Cleanup wrapper");
					for (Strategy strategy : newStrategies) {
						DJHarvest.this.revoke(strategy);
					}
					if((boolean)alternateScript.getDeclaredMethod("cleanup").invoke(null))
						setup = false;
					Time.sleep(3100, 3300);
					isRunning = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean validate() {
			if (isRunning) return Patches.countAllWork() >= 10;
			boolean valid = !(new Teleport()).validate()
					&& !(new Work()).validate()
					&& !(new RemoteFarm()).validate();
			if (!setup || alternateScript == null)
				valid = false;
			return valid;

		}

	}
	
	
	/** withdraw necessary farming items at the beginning 
	 * and after interrupting the alternative script
	 */
	public class SetupItems extends Strategy implements Task, Condition {
		public void run() {
			boolean valid = false;
			valid |= Inventory.getCount(4251) == 0;
			valid |= Inventory.getCount(18682) == 0;
			valid |= Inventory.getCount(7409) == 0;
			valid |= Inventory.getCount(19760) == 0;
			valid |= Inventory.getCount(6562) == 0;
			valid |= Inventory.getCount(20175) == 0;
			valid |= Inventory.getCount(561) == 0;
			valid |= Inventory.getCount(563) == 0;
			valid |= Inventory.getCount(9075) == 0;
			for (Patch patch : Patches.patches.values()) {
				if (patch.selectedSeed != null
						&& patch.selectedSeed.getId() > 0) {
					valid |= Inventory.getCount(patch.selectedSeed.getId()) == 0;
				}
			}
			
			if(!valid || !Bank.open()) {
				setup = true;
				return;
			}
			
			// ank.setWithdrawNoted(true);
			Bank.withdraw(4251, 1);
			Time.sleep(200);
			Bank.withdraw(18682, 1);
			Time.sleep(200);
			Bank.withdraw(7409, 1);
			Time.sleep(200);
			Bank.withdraw(19760, 1);
			Time.sleep(200);
			Bank.withdraw(6562, 1);
			Time.sleep(200);
			Bank.withdraw(20175, 0);
			Time.sleep(200);
			Bank.withdraw(561, 0);
			Time.sleep(200);
			Bank.withdraw(563, 0);
			Time.sleep(200);
			Bank.withdraw(9075, 0);
			for (Patch patch : Patches.patches.values()) {
				if (patch.selectedSeed != null
						&& patch.selectedSeed.getId() > 0) {
					Bank.withdraw(patch.selectedSeed.getId(), 0);
					Time.sleep(200);
				}
			}
			Bank.close();
			setup = true;
		}
		public boolean validate() {
			return setupGUI && !setup && !scriptStrategy.isRunning;
		}
	}
	
	public static void waitFor(Condition c) {
		while(!c.validate()) Time.sleep(1);
	}
	
	@Override
	public void onRepaint(Graphics g) {
		if(!setupGUI) return;
		/** inject the alternative script's painting into ours **/
		if(scriptStrategy.isRunning && otherScript != null) {
			for(Class<?> i : otherScript.getClass().getInterfaces()) {
				if(i.getName().equals("org.powerbot.game.bot.event.listener.PaintListener")) {
					PaintListener paint = (PaintListener) otherScript;
					paint.onRepaint(g);
					g.setColor(Color.YELLOW);
					g.fillRect(5, 5, 110, 30);
					g.setColor(Color.BLACK);
					g.drawString("Farming work: " + Patches.countAllWork(),7,18);
					g.drawString("Time: " + timer.toElapsedString(),7,33);
					return;
				}
			}
		}
		int y = 5;
		g.setColor(Color.YELLOW);
		g.fillRect(5, 5, 110, 15);
		g.setColor(Color.BLACK);
		g.drawString("Time: " + timer.toElapsedString(),7,18);
		y+=15;
		g.setFont(new Font("Arial", Font.BOLD, 12));
		for (Location location : Location.locations) {
			if(!location.isActivated()) continue;
			g.setColor(Color.YELLOW);
			g.fillRect(5, y, 200, 15);
			g.setColor(Color.BLACK);
			g.drawString(location.toString()
					+ (location.selectedTeleportOption == null ? "" : " - "
							+ location.selectedTeleportOption), 10, y + 10);
			y += 15;
			for (Patch patch : Patches.patches.values()) {
				if(!patch.isActivated()) continue;
				if (patch.getLocation() == location) {
					g.setColor(Color.YELLOW);
					g.fillRect(5, y, 200, 15);
					g.setColor(Color.RED);
					g.fillRect(20, y + 2, 100, 11);
					int width = (int) Math.round(patch.getProgress() * 100);
					if (patch.isDiseased()) {
						g.setColor(Color.GRAY);
						g.fillRect(20, y + 2, 100, 11);
					} else if (patch.isDead()) {
						g.setColor(Color.BLACK);
						g.fillRect(20, y + 2, 100, 11);
					} else if (patch.isWatered()) {
						g.setColor(Color.BLUE);
						g.fillRect(20, y + 2, Math.min(100,width), 11);
					} else if (patch.countWeeds() > 0) {
						g.setColor(Color.GREEN);
						g.fillRect(20, y + 2, 100, 11);
					} else {
						g.setColor(new Color(0, 128, 0));
						g.fillRect(20, y + 2, Math.min(100,width), 11);
					}
					g.setColor(Color.BLACK);
					g.drawString(patch.getCorrespondingSeed() == null ? "Weed"
							: patch.getCorrespondingSeed().toString(), 130,
							y + 10);
					y += 15;
				}
			}
		}
	}
	
	private static List<Class<?>> getClassesForPackage(String pckgname) {
		// This will hold a list of directories matching the pckgname. There may
		// be more than one if a package is split over multiple jars/paths
		ArrayList<File> directories = new ArrayList<File>();
		try {
			try {
				ClassLoader cld = Thread.currentThread()
						.getContextClassLoader();
				if (cld == null) {
					throw new ClassNotFoundException("Can't get class loader.");
				}
				String path = pckgname.replace('.', '/');
				// Ask for all resources for the path
				Enumeration<URL> resources = cld.getResources(path);
				while (resources.hasMoreElements()) {
					directories.add(new File(URLDecoder.decode(resources
							.nextElement().getPath(), "UTF-8")));
				}
			} catch (NullPointerException x) {
				throw new ClassNotFoundException(
						pckgname
								+ " does not appear to be a valid package (Null pointer exception)");
			} catch (UnsupportedEncodingException encex) {
				throw new ClassNotFoundException(
						pckgname
								+ " does not appear to be a valid package (Unsupported encoding)");
			} catch (IOException ioex) {
				throw new ClassNotFoundException(
						"IOException was thrown when trying to get all resources for "
								+ pckgname);
			}

			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
			// For every directory identified capture all the .class files
			for (File directory : directories) {
				if (directory.exists()) {
					// Get the list of the files contained in the package
					String[] files = directory.list();
					for (String file : files) {
						// we are only interested in .class files
						if (file.endsWith(".class")) {
							// removes the .class extension
							try {
								classes.add(Class.forName(pckgname + '.'
										+ file.substring(0, file.length() - 6)));
							} catch (NoClassDefFoundError e) {
								// do nothing. this class hasn't been found by
								// the
								// loader, and we don't care.
							}
						}
					}
				} else {
					throw new ClassNotFoundException(pckgname + " ("
							+ directory.getPath()
							+ ") does not appear to be a valid package");
				}
			}
			return classes;
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());// reflection.getTypesAnnotatedWith(annotation);
			return new ArrayList<Class<?>>();
		}
	}
}
