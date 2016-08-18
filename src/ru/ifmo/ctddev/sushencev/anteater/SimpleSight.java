package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Util.SerializablePredicate;

public class SimpleSight implements Sight {
	private static final long serialVersionUID = 1561910412376252590L;

	private SerializablePredicate<Cell> isFoodFunction;
	private int r;

	public SimpleSight(SerializablePredicate<Cell> isFoodFunction, int range) {
		this.isFoodFunction = isFoodFunction;
		this.r = range;
	}

	@Override
	public InputSignal check(Cell[][] field, Position position) {
		int rot = position.rot;
		int x = position.x;
		int y = position.y;

		// .up....down..right.left
		// ..............#......#.
		// ..#....##@##..##....##.
		// .###....###...@##..##@.
		// ##@##....#....##....##.
		// ..............#......#.

		int i = 0;
		int mask = 0;
		if (rot == 0 || rot == 2) {
			// up or down
			for (int dy = rot == 0 ? -r : r; rot == 0 ? dy <= 0 : dy >= 0; dy += rot == 0 ? 1
					: -1) {
				for (int dx = rot == 0 ? -r : r; rot == 0 ? dx <= r : dx >= -r; dx += rot == 0
						? 1 : -1) {
					if (Util.mdist(0, 0, dx, dy) <= r && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, field, i++, mask);
					}
				}
			}
		} else {
			// right or left
			for (int dx = rot == 1 ? r : -r; rot == 1 ? dx >= 0 : dx <= 0; dx += rot == 1 ? -1
					: 1) {
				for (int dy = rot == 1 ? -r : r; rot == 1 ? dy <= r : dy >= -r; dy += rot == 1
						? 1 : -1) {
					if (Util.mdist(0, 0, dx, dy) <= r && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, field, i++, mask);
					}
				}
			}
		}
		
		Position fp = World.getForwardPosition(position, field[0].length, field.length);
		if (field[fp.y][fp.x].hasIndividual()) {
			mask += 8;
		}

		return new InputSignal(mask);
	}

	int processCell(int x, int y, int dx, int dy, Cell[][] field, int i, int mask) {
		int nx = x + dx;
		int ny = y + dy;

		int height = field.length;
		int width = field[0].length;

		// torus
		if (nx >= width) nx -= width;
		if (nx < 0) nx += width;
		if (ny >= height) ny -= height;
		if (ny < 0) ny += height;

		return isFoodFunction.test(field[ny][nx]) ? (mask | (1 << i)) : mask;
	}
}
