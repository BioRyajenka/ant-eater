package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;

import ru.ifmo.ctddev.sushencev.anteater.World;

public class AntEaterVisualizator {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AntEaterVisualizator window = new AntEaterVisualizator();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AntEaterVisualizator() {
		initialize();
	}

	private JComboBox<Integer> generationComboBox;
	private JComboBox<Integer> antEaterComboBox;
	private JComboBox<Integer> tryComboBox;
	private JComboBox<Integer> frameComboBox;
	
	private FieldCanvas canvas;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 797, 499);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel worldTab = new JPanel();
		tabbedPane.addTab("World", null, worldTab, null);

		canvas = new FieldCanvas();

		JLabel generationLabel = new JLabel("Generation");

		generationComboBox = new JComboBox<>();

		JLabel antEaterLabel = new JLabel("Ant-eater");

		antEaterComboBox = new JComboBox<>();

		JLabel tryLabel = new JLabel("Try");

		tryComboBox = new JComboBox<>();

		JLabel frameLabel = new JLabel("Frame");

		frameComboBox = new JComboBox<>();

		JButton playButton = new JButton("Play");

		JButton pauseButton = new JButton("Pause");

		JButton stopButton = new JButton("Stop");

		JSpinner frameSpinner = new JSpinner();
		Component mySpinnerEditor = frameSpinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor)
				.getTextField();
		jftf.setColumns(2);

		// from 0 to 9, in 1.0 steps start value 5
		SpinnerNumberModel model = new SpinnerNumberModel(5.0, 0.0, 9.0, 1.0);
		frameSpinner.setModel(model);

		JLabel playSpeedLabel = new JLabel("Play speed");

		JButton prevFrameButton = new JButton("Prev frame");

		JButton nextFrameButton = new JButton("Next frame");
		GroupLayout gl_worldTab = new GroupLayout(worldTab);
		gl_worldTab.setHorizontalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addGap(10).addComponent(
						canvas, GroupLayout.PREFERRED_SIZE, 541,
						GroupLayout.PREFERRED_SIZE).addGroup(gl_worldTab
								.createParallelGroup(Alignment.TRAILING).addGroup(
										gl_worldTab.createSequentialGroup().addGap(10)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.TRAILING)
														.addGroup(gl_worldTab
																.createSequentialGroup()
																.addGroup(gl_worldTab
																		.createParallelGroup(
																				Alignment.LEADING)
																		.addComponent(
																				antEaterLabel,
																				GroupLayout.DEFAULT_SIZE,
																				134,
																				Short.MAX_VALUE)
																		.addComponent(
																				generationLabel,
																				Alignment.TRAILING,
																				GroupLayout.DEFAULT_SIZE,
																				134,
																				Short.MAX_VALUE)
																		.addComponent(
																				tryLabel,
																				GroupLayout.DEFAULT_SIZE,
																				134,
																				Short.MAX_VALUE)
																		.addComponent(
																				frameLabel,
																				GroupLayout.DEFAULT_SIZE,
																				134,
																				Short.MAX_VALUE))
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addGroup(gl_worldTab
																		.createParallelGroup(
																				Alignment.LEADING)
																		.addComponent(
																				frameComboBox,
																				GroupLayout.PREFERRED_SIZE,
																				62,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				tryComboBox,
																				GroupLayout.PREFERRED_SIZE,
																				62,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				antEaterComboBox,
																				GroupLayout.PREFERRED_SIZE,
																				62,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				generationComboBox,
																				GroupLayout.PREFERRED_SIZE,
																				62,
																				GroupLayout.PREFERRED_SIZE)))
														.addGroup(gl_worldTab
																.createSequentialGroup()
																.addComponent(
																		playSpeedLabel)
																.addPreferredGap(
																		ComponentPlacement.RELATED,
																		106,
																		Short.MAX_VALUE)
																.addComponent(
																		frameSpinner,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
														.addGroup(gl_worldTab
																.createSequentialGroup()
																.addGap(1).addComponent(
																		playButton,
																		GroupLayout.DEFAULT_SIZE,
																		56,
																		Short.MAX_VALUE)
																.addGap(9).addComponent(
																		pauseButton,
																		GroupLayout.DEFAULT_SIZE,
																		68,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(stopButton,
																		GroupLayout.DEFAULT_SIZE,
																		60,
																		Short.MAX_VALUE))))
								.addGroup(gl_worldTab.createSequentialGroup()
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(prevFrameButton,
												GroupLayout.DEFAULT_SIZE, 95,
												Short.MAX_VALUE).addGap(9).addComponent(
														nextFrameButton,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)))
						.addContainerGap()));
		gl_worldTab.setVerticalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addContainerGap().addGroup(
						gl_worldTab.createParallelGroup(Alignment.LEADING).addComponent(
								canvas, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
										gl_worldTab.createSequentialGroup().addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.BASELINE).addComponent(
																generationLabel)
														.addComponent(generationComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(
														ComponentPlacement.RELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(antEaterLabel)
														.addComponent(antEaterComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(
														ComponentPlacement.RELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(tryLabel)
														.addComponent(tryComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(
														ComponentPlacement.RELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(frameLabel)
														.addComponent(frameComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(
														ComponentPlacement.UNRELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(pauseButton)
														.addComponent(stopButton)
														.addComponent(playButton))
												.addPreferredGap(
														ComponentPlacement.RELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(frameSpinner,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(playSpeedLabel))
												.addPreferredGap(
														ComponentPlacement.RELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(nextFrameButton)
														.addComponent(prevFrameButton))))
						.addContainerGap()));
		worldTab.setLayout(gl_worldTab);

		JPanel statisticsTab = new JPanel();
		tabbedPane.addTab("Statistics", null, statisticsTab, null);

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

	private Map<Integer, Generation> data;

	private static class Generation {
		Map<Integer, AntEater> antEaters;
	}

	private static class AntEater {
		Map<Integer, Try> tries;
	}

	private static class Try {
		Map<Integer, Frame> frames;
	}

	private static class Frame {
		Map<String, Integer> description;
		World world;

		public Frame(Map<String, Integer> description, World world) {
			this.description = description;
			this.world = world;
		}
	}

	@SuppressWarnings("unchecked")
	private void loadFile(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		//

		applyPresets((Map<String, Integer>) ois.readObject());

		List<Frame> temp = new ArrayList<>();

		// reading all of the frames
		while (ois.available() != 0) {
			Map<String, Integer> description = (Map<String, Integer>) ois.readObject();
			World world = (World) ois.readObject();
			temp.add(new Frame(description, world));
		}

		// sorting them

		data = new HashMap<>();
		temp.forEach(f -> addFrame(f));

		//

		generationComboBox.removeAllItems();
		data.forEach((i, g) -> generationComboBox.addItem(i));
		generationComboBox.setSelectedIndex(0);
		setGeneration((int) generationComboBox.getSelectedItem());

		//
		ois.close();
		fis.close();
	}

	private void addFrame(Frame f) {
		int gen = f.description.get("generation");
		data.compute(gen, (k, v) -> {
			if (v == null) {
				v = new Generation();
				v.antEaters = new HashMap<>();
			}
			addFrameToGeneration(v, f);
			return v;
		});
	}

	private void addFrameToGeneration(Generation g, Frame f) {
		int aei = f.description.get("ant-eater");
		g.antEaters.compute(aei, (k, v) -> {
			if (v == null) {
				v = new AntEater();
				v.tries = new HashMap<>();
			}
			addFrameToAntEater(v, f);
			return v;
		});
	}

	private void addFrameToAntEater(AntEater ae, Frame f) {
		int tri = f.description.get("try");
		ae.tries.compute(tri, (k, v) -> {
			if (v == null) {
				v = new Try();
				v.frames = new HashMap<>();
			}
			v.frames.put(f.description.get("frame"), f);
			return v;
		});
	}

	private void setGeneration(int gen) {
		antEaterComboBox.removeAllItems();
		data.get(gen).antEaters.forEach((i, ae) -> antEaterComboBox.addItem(i));
		antEaterComboBox.setSelectedIndex(0);
		setAntEater((int) antEaterComboBox.getSelectedItem());
	}

	private void setAntEater(int aei) {
		int gen = (int) generationComboBox.getSelectedItem();
		tryComboBox.removeAllItems();
		data.get(gen).antEaters.get(aei).tries.forEach((i, tri) -> tryComboBox.addItem(
				i));
		tryComboBox.setSelectedIndex(0);
		setTry((int) tryComboBox.getSelectedItem());
	}

	private void setTry(int tri) {
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		frameComboBox.removeAllItems();
		data.get(gen).antEaters.get(aei).tries.get(tri).frames.forEach((i,
				f) -> frameComboBox.addItem(i));
		frameComboBox.setSelectedIndex(0);
		setFrame((int) frameComboBox.getSelectedItem());
	}

	private void setFrame(int fri) {
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		int tri = (int) tryComboBox.getSelectedItem();
		Frame frame = data.get(gen).antEaters.get(aei).tries.get(tri).frames.get(fri);
		canvas.setField(frame.world.getField());
	}

	private void applyPresets(Map<String, Integer> readObject) {

	}
}