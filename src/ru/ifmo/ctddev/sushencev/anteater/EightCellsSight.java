package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Util.SerializablePredicate;

public class EightCellsSight implements Sight {
	private static final long serialVersionUID = 1561910412376252590L;

	private SerializablePredicate<Cell> isFoodFunction;

	public EightCellsSight(SerializablePredicate<Cell> isFoodFunction) {
		this.isFoodFunction = isFoodFunction;
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
			for (int dy = rot == 0 ? -2 : 2; rot == 0 ? dy <= 0 : dy >= 0; dy += rot == 0
					? 1 : -1) {
				for (int dx = rot == 0 ? -2 : 2; rot == 0 ? dx <= 2
						: dx >= -2; dx += rot == 0 ? 1 : -1) {
					if (Util.mdist(0, 0, dx, dy) <= 2 && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, field, i++, mask);
					}
				}
			}
		} else {
			// right or left
			for (int dx = rot == 1 ? 2 : -2; rot == 1 ? dx >= 0 : dx <= 0; dx += rot == 1
					? -1 : 1) {
				for (int dy = rot == 1 ? -2 : 2; rot == 1 ? dy <= 2
						: dy >= -2; dy += rot == 1 ? 1 : -1) {
					if (Util.mdist(0, 0, dx, dy) <= 2 && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, field, i++, mask);
					}
				}
			}
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
