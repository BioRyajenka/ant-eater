package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.EncodedField;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Util;
import ru.ifmo.ctddev.sushencev.anteater.WorldGenerator;
import ru.ifmo.ctddev.sushencev.anteater.WorldRepeater;

public class ParadiseTest {
	protected JFrame frame;
	private FieldCanvas fieldCanvas;
	private JComboBox<Integer> frameComboBox;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParadiseTest window = new ParadiseTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ParadiseTest() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(400, 200, 803, 499);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);

		JPanel worldTab = new JPanel();
		tabbedPane.addTab("World", null, worldTab, null);

		JLabel descriptionLabel = new JLabel();

		ViewAutomataButton viewAutomataButton = new ViewAutomataButton(frame);

		fieldCanvas = new FieldCanvas(descriptionLabel, viewAutomataButton);

		PlayerPanel playerPanel = PlayerPanel.createInstance();
		frameComboBox = playerPanel.getFrameComboBox();
		playerPanel.setOnFrameSelected(e -> {
			if (frameComboBox.getSelectedIndex() == -1) return;
			int frame = (int) frameComboBox.getSelectedItem();
			world.goToStep(frame);
			fieldCanvas.repaint();
		});
		playerPanel.setOnPlay(play -> {
			viewAutomataButton.setEnabled(!play);
		});

		GroupLayout gl_worldTab = new GroupLayout(worldTab);
		gl_worldTab.setHorizontalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addGap(10).addComponent(
						fieldCanvas, GroupLayout.PREFERRED_SIZE, 541,
						GroupLayout.PREFERRED_SIZE).addPreferredGap(
								ComponentPlacement.UNRELATED).addGroup(gl_worldTab
										.createParallelGroup(Alignment.TRAILING).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.LEADING).addComponent(
																descriptionLabel).addComponent(
																		playerPanel,
																		GroupLayout.PREFERRED_SIZE,
																		209,
																		GroupLayout.PREFERRED_SIZE))
										.addComponent(viewAutomataButton)).addContainerGap()));
		gl_worldTab.setVerticalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addGroup(gl_worldTab
						.createParallelGroup(Alignment.LEADING).addGroup(gl_worldTab
								.createSequentialGroup().addGap(35).addGroup(gl_worldTab
										.createParallelGroup(Alignment.LEADING).addGroup(
												gl_worldTab.createSequentialGroup().addGap(128)
														.addComponent(descriptionLabel))
										.addComponent(fieldCanvas, Alignment.TRAILING,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGroup(gl_worldTab.createSequentialGroup().addContainerGap()
								.addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 120,
										GroupLayout.PREFERRED_SIZE).addPreferredGap(
												ComponentPlacement.RELATED, 229,
												Short.MAX_VALUE).addComponent(
														viewAutomataButton)))
						.addContainerGap()));
		worldTab.setLayout(gl_worldTab);

		JPanel statisticsTab = new JPanel();
		tabbedPane.addTab("Statistics", null, statisticsTab, null);

		StatisticsCanvas statisticsCanvas = new StatisticsCanvas();

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

		JMenuItem newTestMenuItem = new JMenuItem("New test");
		newTestMenuItem.addActionListener((e) -> {
			new CreateParadiseTestFrame(ParadiseTest.this);
		});
		menu.add(newTestMenuItem);

		// about dialog
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener((e) -> {
			JOptionPane.showMessageDialog(frame, "Paradise test");
		});
		menu.add(aboutMenuItem);

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener((e) -> {
			frame.dispose();
		});
		menu.add(exitMenuItem);
	}
	
	private WorldRepeater world;

	protected void publishTest(Individual[] ants, Individual[] antEaters,
			WorldGenerator worldGenerator) {
		List<Individual> antsList = new ArrayList<>(Arrays.asList(ants));
		antsList.addAll(Arrays.asList(antEaters));
		
		Cell[][] field = worldGenerator.generateWorld(ants, antEaters);
		int[] antsRots = new int[antsList.size()];
		for (int i = 0; i < antsList.size(); i++) {
			antsRots[i] = Util.nextInt(4);
		}
		EncodedField encodedField = new EncodedField(field, antsRots, 0, null);
		world = new WorldRepeater(antsList.toArray(new Individual[antsList.size()]), null);
		world.setField(encodedField);
		fieldCanvas.setWorld(world);
		frameComboBox.removeAllItems();
		for (int i = 0; i < 1000; i++) {
			frameComboBox.addItem(i);
		}
		frameComboBox.setSelectedIndex(0);
	}
}