package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ifmo.ctddev.sushencev.anteater.Statistics;
import ru.ifmo.ctddev.sushencev.anteater.Util;

public class StatisticsCanvas extends Canvas {
	private static final long serialVersionUID = -630831644816061456L;

	private List<Statistics> data = new ArrayList<>();
	private Map<Statistics, Color> colors = new HashMap<>();

	// indentation
	private final static int IND = 45;

	private int maxX = -1;
	private int maxY = -1;

	private int xToDetail = 0;
	private int yToDetail = 0;

	public StatisticsCanvas() {
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				xToDetail = 0;
				yToDetail = 0;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				xToDetail = e.getX();
				yToDetail = e.getY();
				repaint();
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

	public List<Statistics> getData() {
		return data;
	}

	public void addStatistics(Statistics statistics) {
		data.add(statistics);
		colors.put(statistics, new Color(Util.dice(), Util.dice(), Util.dice()));
		maxX = Math.max(maxX, statistics.getAbscisses().stream().max(Integer::compareTo)
				.get());
		int curMaxY = statistics.getAbscisses().stream().map(x -> statistics.get(x)).max(
				Integer::compareTo).get();
		maxY = Math.max(maxY, curMaxY);
		if (weaker == null || curMaxY < maxY) {
			weaker = statistics;
			weakerScale = .5f * maxY / curMaxY;
		}
		Util.log("maxY: " + maxY);
	}

	public void clear() {
		data.clear();
		maxX = maxY = -1;
	}

	private void drawGrid(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		g.drawLine(IND, IND, IND, getHeight() - IND);
		g.drawLine(IND, getHeight() - IND, getWidth() - IND, getHeight() - IND);
	}

	private Statistics weaker;
	private float weakerScale;

	private void drawGraph(Statistics s, Graphics g) {
		g.setColor(colors.get(s));
		int prevX = -1;
		float scale = s == weaker ? weakerScale : 1;
		for (int x : s.getAbscisses()) {
			if (prevX == -1) {
				prevX = x;
				continue;
			}
			int y1 = (int) (s.get(prevX) * scale);
			int y2 = (int) (s.get(x) * scale);
			g.drawLine(scaleX(prevX), scaleY(y1), scaleX(x), scaleY(y2));
			prevX = x;
		}
	}

	private int scaleX(int x) {
		return (int) ((getWidth() - 2 * IND) * (1f * x / maxX) + IND);
	}

	private int scaleY(int y) {
		return getHeight() - (int) ((getHeight() - 2 * IND) * (1f * y / maxY) + IND);
	}

	private int unscaleX(int x) {
		return (int) ((1f * (x - IND) / (getWidth() - 2 * IND)) * maxX);
	}
	
	private int unscaleY(int y) {
		return (int) (1f * (getHeight() - y - IND) / (getHeight() - 2 * IND) * maxY);
	}

	private void drawLegend(Graphics g) {
		int x = getWidth() - IND - 100 - 50;
		int y = g.getFontMetrics().getHeight() / 4 + 3;
		for (Statistics s : data) {
			g.setColor(colors.get(s));
			g.drawLine(x, y, x + IND, y);
			g.setColor(Color.BLACK);
			String scaleDesc = ", scale " + (s == weaker ? weakerScale : 1);
			g.drawString(s.getDescription() + scaleDesc, x + IND + 3, y + g.getFontMetrics()
					.getHeight() / 4);
			y += g.getFontMetrics().getHeight();
		}
	}

	private static int calcNumberOfSymbols(int n) {
		int res = 0;
		while (n > 0) {
			n /= 10;
			res++;
		}
		if (res == 0)
			res = 1;
		return res;
	}

	private int toGoodNumber(int n) {
		n = (n / 10) * 10;
		if (n / 10 % 10 == 9)
			n += 10;
		if (n / 10 % 10 == 1)
			n -= 10;
		return n;
	}

	private void drawCoordinateNames(Graphics g) {
		if (data.isEmpty())
			return;
		int maxSymbols = data.stream().map(s -> s.getAbscisses().stream().map(
				x -> calcNumberOfSymbols(s.get(x))).max(Integer::compareTo).get()).max(
						Integer::compareTo).get();
		int x = IND - g.getFontMetrics().charWidth('0') * maxSymbols - 3;

		final int points = 10;
		int delta = toGoodNumber(maxY / points);

		for (int y = delta; y < maxY; y += delta) {
			String str = y + "";
			// Util.log("drawing string " + str);
			g.drawString(str, x, scaleY(y));
		}

		if (xToDetail != 0) {
			String str = "" + unscaleX(xToDetail);
			g.drawString(str, xToDetail - g.getFontMetrics().stringWidth(str), getHeight()
					- IND + g.getFontMetrics().getHeight());
			str = "" + unscaleY(yToDetail);
			g.drawString(str, x, yToDetail);
		}
		// data.forEach(s -> s.getAbscisses().forEach(x1 ->
		// g.drawString(s.get(x1) + "", x, scaleY(s.get(x1)))));
	}

	@Override
	public void paint(Graphics g) {
		drawGrid(g);
		drawLegend(g);
		drawCoordinateNames(g);
		for (Statistics s : data) {
			drawGraph(s, g);
		}
	}

	public void recolorStatistics() {
		data.forEach(s -> colors.put(s, new Color(Util.dice(), Util.dice(), Util.dice())));
		repaint();
	}
}
