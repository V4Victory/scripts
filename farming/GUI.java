package scripts.farming;

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
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.reflections.Reflections;

import djharvest.Location;
import djharvest.Option;
import djharvest.Patch;
import djharvest.Patches;
import djharvest.ScriptWrapper;
import djharvest.Seed;

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

	String selectedScriptName = null;

	JComboBox<Location> locations = new JComboBox<Location>(Location.locations);
	JComboBox<Option> teleports = new JComboBox<Option>();
	JComboBox<Patch> patches = new JComboBox<Patch>();
	JComboBox<Seed> seeds = new JComboBox<Seed>();
	JComboBox<String> patchTypes = new JComboBox<String>();
	JFormattedTextField patchTypeRating = new JFormattedTextField(
			NumberFormat.getIntegerInstance());
	JComboBox<String> scripts = new JComboBox<String>();
	JButton button = new JButton("Start");

	public GUI(File path) {
		settingsPath = path;
		try {
			loadSettings();
			for (int i = 0; i < Patches.COUNT_TYPES; i++)
				patchTypes.addItem(Patches.getTypeName(i));
			Reflections ref = new Reflections("scripts.wrapper");
			final Set<Class<?>> subTypes = ref
					.getTypesAnnotatedWith(ScriptWrapper.class);
			for (Class<?> myClass : subTypes) {
				System.out.println(myClass.getName());
				scripts.addItem(myClass.getName());
			}
			if (selectedScriptName != null && selectedScriptName.length() > 0)
				scripts.setSelectedItem(selectedScriptName);
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
								+ Patches.getTypeName(type) + " = "
								+ Integer.parseInt(patchTypeRating.getText()));
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
			if (selectedScriptName != null)
				oos.writeObject(selectedScriptName);
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
			selectedScriptName = (String) ois.readObject();
			ois.close();
			fis.close();
			System.out.println(">Finished!");
		} catch (IOException | ClassNotFoundException e) {
			System.out.print("Unable to load settings");
			e.printStackTrace();
		}
	}

}