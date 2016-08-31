package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public interface WorldGenerator {
	Cell[][] generateWorld(Individual[] ants, Individual... antEaters);
	
	default Cell[][] createEmptyField(int width, int height) {
		Cell[][] field = new Cell[height][];
		for (int i = 0; i < height; i++) {
			field[i] = new Cell[width];
			for (int j = 0; j < width; j++) {
				field[i][j] = new Cell(Type.NOTHING);
			}
		}
		return field;
	}

	default void advanceFoodPercentage() {}
}
