package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

public final class PlayerPanel extends JPanel {
	private static final long serialVersionUID = 6560885420628863775L;

	private JComboBox<Integer> frameComboBox;

	private JButton playButton;
	private JButton pauseButton;

	private JButton prevFrameButton;
	private JButton nextFrameButton;

	private enum PlayState {
		PLAY, PAUSE;
	}

	private PlayState playState;

	private PlayerPanel() {
	}

	/**
	 * @wbp.factory
	 * @wbp.parser.entryPoint
	 */
	public static PlayerPanel createInstance() {
		PlayerPanel panel = new PlayerPanel();
		panel.setBounds(100, 100, 422, 239);

		panel.frameComboBox = new JComboBox<>();

		JLabel frameLabel = new JLabel("Frame");

		panel.frameComboBox = new JComboBox<>();

		Timer timer = new Timer(1000, e -> {
            if (panel.playState == PlayState.PAUSE) {
                return;
            }
            int i = panel.frameComboBox.getSelectedIndex();
            if (i + 1 < panel.frameComboBox.getItemCount()) {
                panel.frameComboBox.setSelectedIndex(i + 1);
            } else {
                panel.pause();
            }
        });
		timer.start();

		JSpinner frameSpinner = new JSpinner();
		Component mySpinnerEditor = frameSpinner.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(2);

		// from 0 to 9, in 1.0 steps start value 5
		SpinnerNumberModel model = new SpinnerNumberModel(5.0, 1.0, 50.0, 1.0);
		frameSpinner.setModel(model);
		frameSpinner.addChangeListener(e -> timer.setDelay((int) (1000 / (double) frameSpinner.getValue())));

		timer.setDelay(1000 / (int) model.getNumber().doubleValue());

		JLabel playSpeedLabel = new JLabel("Play speed, fps");

		panel.prevFrameButton = new JButton("Prev frame");
		panel.prevFrameButton.addActionListener(e -> {
            int i = panel.frameComboBox.getSelectedIndex();
            if (i > 0) {
                panel.frameComboBox.setSelectedIndex(i - 1);
            }
        });

		panel.nextFrameButton = new JButton("Next frame");
		panel.nextFrameButton.addActionListener(e -> {
            int i = panel.frameComboBox.getSelectedIndex();
            if (i + 1 < panel.frameComboBox.getItemCount()) {
                panel.frameComboBox.setSelectedIndex(i + 1);
            }
        });

		panel.playButton = new JButton("Play");
		panel.playButton.addActionListener(e -> panel.play());

		panel.pauseButton = new JButton("Pause");
		panel.pauseButton.addActionListener(e -> panel.pause());

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(e -> {
            panel.stop();
        });

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addGroup(gl_panel.createParallelGroup(
						Alignment.LEADING).addGroup(gl_panel.createSequentialGroup()
								.addComponent(frameLabel, GroupLayout.PREFERRED_SIZE, 142,
										GroupLayout.PREFERRED_SIZE).addGap(5).addComponent(
												panel.frameComboBox,
												GroupLayout.PREFERRED_SIZE, 62,
												GroupLayout.PREFERRED_SIZE)).addGroup(gl_panel
														.createSequentialGroup().addComponent(
																playSpeedLabel,
																GroupLayout.PREFERRED_SIZE, 86,
																GroupLayout.PREFERRED_SIZE)
														.addGap(78).addComponent(frameSpinner,
																GroupLayout.PREFERRED_SIZE, 45,
																GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup().addComponent(
								panel.playButton, GroupLayout.PREFERRED_SIZE, 61,
								GroupLayout.PREFERRED_SIZE).addGap(7).addComponent(
										panel.pauseButton, GroupLayout.PREFERRED_SIZE, 72,
										GroupLayout.PREFERRED_SIZE).addGap(7).addComponent(
												stopButton, GroupLayout.PREFERRED_SIZE, 62,
												GroupLayout.PREFERRED_SIZE)).addGroup(gl_panel
														.createSequentialGroup().addComponent(
																panel.prevFrameButton,
																GroupLayout.PREFERRED_SIZE,
																100,
																GroupLayout.PREFERRED_SIZE)
														.addGap(9).addComponent(
																panel.nextFrameButton,
																GroupLayout.PREFERRED_SIZE,
																100,
																GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(86, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addGroup(gl_panel.createParallelGroup(
						Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(3)
								.addComponent(frameLabel)).addComponent(panel.frameComboBox,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)).addGap(7).addGroup(
												gl_panel.createParallelGroup(Alignment.LEADING)
														.addGroup(gl_panel
																.createSequentialGroup()
																.addGap(6).addComponent(
																		playSpeedLabel))
														.addComponent(frameSpinner,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
						.addGap(7).addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel.playButton).addComponent(panel.pauseButton)
								.addComponent(stopButton)).addGap(7).addGroup(gl_panel
										.createParallelGroup(Alignment.LEADING).addComponent(
												panel.prevFrameButton).addComponent(
														panel.nextFrameButton))
						.addContainerGap(165, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);

		return panel;
	}

	private Consumer<Boolean> onPlay = null;
	
	private void enableComponents(boolean play) {
		pauseButton.setEnabled(play);
		playButton.setEnabled(!play);
		frameComboBox.setEnabled(!play);
		prevFrameButton.setEnabled(!play);
		nextFrameButton.setEnabled(!play);
		
		if (onPlay != null) {
			onPlay.accept(play); 
		}
	}
	
	public void setOnPlay(Consumer<Boolean> onPlay) {
		this.onPlay = onPlay;
	}

	protected void pause() {
		playState = PlayState.PAUSE;
		enableComponents(false);
	}

	protected void play() {
		playState = PlayState.PLAY;
		enableComponents(true);
	}

	protected void stop() {
		pause();
        frameComboBox.setSelectedIndex(0);
	}

	private ActionListener prevOnFrameSelected = null;

	public void setOnFrameSelected(ActionListener onFrameSelected) {
		if (prevOnFrameSelected != null) {
			frameComboBox.removeActionListener(prevOnFrameSelected);
		}
		prevOnFrameSelected = onFrameSelected;
		frameComboBox.addActionListener(onFrameSelected);
	}
	
	public JComboBox<Integer> getFrameComboBox() {
		return frameComboBox;
	}
}
