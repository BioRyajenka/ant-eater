package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import ru.ifmo.ctddev.sushencev.anteater.Automata;
import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.World;

public class FieldCanvas extends Canvas {
	private static final long serialVersionUID = 8109184974840919569L;

	private Cell[][] field;
	private JLabel descriptionLabel;

	public FieldCanvas(JLabel descriptionLabel) {
		this.descriptionLabel = descriptionLabel;

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (field == null) {
					return;
				}
				//Util.log("click (" + e.getX() + ":" + e.getY() + ")");
				int i = e.getY() / sizeY;
				int j = e.getX() / sizeX;
				selectedIndividual = field[i][j].getIndividual();

				if (selectedIndividual == null) {
					descriptionLabel.setText("Individual description");
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

	private Individual antEater;

	public void setWorld(World world) {
		field = world.getField();
		antEater = world.getCurrentAntEater();

		n = field.length;
		m = field[0].length;

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

	private void drawIndividual(Color color, int i, int j, int orientation, Graphics g,
			boolean mark) {
		Graphics2D g2 = (Graphics2D) g;

		if (mark) {
			g2.setStroke(new BasicStroke(2));
			g.setColor(Color.RED);
			g.drawRect(j * sizeX + 2, i * sizeY + 2, sizeX - 3, sizeY - 3);
		}

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

	private void drawAnt(int i, int j, int orientation, Graphics g, boolean mark) {
		drawIndividual(Color.BLACK, i, j, orientation, g, mark);
	}

	private void drawAntEater(int i, int j, int orientation, Graphics g, boolean mark) {
		drawIndividual(Color.RED, i, j, orientation, g, mark);
	}

	@Override
	public void paint(Graphics g) {
		if (field == null) {
			return;
		}

		drawGrid(g);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Cell c = field[i][j];
				if (c.hasIndividual()) {
					Individual ind = c.getIndividual();
					if (ind == antEater) {
						drawAntEater(i, j, ind.getPosition().rot, g,
								ind == selectedIndividual);
					} else {
						drawAnt(i, j, ind.getPosition().rot, g,
								ind == selectedIndividual);
					}
					if (ind == selectedIndividual) {
						StringBuilder sb = new StringBuilder("<html>");
						sb.append("ate: ");
						sb.append(ind.getEatenFoodAmount());
						sb.append("<br>");
						Automata chr = ind.getChromosome();
						if (chr != null) {
							sb.append("currect state number: ");
							sb.append(chr.getCurStateNumber());
							sb.append("<br>");
							sb.append("states: ");
							sb.append(chr.getStatesNumber());
							sb.append("<br>");
						}
						if (ind.isDead()) {
							sb.append("<font color=\"red\">dead</font>");
							sb.append("<br>");
						}
						sb.append("</html>");
						descriptionLabel.setText(sb.toString());
					}
				} else if (c.getType() == Type.FOOD) {
					drawFood(i, j, g);
				}
			}
		}
	}
}