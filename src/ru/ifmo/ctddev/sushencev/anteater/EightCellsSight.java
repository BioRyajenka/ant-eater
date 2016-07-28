package ru.ifmo.ctddev.sushencev.anteater;

import java.util.function.Predicate;

import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;

public class EightCellsSight implements Sight {
	private Predicate<Cell> isFoodFunction;
	
	public EightCellsSight(Predicate<Cell> isFoodFunction) {
		this.isFoodFunction = isFoodFunction;
	}

	@Override
	public InputSignal check(World world, Position position) {
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
			for (int dx = rot == 0 ? -2 : 2; rot == 0 ? dx <= 0 : dx >= 0; dx += rot == 0
					? 1 : -1) {
				for (int dy = rot == 0 ? -2 : 2; rot == 0 ? dy <= 2
						: dy >= -2; dy += rot == 0 ? 1 : -1) {
					if (Util.mdist(0, 0, dx, dy) <= 2 && (dx != 0 || dy != 0)) {
						mask = processCell(x, y, dx, dy, world, i++, mask);
					}
				}
			}
		} else {
			// right or left
			if (rot == 1 || rot == 3) {
				for (int dy = rot == 1 ? 2 : -2; rot == 1 ? dy >= 0
						: dy <= 0; dy += rot == 1 ? -1 : 1) {
					for (int dx = rot == 1 ? -2 : 2; rot == 1 ? dx <= 2
							: dx >= -2; dx += rot == 1 ? 1 : -1) {
						if (Util.mdist(0, 0, dx, dy) <= 2 && (dx != 0 || dy != 0)) {
							mask = processCell(x, y, dx, dy, world, i++, mask);
						}
					}
				}
			}
		}
		
		return new InputSignal(mask);
	}

	int processCell(int x, int y, int dx, int dy, World world, int i, int mask) {
		int nx = x + dx;
		int ny = y + dy;
		// torus
		if (nx >= world.getHeight()) nx -= world.getHeight();
		if (nx < 0) nx += world.getHeight();
		if (ny >= world.getWidth()) ny -= world.getWidth();
		if (ny < 0) ny += world.getWidth();
		
		return isFoodFunction.test(world.getField()[nx][ny]) ? (mask | (1 << i)) : mask; 
	}
}
