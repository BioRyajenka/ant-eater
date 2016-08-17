package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import ru.ifmo.ctddev.sushencev.anteater.Automata;
import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Automata.State;
import ru.ifmo.ctddev.sushencev.anteater.ElitisticSelectionStrategy;
import ru.ifmo.ctddev.sushencev.anteater.EncodedField;
import ru.ifmo.ctddev.sushencev.anteater.EncodedGeneration;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.LogLoader;
import ru.ifmo.ctddev.sushencev.anteater.ProportionalSelectionStrategy;
import ru.ifmo.ctddev.sushencev.anteater.SelectionStrategy;
import ru.ifmo.ctddev.sushencev.anteater.Statistics;
import ru.ifmo.ctddev.sushencev.anteater.Util;
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

	private JButton playButton;
	private JButton pauseButton;

	private JButton prevFrameButton;
	private JButton nextFrameButton;

	private FieldCanvas fieldCanvas;
	private StatisticsCanvas statisticsCanvas;

	private enum PlayState {
		PLAY, PAUSE;
	}

	private PlayState playState;

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

		JButton viewAutomataButton = new JButton("View automata");
		viewAutomataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAutomataFrame(fieldCanvas.getSelectedAutomata());
			}
		});

		fieldCanvas = new FieldCanvas(descriptionLabel, viewAutomataButton);

		JLabel generationLabel = new JLabel("Generation");

		generationComboBox = new JComboBox<>();
		generationComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectGeneration();
			}
		});

		JLabel antEaterLabel = new JLabel("Ant-eater");

		antEaterComboBox = new JComboBox<>();
		antEaterComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAntEater();
			}
		});

		JLabel tryLabel = new JLabel("Try");

		tryComboBox = new JComboBox<>();
		tryComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTry();
			}
		});

		JLabel frameLabel = new JLabel("Frame");

		frameComboBox = new JComboBox<>();
		frameComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectFrame();
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
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(2);

		// from 0 to 9, in 1.0 steps start value 5
		SpinnerNumberModel model = new SpinnerNumberModel(10.0, 1.0, 50.0, 1.0);
		frameSpinner.setModel(model);
		frameSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				timer.setDelay((int) (1000 / (double) frameSpinner.getValue()));
			}
		});

		timer.setDelay(1000 / (int) model.getNumber().doubleValue());

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

		JButton predictSelectionButton = new JButton("Predict selection");
		predictSelectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame newFrame = new JFrame();
				JLabel label = new JLabel();
				newFrame.add(label);
				newFrame.setBounds(frame.getX() + frame.getWidth(), frame.getY(), 418, frame
						.getHeight());
				newFrame.setVisible(true);
				
				float crossingoverProbability = .3f;
				float mutationProbability = .3f;

				// SelectionStrategy selectionStrategy = new TwoRandomSelectionStrategy(
				// 		crossingoverProbability, mutationProbability);
				SelectionStrategy selectionStrategy = new ProportionalSelectionStrategy(
						crossingoverProbability, mutationProbability);
				selectionStrategy = new ElitisticSelectionStrategy(selectionStrategy, 3);

				int gen = (int) generationComboBox.getSelectedItem();
				Individual[] ants = data.get(gen).world.getAnts();
				selectionStrategy.doSelectionAndMutation(ants);
			}
		});

		GroupLayout gl_worldTab = new GroupLayout(worldTab);
		gl_worldTab.setHorizontalGroup(gl_worldTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_worldTab.createSequentialGroup().addContainerGap().addComponent(
						fieldCanvas, GroupLayout.PREFERRED_SIZE, 541,
						GroupLayout.PREFERRED_SIZE).addGroup(gl_worldTab.createParallelGroup(
								Alignment.TRAILING).addGroup(gl_worldTab.createParallelGroup(
										Alignment.LEADING).addGroup(gl_worldTab
												.createSequentialGroup().addGap(10).addGroup(
														gl_worldTab.createParallelGroup(
																Alignment.LEADING)
																.addComponent(antEaterLabel,
																		GroupLayout.DEFAULT_SIZE,
																		146, Short.MAX_VALUE)
																.addComponent(generationLabel,
																		Alignment.TRAILING,
																		GroupLayout.DEFAULT_SIZE,
																		146, Short.MAX_VALUE)
																.addComponent(tryLabel,
																		GroupLayout.DEFAULT_SIZE,
																		146, Short.MAX_VALUE)
																.addComponent(frameLabel,
																		GroupLayout.DEFAULT_SIZE,
																		146, Short.MAX_VALUE))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(gl_worldTab.createParallelGroup(
														Alignment.LEADING).addComponent(
																frameComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(tryComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(antEaterComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(generationComboBox,
																GroupLayout.PREFERRED_SIZE, 62,
																GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_worldTab.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(playSpeedLabel).addPreferredGap(
														ComponentPlacement.RELATED, 82,
														Short.MAX_VALUE).addComponent(
																frameSpinner,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_worldTab.createSequentialGroup().addGap(
												10).addComponent(playButton,
														GroupLayout.PREFERRED_SIZE, 61,
														GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(pauseButton,
														GroupLayout.PREFERRED_SIZE, 75,
														GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(stopButton,
														GroupLayout.DEFAULT_SIZE, 63,
														Short.MAX_VALUE)).addGroup(gl_worldTab
																.createSequentialGroup()
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(prevFrameButton,
																		GroupLayout.DEFAULT_SIZE,
																		102, Short.MAX_VALUE)
																.addGap(9).addComponent(
																		nextFrameButton,
																		GroupLayout.DEFAULT_SIZE,
																		102, Short.MAX_VALUE))
										.addGroup(gl_worldTab.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(descriptionLabel))).addGroup(
														gl_worldTab.createParallelGroup(
																Alignment.LEADING, false)
																.addComponent(
																		predictSelectionButton,
																		Alignment.TRAILING,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		viewAutomataButton,
																		Alignment.TRAILING,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)))
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
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.BASELINE).addComponent(
																frameLabel).addComponent(
																		frameComboBox,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.TRAILING).addComponent(
																frameSpinner,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(playSpeedLabel))
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.BASELINE).addComponent(
																playButton).addComponent(
																		stopButton)
														.addComponent(pauseButton))
										.addPreferredGap(ComponentPlacement.RELATED).addGroup(
												gl_worldTab.createParallelGroup(
														Alignment.BASELINE).addComponent(
																nextFrameButton).addComponent(
																		prevFrameButton))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(descriptionLabel).addPreferredGap(
												ComponentPlacement.RELATED, 88,
												Short.MAX_VALUE).addComponent(
														predictSelectionButton)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(viewAutomataButton)))
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

	static class MyEdge {
		private static int freeid = 0;
		private int id = freeid++;
		private String data;

		public MyEdge(String data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MyEdge other = (MyEdge) obj;
			if (id != other.id)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return data;
		}
	}

	private Graph<State, MyEdge> automataToGraph(Automata automata, int inputSignal) {
		Graph<State, MyEdge> res = new DirectedSparseGraph<>();
		State[] data = automata.getData();
		for (State s : data) {
			res.addVertex(s);
		}
		for (State s : data) {
			res.addEdge(new MyEdge(OutputSignal.values()[s.output[inputSignal]].name()), s,
					data[s.nextState[inputSignal]]);
		}

		return res;
	}

	private void showAutomataFrame(Automata automata) {
		Graph<State, MyEdge> g = automataToGraph(automata, 0);
		final State currentState = automata.getData()[automata.getCurStateNumber()];

		JFrame jf = new JFrame();
		jf.setLocation(frame.getX() + frame.getWidth(), frame.getY());
		VisualizationViewer<State, MyEdge> vv = new VisualizationViewer<>(new KKLayout<>(g),
				new Dimension(400, frame.getHeight()));

		Transformer<State, Paint> vertexPaint = s -> s == currentState ? Color.RED
				: Color.GREEN;
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);

		vv.getRenderContext().setVertexLabelTransformer(s -> "" + automata.getStateNumber(s));
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<MyEdge>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		final DefaultModalGraphMouse<Integer, Number> graphMouse = new DefaultModalGraphMouse<Integer, Number>();
		vv.setGraphMouse(graphMouse);
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		// relax();

		vv.addGraphMouseListener(new GraphMouseListener<State>() {
			@Override
			public void graphClicked(State v, MouseEvent me) {
			}

			@Override
			public void graphPressed(State v, MouseEvent me) {
			}

			@Override
			public void graphReleased(State v, MouseEvent me) {
			}
		});

		JComboBox<Integer> inputSignalComboBox = new JComboBox<>();
		for (int i = 0; i < InputSignal.SIGNALS_NUMBER; i++) {
			inputSignalComboBox.addItem(i);
		}
		inputSignalComboBox.addActionListener(e -> {
			int input = (int) inputSignalComboBox.getSelectedItem();
			vv.setGraphLayout(new KKLayout<>(automataToGraph(automata, input)));
			// relax();
		});

		JButton relaxButton = new JButton("relax");
		relaxButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// relax();
			}
		});

		JPanel controls = new JPanel();
		JPanel controlPanel = new JPanel(new GridLayout(2, 1));
		controlPanel.add(controls);
		jf.getContentPane().add(controlPanel, BorderLayout.NORTH);
		controls.add(inputSignalComboBox);
		controls.add(relaxButton);

		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);

		// jf.setSize(418, frame.getHeight());
	}

	private Map<Integer, Generation> data;

	private static class Generation {
		WorldRepeater world;
		Map<Integer, AntEater> antEaters = new HashMap<>();

		public Generation(WorldRepeater world) {
			this.world = world;
		}
	}

	private static class AntEater {
		Map<Integer, EncodedField> tries = new HashMap<>();
	}

	private void loadFile(File file) throws IOException, ClassNotFoundException {
		LogLoader logLoader = new LogLoader(file);

		// reading all of the frames
		data = new HashMap<>();
		statisticsCanvas.clear();
		while (true) {
			try {
				Object o = logLoader.getSmth();

				if (o instanceof EncodedGeneration) {
					EncodedGeneration p = (EncodedGeneration) o;
					int gen = p.first.get("gen");
					Util.log("loading gen: " + gen);
					data.put(gen, new Generation(new WorldRepeater(p.second.first,
							p.second.second)));

					Map<Integer, AntEater> aes = data.get(gen).antEaters;
					for (int aei = 0; aei < 10; aei++) {
						aes.putIfAbsent(aei, new AntEater());
						for (int tri = 0; tri < 10; tri++) {
							aes.get(aei).tries.put(tri, (EncodedField) logLoader.getSmth());
						}
					}
				} else {
					Util.log("loading statistics");

					Statistics antsStatistics = (Statistics) o;
					Util.log("here1");
					Statistics antEatersStatistics = (Statistics) logLoader.getSmth();
					Util.log("here2");

					statisticsCanvas.addStatistics(antsStatistics);
					statisticsCanvas.addStatistics(antEatersStatistics);
					
					Util.log("dobavil?? " + statisticsCanvas.getData().size());

					statisticsCanvas.repaint();
				}
			} catch (EOFException | RuntimeException e) {
				if (!(e.getCause() instanceof EOFException) && e.getCause() != null) {
					e.getCause().printStackTrace();
					System.exit(0);
				}
				break;
			}
		}

		//
		generationComboBox.removeAllItems();
		Util.log("loading: " + data.size());
		data.forEach((i, g) -> {
			Util.log("adding generation " + i);
			generationComboBox.addItem(i);
		});
		generationComboBox.setSelectedIndex(0);

		//
		logLoader.close();
	}

	private void selectGeneration() {
		if (data == null || generationComboBox.getSelectedIndex() == -1)
			return;
		antEaterComboBox.removeAllItems();
		int gen = (int) generationComboBox.getSelectedItem();
		data.get(gen).antEaters.forEach((i, ae) -> antEaterComboBox.addItem(i));
		antEaterComboBox.setSelectedIndex(0);
		// not yet implemented
		antEaterComboBox.setEnabled(false);

		WorldRepeater world = data.get(gen).world;
		fieldCanvas.setWorld(world);
	}

	private void selectAntEater() {
		if (data == null || antEaterComboBox.getSelectedIndex() == -1)
			return;
		int gen = (int) generationComboBox.getSelectedItem();
		int aei = (int) antEaterComboBox.getSelectedItem();
		tryComboBox.removeAllItems();
		data.get(gen).antEaters.get(aei).tries.forEach((i, tri) -> tryComboBox.addItem(i));
		tryComboBox.setSelectedIndex(0);
	}

	private void selectTry() {
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

	private void selectFrame() {
		if (data == null || frameComboBox.getSelectedIndex() == -1)
			return;
		int gen = (int) generationComboBox.getSelectedItem();
		WorldRepeater world = data.get(gen).world;
		int frame = (int) frameComboBox.getSelectedItem();
		world.goToStep(frame);
		fieldCanvas.repaint();
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