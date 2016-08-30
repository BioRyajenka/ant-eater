package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.LogLoader;
import ru.ifmo.ctddev.sushencev.anteater.LogLoader.LGeneration;
import ru.ifmo.ctddev.sushencev.anteater.RandomWorldGenerator;
import ru.ifmo.ctddev.sushencev.anteater.Statistics;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;
import ru.ifmo.ctddev.sushencev.anteater.WorldGenerator;

public class CreateParadiseTestFrame {
	private JFrame jf;
	private ParadiseTest parent;
	private JTextField loadedFileTextField;
	private DefaultListModel<Individual> chosenAntsListModel = new DefaultListModel<>();
	private DefaultListModel<Individual> loadedAntsListModel = new DefaultListModel<>();
	private JList<Individual> loadedAntsList;
	private JButton okButton;
	private JButton removeIndividualButton;
	private JComboBox<WorldType> worldTypeComboBox;

	private enum WorldType {
		RANDOM_50(new RandomWorldGenerator(25, 25, 0.50)),
		RANDOM_25(new RandomWorldGenerator(25, 25, 0.25)),
		RANDOM_10(new RandomWorldGenerator(25, 25, 0.10)),
		RANDOM_05(new RandomWorldGenerator(25, 25, 0.05));

		private WorldGenerator worldGenerator;

		WorldType(WorldGenerator worldGenerator) {
			this.worldGenerator = worldGenerator;
		}

		private WorldGenerator getWorldGenerator() {
			return worldGenerator;
		}

		@Override
		public String toString() {
			return worldGenerator.getClass().getSimpleName();
		}
	}

	/**
	 * Create the application.
	 */
	public CreateParadiseTestFrame(ParadiseTest parent) {
		this.parent = parent;
		parent.frame.setEnabled(false);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		jf = new JFrame("New test");
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				parent.frame.setEnabled(true);
			}
		});
		jf.setBounds(100, 100, 452, 448);
		jf.setLocation(parent.frame.getX() + parent.frame.getWidth() / 2 - jf.getWidth() / 2,
				parent.frame.getY() + parent.frame.getHeight() / 2 - jf.getHeight() / 2);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// jf.setAlwaysOnTop(true);

		JLabel worldTypeLabel = new JLabel("World type");

		worldTypeComboBox = new JComboBox<>();
		Arrays.stream(WorldType.values()).forEach(v -> worldTypeComboBox.addItem(v));

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

		JButton loadFileButton = new JButton("Load file");
		loadFileButton.addActionListener(e -> {
			int returnVal = fc.showOpenDialog(jf);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				loadFile(fc.getSelectedFile());
			}
		});

		JScrollPane loadedAntsScrollPane = new JScrollPane();

		JScrollPane chosenAntsScrollPane = new JScrollPane();

		JList<Individual> chosenAntsList = new JList<>(chosenAntsListModel);
		chosenAntsScrollPane.setViewportView(chosenAntsList);
		chosenAntsList.setCellRenderer(new MyListRenderer());
		loadedAntsList = new JList<>(loadedAntsListModel);
		loadedAntsScrollPane.setViewportView(loadedAntsList);
		loadedAntsList.setCellRenderer(new MyListRenderer());

		JLabel savedAntsLabel = new JLabel("Saved");

		JLabel chosenAntsLabel = new JLabel("Chosen");

		JButton addIndividualButton = new JButton("Add");
		addIndividualButton.addActionListener(e -> {
			int ind = loadedAntsList.getSelectedIndex();
			addIndividual(loadedAntsList.getSelectedValue());
			if (ind < loadedAntsList.getModel().getSize()) {
				loadedAntsList.setSelectedIndex(ind);
			}
			//okButton.setEnabled(!chosenAnts.isEmpty());
		});
		loadedAntsList.addListSelectionListener(e -> {
			boolean selected = loadedAntsList.getSelectedIndex() != -1;
			addIndividualButton.setEnabled(selected);
			if (selected) {
				chosenAntsList.clearSelection();
			}
		});

		removeIndividualButton = new JButton("Remove");
		removeIndividualButton.addActionListener(e -> {
			int ind = chosenAntsList.getSelectedIndex();
			removeIndividual(chosenAntsList.getSelectedValue());
			if (ind < chosenAntsList.getModel().getSize()) {
				chosenAntsList.setSelectedIndex(ind);
			}
		});
		chosenAntsList.addListSelectionListener(e -> {
			boolean selected = chosenAntsList.getSelectedIndex() != -1;
			removeIndividualButton.setEnabled(selected);
			if (selected) {
				loadedAntsList.clearSelection();
			}
		});

		loadedFileTextField = new JTextField();
		loadedFileTextField.setText("Nothing loaded");
		loadedFileTextField.setColumns(10);
		loadedFileTextField.setEnabled(false);
		loadedFileTextField.setHorizontalAlignment(JTextField.CENTER);

		okButton = new JButton("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(e -> {
			WorldGenerator wg = ((WorldType) worldTypeComboBox.getSelectedItem())
					.getWorldGenerator();
			parent.publishTest(chosenAnts.toArray(new Individual[chosenAnts.size()]),
					chosenAntEaters.toArray(new Individual[chosenAntEaters.size()]), wg);
			jf.dispatchEvent(new WindowEvent(jf, WindowEvent.WINDOW_CLOSING));
		});

		JButton renameIndividualButton = new JButton("Rename");
		renameIndividualButton.addActionListener(e -> {
			Individual i = null;
			if (!loadedAntsList.isSelectionEmpty()) {
				i = loadedAntsList.getSelectedValue();
			} else {
				i = chosenAntsList.getSelectedValue();
			}
			String name = JOptionPane.showInputDialog(jf, "Enter new name", i.getName());
			if (name != null) {
				i.setName(name);
			}
			loadedAntsList.repaint();
			chosenAntsList.repaint();
		});

		GroupLayout groupLayout = new GroupLayout(jf.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
						.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
								.createSequentialGroup().addGroup(groupLayout
										.createParallelGroup(Alignment.LEADING).addGroup(
												groupLayout.createSequentialGroup()
														.addContainerGap().addGroup(groupLayout
																.createParallelGroup(
																		Alignment.LEADING)
																.addComponent(worldTypeLabel)
																.addComponent(loadFileButton)))
										.addGroup(groupLayout.createSequentialGroup().addGap(
												70).addComponent(savedAntsLabel)))
								.addPreferredGap(ComponentPlacement.RELATED).addGroup(
										groupLayout.createParallelGroup(Alignment.LEADING)
												.addGroup(groupLayout.createParallelGroup(
														Alignment.TRAILING).addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				worldTypeComboBox,
																				0, 311,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED))
														.addComponent(loadedFileTextField,
																GroupLayout.DEFAULT_SIZE, 311,
																Short.MAX_VALUE)).addGroup(
																		Alignment.TRAILING,
																		groupLayout
																				.createSequentialGroup()
																				.addComponent(
																						chosenAntsLabel)
																				.addGap(57))))
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addComponent(loadedAntsScrollPane, GroupLayout.PREFERRED_SIZE,
										158, GroupLayout.PREFERRED_SIZE).addPreferredGap(
												ComponentPlacement.RELATED).addGroup(
														groupLayout.createParallelGroup(
																Alignment.LEADING)
																.addComponent(
																		addIndividualButton,
																		GroupLayout.DEFAULT_SIZE,
																		79, Short.MAX_VALUE)
																.addComponent(
																		renameIndividualButton,
																		GroupLayout.DEFAULT_SIZE,
																		82, Short.MAX_VALUE)
																.addComponent(
																		removeIndividualButton,
																		GroupLayout.DEFAULT_SIZE,
																		82, Short.MAX_VALUE)
																.addComponent(okButton,
																		GroupLayout.DEFAULT_SIZE,
																		79, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(
										chosenAntsScrollPane, GroupLayout.PREFERRED_SIZE, 158,
										GroupLayout.PREFERRED_SIZE))).addGap(34)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(
						groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(
								worldTypeLabel).addComponent(worldTypeComboBox,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)).addPreferredGap(
												ComponentPlacement.RELATED).addGroup(
														groupLayout.createParallelGroup(
																Alignment.BASELINE)
																.addComponent(loadFileButton)
																.addComponent(
																		loadedFileTextField,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(
										savedAntsLabel).addComponent(chosenAntsLabel))
						.addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout
								.createParallelGroup(Alignment.LEADING, false).addComponent(
										loadedAntsScrollPane, GroupLayout.PREFERRED_SIZE, 295,
										GroupLayout.PREFERRED_SIZE).addGroup(groupLayout
												.createSequentialGroup().addGap(80)
												.addComponent(renameIndividualButton)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(addIndividualButton)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(removeIndividualButton)
												.addPreferredGap(ComponentPlacement.RELATED,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE).addComponent(
																okButton)).addComponent(
																		chosenAntsScrollPane,
																		GroupLayout.PREFERRED_SIZE,
																		295,
																		GroupLayout.PREFERRED_SIZE))
						.addGap(32)));

		jf.getContentPane().setLayout(groupLayout);
		jf.setVisible(true);
	}

	private class MyListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 4153693845471283808L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object i, int index,
				boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, i, index, isSelected,
					cellHasFocus);
			setText(((Individual) i).getName());
			return c;
		}
	}

	private void loadFile(File file) {
		Pair<Map<Integer, LGeneration>, Pair<Statistics, Statistics>> res = null;
		loadedFileTextField.setText(file.getName());

		try {
			res = LogLoader.loadFile(file);
		} catch (RuntimeException e) {
			e.getCause().printStackTrace();
			System.exit(0);
		}

		Map<Integer, LGeneration> data = res.first;
		int max = data.keySet().stream().max(Integer::compareTo).get();
		Individual[] ants = data.get(max).world.getAnts();
		Individual[] antEaters = data.get(max).world.getAntEaters();

		loadedAnts = new ArrayList<>(Arrays.asList(ants));
		loadedAntEaters = new ArrayList<>(Arrays.asList(antEaters));

		final String prefix = file.getName().substring(0, 8) + " ";
		loadedAnts.forEach(a -> a.setName(prefix + a.getName()));
		for (int i = 0; i < loadedAnts.size(); i++) {
			initalPositions.put(loadedAnts.get(i), i);
		}
		loadedAntEaters.forEach(a -> a.setName(prefix + a.getName()));
		for (int i = 0; i < loadedAntEaters.size(); i++) {
			initalPositions.put(loadedAntEaters.get(i), i);
		}

		updateLists();

		loadedAntsList.setSelectedIndex(0);
		removeIndividualButton.setEnabled(false);
	}

	private void updateLists() {
		updateLoadedList();
		updateChosenList();
	}

	private Map<Individual, Integer> initalPositions = new HashMap<>();

	private int getId(Individual i) {
		return initalPositions.get(i);
	}

	private void updateLoadedList() {
		loadedAntsListModel.clear();
		loadedAnts.sort((a, b) -> Integer.compare(getId(a), getId(b)));
		loadedAnts.forEach(i -> loadedAntsListModel.addElement(i));
		loadedAntEaters.sort((a, b) -> Integer.compare(getId(a), getId(b)));
		loadedAntEaters.forEach(i -> loadedAntsListModel.addElement(i));
	}

	private void updateChosenList() {
		chosenAntsListModel.clear();
		chosenAnts.forEach(i -> chosenAntsListModel.addElement(i));
		chosenAntEaters.forEach(i -> chosenAntsListModel.addElement(i));
	}

	private List<Individual> loadedAnts = new ArrayList<>();
	private List<Individual> loadedAntEaters = new ArrayList<>();
	private List<Individual> chosenAnts = new ArrayList<>();
	private List<Individual> chosenAntEaters = new ArrayList<>();

	private void addIndividual(Individual i) {
		okButton.setEnabled(true);
		if (i.isAntEater()) {
			chosenAntEaters.add(i);
			loadedAntEaters.remove(i);
		} else {
			chosenAnts.add(i);
			loadedAnts.remove(i);
		}
		updateLists();
	}

	private void removeIndividual(Individual i) {
		okButton.setEnabled(chosenAntsListModel.getSize() != 0);
		if (i.isAntEater()) {
			loadedAntEaters.add(i);
			chosenAntEaters.remove(i);
		} else {
			loadedAnts.add(i);
			chosenAnts.remove(i);
		}
		updateLists();
	}
}