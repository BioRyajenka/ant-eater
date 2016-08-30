package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import ru.ifmo.ctddev.sushencev.anteater.EncodedField;
import ru.ifmo.ctddev.sushencev.anteater.LogLoader;
import ru.ifmo.ctddev.sushencev.anteater.LogLoader.LGeneration;
import ru.ifmo.ctddev.sushencev.anteater.Statistics;
import ru.ifmo.ctddev.sushencev.anteater.Util;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;
import ru.ifmo.ctddev.sushencev.anteater.WorldRepeater;

public class AntEaterVisualizer {
	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AntEaterVisualizer window = new AntEaterVisualizer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public AntEaterVisualizer() {
		initialize();
	}

	private JComboBox<Integer> generationComboBox;
	private JComboBox<Integer> antEaterComboBox;
	private JComboBox<Integer> tryComboBox;
	private JComboBox<Integer> frameComboBox;

	private FieldCanvas fieldCanvas;
	private StatisticsCanvas statisticsCanvas;

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(180, 140, 803, 499);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);

		JPanel worldTab = new JPanel();
		tabbedPane.addTab("World", null, worldTab, null);

		JLabel descriptionLabel = new JLabel("Description");

		ViewAutomataButton viewAutomataButton = new ViewAutomataButton(frame);
		fieldCanvas = new FieldCanvas(descriptionLabel, viewAutomataButton);
		viewAutomataButton.setFieldCanvas(fieldCanvas);

		JLabel generationLabel = new JLabel("Generation");

		generationComboBox = new JComboBox<>();
		generationComboBox.addActionListener(e -> onGenerationSelected());

		JLabel antEaterLabel = new JLabel("Ant-eater");

		antEaterComboBox = new JComboBox<>();
		antEaterComboBox.addActionListener(e -> onAntEaterSelected());

		JLabel tryLabel = new JLabel("Try");

		tryComboBox = new JComboBox<>();
		tryComboBox.addActionListener(e -> onTrySelected());

		PlayerPanel playerPanel = PlayerPanel.createInstance();
		frameComboBox = playerPanel.getFrameComboBox();
		playerPanel.setOnFrameSelected(e -> {
			if (data == null || frameComboBox.getSelectedIndex() == -1)
				return;
			int gen = (int) generationComboBox.getSelectedItem();
			WorldRepeater world = data.get(gen).world;
			int frame = (int) frameComboBox.getSelectedItem();
			world.goToStep(frame);
			fieldCanvas.repaint();
		});
		playerPanel.setOnPlay(play -> {
			generationComboBox.setEnabled(!play);
			tryComboBox.setEnabled(!play);
			viewAutomataButton.setEnabled(!play);
		});

		GroupLayout gl_worldTab = new GroupLayout(worldTab);
		gl_worldTab.setHorizontalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addContainerGap().addComponent(
						fieldCanvas, GroupLayout.PREFERRED_SIZE, 541,
						GroupLayout.PREFERRED_SIZE).addGroup(gl_worldTab.createParallelGroup(
								Alignment.LEADING).addGroup(Alignment.TRAILING, gl_worldTab
										.createSequentialGroup().addGap(10).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.TRAILING).addComponent(
																antEaterLabel,
																GroupLayout.DEFAULT_SIZE, 142,
																Short.MAX_VALUE).addGroup(
																		gl_worldTab
																				.createSequentialGroup()
																				.addComponent(
																						generationLabel,
																						GroupLayout.DEFAULT_SIZE,
																						72,
																						Short.MAX_VALUE)
																				.addGap(70))
														.addComponent(tryLabel,
																GroupLayout.DEFAULT_SIZE, 142,
																Short.MAX_VALUE))
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.LEADING).addComponent(
																tryComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(antEaterComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(generationComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)))
								.addComponent(viewAutomataButton, Alignment.TRAILING).addGroup(
										Alignment.TRAILING, gl_worldTab.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(playerPanel,
														GroupLayout.PREFERRED_SIZE, 209,
														Short.MAX_VALUE)).addGroup(gl_worldTab
																.createSequentialGroup()
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		descriptionLabel)))
						.addContainerGap()));
		gl_worldTab.setVerticalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addContainerGap().addGroup(
						gl_worldTab.createParallelGroup(Alignment.LEADING).addComponent(
								fieldCanvas, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(gl_worldTab
										.createSequentialGroup().addGroup(gl_worldTab
												.createParallelGroup(Alignment.BASELINE)
												.addComponent(generationLabel).addComponent(
														generationComboBox,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.BASELINE).addComponent(
																antEaterLabel).addComponent(
																		antEaterComboBox,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.BASELINE).addComponent(
																tryLabel).addComponent(
																		tryComboBox,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(playerPanel, GroupLayout.PREFERRED_SIZE,
												120, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(descriptionLabel).addPreferredGap(
												ComponentPlacement.RELATED, 115,
												Short.MAX_VALUE).addComponent(
														viewAutomataButton)))
						.addContainerGap()));
		worldTab.setLayout(gl_worldTab);

		JPanel statisticsTab = new JPanel();
		tabbedPane.addTab("Statistics", null, statisticsTab, null);

		statisticsCanvas = new StatisticsCanvas();

		JButton recolorStatisticsButton = new JButton("Recolor");
		recolorStatisticsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statisticsCanvas.recolorStatistics();
			}
		});
		GroupLayout gl_panel = new GroupLayout(statisticsTab);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addGroup(gl_panel.createParallelGroup(
						Alignment.LEADING).addGroup(gl_panel.createSequentialGroup()
								.addContainerGap().addComponent(recolorStatisticsButton))
						.addGroup(gl_panel.createSequentialGroup().addGap(10).addComponent(
								statisticsCanvas, GroupLayout.PREFERRED_SIZE, 756,
								GroupLayout.PREFERRED_SIZE))).addContainerGap(14,
										Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(
				Alignment.LEADING, gl_panel.createSequentialGroup().addContainerGap()
						.addComponent(recolorStatisticsButton).addPreferredGap(
								ComponentPlacement.RELATED).addComponent(statisticsCanvas,
										GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
						.addContainerGap()));
		statisticsTab.setLayout(gl_panel);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menu = new JMenu("File");
		menuBar.add(menu);

		// choose file dialog
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		JMenuItem loadMenuItem = new JMenuItem("Load");
		loadMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerPanel.pause();
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						loadFile(fc.getSelectedFile());
					} catch (IOException | ClassNotFoundException e1) {
						JOptionPane.showMessageDialog(frame, "Can't load file: " + e1);
					}
				}
			}
		});
		menu.add(loadMenuItem);

		// about dialog
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerPanel.pause();
				JOptionPane.showMessageDialog(frame, "Ant-eater visualizer");
			}
		});
		menu.add(aboutMenuItem);

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		menu.add(exitMenuItem);
	}

	private Map<Integer, LGeneration> data;

	private void loadFile(File file) throws IOException, ClassNotFoundException {
		frame.setTitle(file.getName());
		statisticsCanvas.clear();

		Pair<Map<Integer, LGeneration>, Pair<Statistics, Statistics>> res = null;

		try {
			res = LogLoader.loadFile(file);
		} catch (RuntimeException e) {
			e.getCause().printStackTrace();
			System.exit(0);
		}

		data = res.first;

		statisticsCanvas.addStatistics(res.second.first);
		statisticsCanvas.addStatistics(res.second.second);

		statisticsCanvas.repaint();

		//
		generationComboBox.removeAllItems();
		Util.log("loading: " + data.size());
		data.forEach((i, g) -> {
			Util.log("adding generation " + i);
			generationComboBox.addItem(i);
		});
		generationComboBox.setSelectedIndex(0);
	}

	private void onGenerationSelected() {
		if (data == null || generationComboBox.getSelectedIndex() == -1) {
			return;
		}
		antEaterComboBox.removeAllItems();
		int gen = (int) generationComboBox.getSelectedItem();
		data.get(gen).antEaters.forEach((i, ae) -> antEaterComboBox.addItem(i));
		antEaterComboBox.setSelectedIndex(0);
		// not yet implemented
		antEaterComboBox.setEnabled(false);

		WorldRepeater world = data.get(gen).world;
		fieldCanvas.setWorld(world);
	}

	private void onAntEaterSelected() {
		if (data == null || antEaterComboBox.getSelectedIndex() == -1)
			return;
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		tryComboBox.removeAllItems();
		data.get(gen).antEaters.get(aei).tries.forEach((i, tri) -> tryComboBox.addItem(i));
		tryComboBox.setSelectedIndex(0);
	}

	private void onTrySelected() {
		if (data == null || tryComboBox.getSelectedIndex() == -1)
			return;
		frameComboBox.removeAllItems();
		for (int i = 0; i < 100; i++) {
			frameComboBox.addItem(i);
		}
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		int tri = (int) tryComboBox.getSelectedItem();
		EncodedField field = data.get(gen).antEaters.get(aei).tries.get(tri);
		WorldRepeater world = data.get(gen).world;
		world.setField(field);
		frameComboBox.setSelectedIndex(0);
	}
}