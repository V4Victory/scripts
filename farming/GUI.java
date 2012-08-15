package scripts.farming;

import java.awt.Dimension;
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
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.powerbot.game.api.util.Filter;

import scripts.djharvest.ScriptWrapper;
import scripts.farming.modules.RunOtherScript;
import state.Module;

public class GUI extends JFrame {

	boolean isDone_;
	File settingsPath;

	public boolean isDone() {
		return isDone_;
	}

	public Class<?> getSelectedScript() {
		try {
			return Class.forName((String) scripts.getSelectedItem());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public void setSelectedScript(String name) {
		selectedScriptName = name;
	}

	boolean scriptsEnabled = true;
	String selectedScriptName = null;

	JComboBox<Location> locations = new JComboBox<Location>(Location.locations);
	JComboBox<Module> teleports = new JComboBox<Module>();
	JComboBox<String> patchTypes = new JComboBox<String>();
	JFormattedTextField patchTypeRating = new JFormattedTextField(
			NumberFormat.getIntegerInstance());
	JComboBox<String> scripts = new JComboBox<String>();
	JButton button = new JButton("Start");

	public GUI(File path, ScriptLoader loader) {
		settingsPath = path;
		patchTypeRating.setPreferredSize(new Dimension(80, 20));
		try {
			loadSettings();
			for (int i = 0; i < Patches.COUNT_TYPES; i++)
				patchTypes.addItem(Patches.getTypeName(i));
			final Set<Class<?>> subTypes = loader.getScripts();
			for (Class<?> clazz : subTypes) {
				scripts.addItem(clazz.getName());
			}
			if (selectedScriptName != null && selectedScriptName.length() > 0)
				scripts.setSelectedItem(selectedScriptName);

			JPanel locationTab = new JPanel();
			JPanel patchTab = new JPanel();
			JPanel scriptTab = new JPanel();

			JTabbedPane tabs = new JTabbedPane();
			tabs.addTab("Locations", new ImageIcon("images/home_tele.png"),
					locationTab);
			tabs.addTab("Patchs", new ImageIcon("images/farming.png"), patchTab);
			tabs.addTab("Scripts", new ImageIcon("images/minigame.png"),
					scriptTab);
			locations.setMaximumSize(new Dimension(200, 20));
			teleports.setMaximumSize(new Dimension(200, 20));
			scripts.setMaximumSize(new Dimension(220, 20));
			locations.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final Location location = (Location) locations
							.getSelectedItem();

					teleports.removeAllItems();
					if (location == null)
						return;
					Set<Module> options = location.getTeleportOptions();
					if (location.selectedTeleportOption != null) {
						teleports.addItem(location.selectedTeleportOption);
					}
					for (Module option : options) {
						System.out.println(">" + option.toString());
						if (option != location.selectedTeleportOption)
							teleports.addItem(option);
					}
				}
			});
			teleports.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (locations.getSelectedItem() == null)
							return;
						((Location) locations.getSelectedItem()).selectedTeleportOption = (Module) teleports
								.getSelectedItem();
					}
				}
			});

			patchTypeRating.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int type = patchTypes.getSelectedIndex();
					if (type >= 0) {
						Patches.setTypeRating(type,
								Integer.parseInt(patchTypeRating.getText()));
					}
				}
			});

			button.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					setSelectedScript((String) scripts.getSelectedItem());
					saveSettings();
					GUI.this.setVisible(false);
					isDone_ = true;
				}
			});

			// Locations tab
			{
				final JPanel firstRow = new JPanel();
				final JPanel secondRow = new JPanel();

				secondRow.setVisible(false);
				final JCheckBox enable = new JCheckBox("Location activated",
						((Location) locations.getSelectedItem()).activated);
				firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
				firstRow.add(locations);
				Set<Module> options = ((Location) locations.getSelectedItem())
						.getTeleportOptions();
				if (((Location) locations.getSelectedItem()).selectedTeleportOption != null) {
					teleports
							.addItem(((Location) locations.getSelectedItem()).selectedTeleportOption);
				}
				for (Module option : options) {
					if (option != ((Location) locations.getSelectedItem()).selectedTeleportOption)
						teleports.addItem(option);
				}
				secondRow
						.setVisible(((Location) locations.getSelectedItem()).activated);
				locations.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent itemevent) {
						if (itemevent.getSource() != null)
							enable.setVisible(true);
					}
				});
				enable.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						Location location = (Location) locations
								.getSelectedItem();
						if (e.getStateChange() == ItemEvent.DESELECTED) {
							location.activated = false;
						} else if (e.getStateChange() == ItemEvent.SELECTED) {
							location.activated = true;
						}
						secondRow.setVisible(location.activated);
					}
				});
				firstRow.add(enable);
				secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
				secondRow.add(teleports);
				secondRow.add(new JLabel("Choose Teleport"));
				locationTab.setLayout(new BoxLayout(locationTab,
						BoxLayout.Y_AXIS));
				locationTab.add(firstRow);
				locationTab.add(secondRow);
			}

			// Patchs Tab
			{
				patchTab.setLayout(new BoxLayout(patchTab, BoxLayout.Y_AXIS));
				final JPanel[] rows = new JPanel[] { new JPanel(), new JPanel() };
				rows[0].setLayout(new BoxLayout(rows[0], BoxLayout.X_AXIS));
				rows[1].setLayout(new BoxLayout(rows[1], BoxLayout.Y_AXIS));
				rows[0].setMaximumSize(new Dimension(200, 20));

				rows[0].add(patchTypes);
				rows[0].add(new JLabel(" Score: "));
				rows[0].add(patchTypeRating);
				patchTypes.setVisible(true);
				patchTypeRating.setVisible(true);
				int type = patchTypes.getSelectedIndex();
				if (type >= 0) {
					patchTypeRating.setText(String.valueOf(Patches
							.getTypeRating(type)));
					for (final Patch patch : Patches.patches.values()) {
						if (patch.getType() == type) {
							JPanel row = new JPanel();
							createPatchRow(row, patch);
							rows[1].add(row);
						}
					}
				}
				rows[1].validate();
				final JScrollPane scroll = new JScrollPane(rows[1]);
				patchTypes.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							rows[1].removeAll();
							int type = patchTypes.getSelectedIndex();
							if (type >= 0) {
								patchTypeRating.setText(String.valueOf(Patches
										.getTypeRating(type)));
								for (final Patch patch : Patches.patches
										.values()) {
									if (patch.getType() == type) {
										JPanel row = new JPanel();
										createPatchRow(row, patch);
										rows[1].add(row);
									}
								}
							}
							rows[1].validate();
							scroll.validate();
						}
					}
				});
				patchTab.add(rows[0]);
				patchTab.add(scroll);
			}

			// Script Tab
			{
				final JPanel firstRow = new JPanel();
				final JPanel secondRow = new JPanel();

				secondRow.setVisible(scriptsEnabled);
				JCheckBox enable = new JCheckBox("Alternative script enabled",
						scriptsEnabled);
				enable.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.DESELECTED) {
							scriptsEnabled = false;
						} else if (e.getStateChange() == ItemEvent.SELECTED) {
							scriptsEnabled = true;
						}
						secondRow.setVisible(scriptsEnabled);
					}
				});
				firstRow.add(enable);
				secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
				secondRow.add(new JLabel("Choose a script: "));
				// secondRow.add(Box.createRigidArea(new Dimension(5,0)));
				secondRow.add(scripts);

				scriptTab.setLayout(new BoxLayout(scriptTab, BoxLayout.Y_AXIS));
				scriptTab.add(firstRow);
				scriptTab.add(secondRow);
				scriptTab.add(button);
			}

			this.add(tabs);
			this.pack();
			this.setVisible(true);
			this.setResizable(false);
			this.setLocation(50, 50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createPatchRow(JPanel row, final Patch patch) {
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		JLabel label = new JLabel(patch.getLocation().toString() + ": ");
		label.setPreferredSize(new Dimension(120, 20));
		row.add(label);
		JCheckBox activated = new JCheckBox("Active", patch.activated);
		activated.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					patch.activated = false;
				} else if (e.getStateChange() == ItemEvent.SELECTED) {
					patch.activated = true;
				}
			}
		});
		List<Seed> seeds = Seed.getSeeds(new Filter<Seed>() {
			public boolean accept(Seed s) {
				return s.getType() == patch.getType();
			}
		});
		final JComboBox<Seed> seed = new JComboBox<Seed>(
				new Vector<Seed>(seeds));
		seed.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					patch.selectedSeed = (Seed) seed.getSelectedItem();
				}
			}
		});
		seed.setSelectedItem(patch.selectedSeed);
		row.add(activated);
		row.add(seed);
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
				oos.writeBoolean(location.activated);
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
				oos.writeBoolean(patch.activated);
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
			if (selectedScriptName != null)
				oos.writeObject(selectedScriptName);
			else
				oos.writeObject(new String(""));
			oos.writeBoolean(scriptsEnabled);
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
			//System.out.print("Load locations");
			for (int i = 0; i < l; i++) {
				boolean activated = ois.readBoolean();
				String lname = (String) ois.readObject();
				String ltele = (String) ois.readObject();
				Location location = Location.getLocation(lname);
				location.activated = activated;
				if (location != null) {
				//	System.out.println(ltele);
					for (Module option : location.getTeleportOptions()) {
						System.out.println(option.toString());
						if (option.toString().equals(ltele)) {
							System.out.println(location.name + "-" + location.selectedTeleportOption + "-" + option);
							location.selectedTeleportOption = option;
						}
					}

					if(location.selectedTeleportOption == null)
						location.selectedTeleportOption = location.getTeleportOptions().iterator().next();
				}
			//	System.out.print(".");
			}
			//System.out.println("");
			//System.out.print("Load patches");
			l = ois.readInt();
			for (int i = 0; i < l; i++) {
				int id = ois.readInt();
				boolean activated = ois.readBoolean();
				int seedId = ois.readInt();
				Patch patch = Patches.patches.get(id);
				patch.activated = activated;
				if (patch != null && seedId != 0) {
					patch.selectedSeed = Seed.seeds.get(seedId);
				}
				System.out.print(".");
			}
			l = ois.readInt();
			for (int i = 0; i < l; i++) {
				Patches.setTypeRating(i, ois.readInt());
			}
			selectedScriptName = (String) ois.readObject();
			scriptsEnabled = ois.readBoolean();
			ois.close();
			fis.close();
			System.out.println(">Finished!");
		} catch (IOException | ClassNotFoundException e) {
			System.out.print("Unable to load settings");
			e.printStackTrace();
		}
	}

}