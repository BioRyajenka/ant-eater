package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;
import ru.ifmo.ctddev.sushencev.anteater.Util.SerializablePredicate;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class SimpleSight implements Sight {
	private static final long serialVersionUID = 1561910412376252590L;

	private SerializablePredicate<Cell> isFoodFunction;
	private int r;

	public SimpleSight(SerializablePredicate<Cell> isFoodFunction, int range) {
		this.isFoodFunction = isFoodFunction;
		this.r = range;
	}

	@Override
	public InputSignal check(Cell[][] field, Position position, WorldGenerator wg) {
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
					if (Util.manhattanDist(0, 0, dx, dy) <= r && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, field, i++, mask, wg);
					}
				}
			}
		} else {
			// right or left
			for (int dx = rot == 1 ? r : -r; rot == 1 ? dx >= 0 : dx <= 0; dx += rot == 1 ? -1
					: 1) {
				for (int dy = rot == 1 ? -r : r; rot == 1 ? dy <= r : dy >= -r; dy += rot == 1
						? 1 : -1) {
					if (Util.manhattanDist(0, 0, dx, dy) <= r && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, field, i++, mask, wg);
					}
				}
			}
		}

		return new InputSignal(mask);
	}

	@Override
	public int getInputSignalsNumber() {
		return 1 << (r * (r + 2));
	}

	private int processCell(int x, int y, int dx, int dy, Cell[][] field, int i, int mask, WorldGenerator wg) {
		int nx = x + dx;
		int ny = y + dy;

		IntPair corrected = wg.correctCoordinates(nx, ny);
		if (corrected == null) {
			// doesn't see obstacle
			return mask;
		}
		nx = corrected.first;
		ny = corrected.second;

		return isFoodFunction.test(field[ny][nx]) ? (mask | (1 << i)) : mask;
	}
}
