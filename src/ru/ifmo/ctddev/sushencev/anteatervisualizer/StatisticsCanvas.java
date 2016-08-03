package ru.ifmo.ctddev.sushencev.anteatervisualizer;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	private final static int IND = 30;

	private int maxX = -1;
	private int maxY = -1;

	public void addStatistics(Statistics statistics) {
		data.add(statistics);
		colors.put(statistics, new Color(Util.dice(), Util.dice(), Util.dice()));
		maxX = Math.max(maxX, statistics.getAbscisses().stream().max(Integer::compareTo)
				.get());
		maxY = Math.max(maxY, statistics.getAbscisses().stream().map(x -> statistics.get(x))
				.max(Integer::compareTo).get());
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

	private void drawGraph(Statistics s, Graphics g) {
		g.setColor(colors.get(s));
		int prevX = -1;
		for (int x : s.getAbscisses()) {
			if (prevX == -1) {
				prevX = x;
				continue;
			}
			g.drawLine(scaleX(prevX), scaleY(s.get(prevX)), scaleX(x), scaleY(s.get(x)));
			prevX = x;
		}
	}

	private int scaleX(int x) {
		return (int) ((getWidth() - 2 * IND) * (1f * x / maxX) + IND);
	}

	private int scaleY(int y) {
		return getHeight() - (int) ((getHeight() - 2 * IND) * (1f * y / maxY) + IND);
	}

	private void drawLegend(Graphics g) {
		int x = getWidth() - IND - 100;
		int y = IND;
		for (Statistics s : data) {
			g.setColor(colors.get(s));
			g.drawLine(x, y, x + IND, y);
			g.setColor(Color.BLACK);
			g.drawString(s.getDescription(), x + IND + 3, y + g.getFontMetrics().getHeight()
					/ 4);
			y += g.getFontMetrics().getHeight();
		}
	}

	private static int calcNumberOfSymbols(int n) {
		int res = 0;
		while (n > 0) {
			n /= 10;
			res++;
		}
		if (res == 0) res = 1;
		return res;
	}

	private void drawCoordinateNames(Graphics g) {
		int maxSymbols = data.stream().map(s -> s.getAbscisses().stream().map(
				x -> calcNumberOfSymbols(s.get(x))).max(Integer::compareTo).get()).max(
						Integer::compareTo).get();
		int x = IND - g.getFontMetrics().charWidth('0') * maxSymbols - 3;
		data.forEach(s -> s.getAbscisses().forEach(x1 -> g.drawString(s.get(x1) + "", x, scaleY(s.get(x1)))));
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
}
