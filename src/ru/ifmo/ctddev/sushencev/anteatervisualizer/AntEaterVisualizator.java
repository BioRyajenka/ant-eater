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
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ru.ifmo.ctddev.sushencev.anteater.World;

public class AntEaterVisualizator {
	private JFrame frame;

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

	public AntEaterVisualizator() {
		initialize();
	}

	private JComboBox<Integer> generationComboBox;
	private JComboBox<Integer> antEaterComboBox;
	private JComboBox<Integer> tryComboBox;
	private JComboBox<Integer> frameComboBox;
	
	private JButton playButton;
	private JButton pauseButton;
	
	private JButton prevFrameButton;
	private JButton nextFrameButton;

	private FieldCanvas canvas;
	
	private enum PlayState {
		PLAY, PAUSE;
	}
	
	private PlayState playState;

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 803, 499);
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
		generationComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGeneration();
			}
		});

		JLabel antEaterLabel = new JLabel("Ant-eater");

		antEaterComboBox = new JComboBox<>();
		antEaterComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAntEater();
			}
		});

		JLabel tryLabel = new JLabel("Try");

		tryComboBox = new JComboBox<>();
		tryComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTry();
			}
		});

		JLabel frameLabel = new JLabel("Frame");

		frameComboBox = new JComboBox<>();
		frameComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFrame();
			}
		});

		Timer timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playState == PlayState.PAUSE) {
					return;
				}
				int i = frameComboBox.getSelectedIndex();
				if (i + 1 < frameComboBox.getItemCount()) {
					frameComboBox.setSelectedIndex(i + 1);
				} else {
					pause();
				}
			}
		});
		timer.start();

		JSpinner frameSpinner = new JSpinner();
		Component mySpinnerEditor = frameSpinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor)
				.getTextField();
		jftf.setColumns(2);

		// from 0 to 9, in 1.0 steps start value 5
		SpinnerNumberModel model = new SpinnerNumberModel(1.0, 1.0, 10.0, 1.0);
		frameSpinner.setModel(model);
		frameSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				timer.setDelay((int) (1000 / (double) frameSpinner.getValue()));
			}
		});

		JLabel playSpeedLabel = new JLabel("Play speed, fps");

		prevFrameButton = new JButton("Prev frame");
		prevFrameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = frameComboBox.getSelectedIndex();
				if (i > 0) {
					frameComboBox.setSelectedIndex(i - 1);
				}
			}
		});

		nextFrameButton = new JButton("Next frame");
		nextFrameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = frameComboBox.getSelectedIndex();
				if (i + 1 < frameComboBox.getItemCount()) {
					frameComboBox.setSelectedIndex(i + 1);
				}
			}
		});

		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});

		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pause();
			}
		});

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pause();
				frameComboBox.setSelectedIndex(0);
			}
		});
		
		GroupLayout gl_worldTab = new GroupLayout(worldTab);
		gl_worldTab.setHorizontalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addGap(10).addComponent(
						canvas, GroupLayout.PREFERRED_SIZE, 541,
						GroupLayout.PREFERRED_SIZE).addGroup(gl_worldTab
								.createParallelGroup(Alignment.LEADING).addGroup(
										gl_worldTab.createSequentialGroup().addGap(10)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(antEaterLabel,
																GroupLayout.DEFAULT_SIZE,
																145, Short.MAX_VALUE)
														.addComponent(generationLabel,
																Alignment.TRAILING,
																GroupLayout.DEFAULT_SIZE,
																145, Short.MAX_VALUE)
														.addComponent(tryLabel,
																GroupLayout.DEFAULT_SIZE,
																145, Short.MAX_VALUE)
														.addComponent(frameLabel,
																GroupLayout.DEFAULT_SIZE,
																145, Short.MAX_VALUE))
												.addPreferredGap(
														ComponentPlacement.RELATED)
												.addGroup(gl_worldTab
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(frameComboBox,
																GroupLayout.PREFERRED_SIZE,
																62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(tryComboBox,
																GroupLayout.PREFERRED_SIZE,
																62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(antEaterComboBox,
																GroupLayout.PREFERRED_SIZE,
																62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(generationComboBox,
																GroupLayout.PREFERRED_SIZE,
																62,
																GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_worldTab.createSequentialGroup()
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(playSpeedLabel).addPreferredGap(
												ComponentPlacement.RELATED, 117,
												Short.MAX_VALUE).addComponent(
														frameSpinner,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_worldTab.createSequentialGroup().addGap(10)
										.addComponent(playButton,
												GroupLayout.PREFERRED_SIZE, 61,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(pauseButton,
												GroupLayout.PREFERRED_SIZE, 75,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(stopButton,
												GroupLayout.DEFAULT_SIZE, 60,
												Short.MAX_VALUE)).addGroup(gl_worldTab
														.createSequentialGroup()
														.addPreferredGap(
																ComponentPlacement.RELATED)
														.addComponent(prevFrameButton,
																GroupLayout.DEFAULT_SIZE,
																101, Short.MAX_VALUE)
														.addGap(9).addComponent(
																nextFrameButton,
																GroupLayout.DEFAULT_SIZE,
																102, Short.MAX_VALUE)))
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
														.addComponent(playButton)
														.addComponent(stopButton)
														.addComponent(pauseButton))
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
				pause();
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
		while (true) {
			try {
				Map<String, Integer> description = (Map<String, Integer>) ois
						.readObject();
				World world = (World) ois.readObject();
				temp.add(new Frame(description, world));
			} catch (Exception e) {
				break;
			}
		}

		// sorting them
		data = new HashMap<>();
		temp.forEach(f -> addFrame(f));

		//
		generationComboBox.removeAllItems();
		data.forEach((i, g) -> generationComboBox.addItem(i));
		generationComboBox.setSelectedIndex(0);

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

	private void updateGeneration() {
		if (data == null || generationComboBox.getSelectedIndex() == -1)
			return;
		antEaterComboBox.removeAllItems();
		int gen = (int) generationComboBox.getSelectedItem();
		data.get(gen).antEaters.forEach((i, ae) -> antEaterComboBox.addItem(i));
		antEaterComboBox.setSelectedIndex(0);
	}

	private void updateAntEater() {
		if (data == null || antEaterComboBox.getSelectedIndex() == -1)
			return;
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		tryComboBox.removeAllItems();
		data.get(gen).antEaters.get(aei).tries.forEach((i, tri) -> tryComboBox.addItem(
				i));
		tryComboBox.setSelectedIndex(0);
	}

	private void updateTry() {
		if (data == null || tryComboBox.getSelectedIndex() == -1)
			return;
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		int tri = (int) tryComboBox.getSelectedItem();
		frameComboBox.removeAllItems();
		data.get(gen).antEaters.get(aei).tries.get(tri).frames.forEach((i,
				f) -> frameComboBox.addItem(i));
		frameComboBox.setSelectedIndex(0);
	}

	private void updateFrame() {
		if (data == null || frameComboBox.getSelectedIndex() == -1)
			return;
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		int tri = (int) tryComboBox.getSelectedItem();
		int fri = (int) frameComboBox.getSelectedItem();
		Frame frame = data.get(gen).antEaters.get(aei).tries.get(tri).frames.get(fri);
		canvas.setWorld(frame.world);
	}

	private void applyPresets(Map<String, Integer> readObject) {

	}
	
	private void enableComponents(boolean play) {
		pauseButton.setEnabled(play);
		playButton.setEnabled(!play);
		generationComboBox.setEnabled(!play);
		antEaterComboBox.setEnabled(!play);
		tryComboBox.setEnabled(!play);
		frameComboBox.setEnabled(!play);
		prevFrameButton.setEnabled(!play);
		nextFrameButton.setEnabled(!play);
	}
	
	private void pause() {
		playState = PlayState.PAUSE;
		enableComponents(false);
	}
	
	private void play() {
		playState = PlayState.PLAY;
		enableComponents(true);
	}
}