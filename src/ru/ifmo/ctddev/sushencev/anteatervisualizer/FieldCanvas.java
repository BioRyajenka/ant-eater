package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;

import ru.ifmo.ctddev.sushencev.anteater.Automata;
import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Util;
import ru.ifmo.ctddev.sushencev.anteater.World;

public class FieldCanvas extends Canvas {
	private static final long serialVersionUID = 8109184974840919569L;

	private JLabel descriptionLabel;
	private JButton viewAutomataButton;

	public FieldCanvas(JLabel descriptionLabel, JButton viewAutomataButton) {
		this.descriptionLabel = descriptionLabel;
		this.viewAutomataButton = viewAutomataButton;

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (world == null) {
					return;
				}
				int i = e.getY() / sizeY;
				int j = e.getX() / sizeX;
				if (i < world.getField().length && j < world.getField()[0].length) {
					selectedIndividual = world.getField()[i][j].getIndividual();
				} else {
					selectedIndividual = null;
				}

				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	private Individual selectedIndividual;

	private int n, m;
	private int sizeX, sizeY;

	private World world;

	public void setWorld(World world) {
		Util.log("setting world " + world.hashCode());
		this.world = world;

		n = world.getField().length;
		m = world.getField()[0].length;

		sizeX = getWidth() / m;
		sizeY = getHeight() / n;

		repaint();
	}

	private void drawGrid(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		for (int i = 0; i <= n; i++) {
			g.drawLine(0, i * sizeY, m * sizeX, i * sizeY);
		}
		for (int i = 0; i <= m; i++) {
			g.drawLine(i * sizeX, 0, i * sizeX, n * sizeY);
		}
	}

	private void drawFood(int i, int j, Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(j * sizeX + 1, i * sizeY + 1, sizeX - 1, sizeY - 1);
	}

	private void drawIndividual(Color color, int i, int j, int orientation, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.setColor(color);

		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		int y2 = 0;
		int x3 = 0;
		int y3 = 0;

		switch (orientation) {
		case 0:
			x1 = 3;
			y1 = sizeY - 3;
			x2 = sizeX / 2;
			y2 = 3;
			x3 = sizeX - 3;
			y3 = sizeY - 3;
			break;
		case 1:
			x1 = 3;
			y1 = 3;
			x2 = sizeX - 3;
			y2 = sizeY / 2;
			x3 = 3;
			y3 = sizeY - 3;
			break;
		case 2:
			x1 = 3;
			y1 = 3;
			x2 = sizeX / 2;
			y2 = sizeY - 3;
			x3 = sizeX - 3;
			y3 = 3;
			break;
		case 3:
			x1 = sizeX - 3;
			y1 = 3;
			x2 = 3;
			y2 = sizeY / 2;
			x3 = sizeX - 3;
			y3 = sizeY - 3;
			break;

		default:
			break;
		}

		x1 += j * sizeX;
		y1 += i * sizeY;
		x2 += j * sizeX;
		y2 += i * sizeY;
		x3 += j * sizeX;
		y3 += i * sizeY;

		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x3, y3, x2, y2);
	}

	private void drawDead(int i, int j, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.BLUE);
		g2.setStroke(new BasicStroke(2));
		
		int x1 = sizeX / 6;
		int y1 = sizeY / 6;
		int x2 = sizeX - sizeX / 6;
		int y2 = sizeY - sizeY / 6;
		x1 += j * sizeX;
		y1 += i * sizeY;
		x2 += j * sizeX;
		y2 += i * sizeY;
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y2, x2, y1);
	}

	private void drawAnt(int i, int j, int orientation, Graphics g) {
		drawIndividual(Color.BLACK, i, j, orientation, g);
	}

	private void drawAntEater(int i, int j, int orientation, Graphics g) {
		drawIndividual(Color.RED, i, j, orientation, g);
	}

	private void drawBorder(int i, int j, Graphics g, Color borderColor) {
		((Graphics2D) g).setStroke(new BasicStroke(2));
		g.setColor(borderColor);
		g.drawRect(j * sizeX + 2, i * sizeY + 2, sizeX - 3, sizeY - 3);
	}

	@Override
	public void paint(Graphics g) {
		if (world == null) {
			return;
		}

		drawGrid(g);

		float maxFitness = Arrays.stream(world.getAnts()).max((a, b) -> Float.compare(a
				.getFitness(), b.getFitness())).get().getFitness();

		int foodLeft = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Cell c = world.getField()[i][j];
				foodLeft += c.getType() == Type.FOOD ? 1 : 0;
			}
		}

		// description
		StringBuilder sb = new StringBuilder("<html>");
		sb.append("left food on field ");
		sb.append(foodLeft);
		sb.append("<br>");

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Cell c = world.getField()[i][j];
				if (c.hasIndividual()) {
					Individual ind = c.getIndividual();

					Color borderColor = Color.WHITE;
					if (ind != world.getCurrentAntEater() && ind.getFitness() == maxFitness) {
						borderColor = Color.YELLOW;
					}
					if (ind == selectedIndividual) {
						borderColor = Color.RED;
					}

					if (borderColor != Color.WHITE) {
						drawBorder(i, j, g, borderColor);
					}

					if (ind == world.getCurrentAntEater()) {
						drawAntEater(i, j, ind.getPosition().rot, g);
					} else {
						if (ind.isDead()) {
							drawDead(i, j, g);
						} else {
							drawAnt(i, j, ind.getPosition().rot, g);
						}
					}

					if (ind == selectedIndividual) {
						sb.append(ind.toString());
						sb.append("<br>ate: ");
						sb.append(ind.getFitness());
						sb.append("<br>");
						Automata chr = ind.getChromosome();
						if (chr != null) {
							sb.append("sight: ");
							sb.append(ind.checkSight().getMask());
							sb.append("<br>currect state number: ");
							sb.append(chr.getCurStateNumber());
							sb.append("<br>states: ");
							sb.append(chr.getStatesNumber());
							sb.append("<br>");
						}
						if (ind.isDead()) {
							sb.append("<font color=\"red\">dead</font><br>");
						}
					}
				} else if (c.getType() == Type.FOOD) {
					drawFood(i, j, g);
				}
			}
		}
		sb.append("</html>");
		descriptionLabel.setText(sb.toString());
		viewAutomataButton.setEnabled(selectedIndividual != null);
	}

	public Automata getSelectedAutomata() {
		return selectedIndividual.getChromosome();
	}
}