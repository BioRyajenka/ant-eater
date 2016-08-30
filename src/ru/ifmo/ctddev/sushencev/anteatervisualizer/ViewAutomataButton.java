package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Automata.State;

public final class ViewAutomataButton extends JButton {
	private static final long serialVersionUID = -2548629450202001689L;
	
	private JFrame parentFrame; 

	public ViewAutomataButton(JFrame parentFrame) {
		super("View automata");
		this.parentFrame = parentFrame;
	}
	
	public void setFieldCanvas(FieldCanvas fieldCanvas) {
		addActionListener(e -> showAutomataFrame(fieldCanvas.getSelectedAutomata()));		
	}
	
	private static class MyEdge {
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
		jf.setLocation(parentFrame.getX() + parentFrame.getWidth(), parentFrame.getY());
		VisualizationViewer<State, MyEdge> vv = new VisualizationViewer<>(new KKLayout<>(g),
				new Dimension(400, parentFrame.getHeight()));

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
		int inputSignalsNumber = automata.data[0].nextState.length;
		for (int i = 0; i < inputSignalsNumber; i++) {
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
}