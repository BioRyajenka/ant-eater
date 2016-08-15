package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Arrays;
import java.util.Iterator;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public class ArenaWorldGenerator implements WorldGenerator {
	private int width;
	private int height;
	private float foodPercentage;
	
	public ArenaWorldGenerator(int width, int height, float foodPercentage) {
		this.foodPercentage = foodPercentage;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Cell[][] generateWorld(Individual[] ants, Individual antEater) {
		Cell[][] field = new Cell[height][];
		for (int i = 0; i < height; i++) {
			field[i] = new Cell[width];
			for (int j = 0; j < width; j++) {
				field[i][j] = new Cell(Type.NOTHING);
			}
		}
		
		int rank = field.length / 5;
		if (field.length != 5 * rank) {
			throw new RuntimeException("field should be " + (5 * rank) + "x" + (5 * rank));
		}
		if (rank % 2 == 0) {
			throw new RuntimeException("rank should be even");
		}
		if (ants.length != 12) {
			throw new RuntimeException("ants number should be 12");
		}

		Iterator<Individual> ait = Arrays.asList(ants).iterator();
		Cell[][] stamp = createStamp();
		int vectX = rank;
		int vectY = 0;
		int stampX = 0;
		int stampY = 0;
		for (int i = 0; i < 4; i++) {
			stampX += vectX;
			stampY += vectY;
			for (int j = 0; j < 3; j++) {
				placeStamp(field, stampX, stampY, stamp);
				
				// placing ant
				Individual ant = ait.next();
				field[stampY + rank / 2][stampX + rank / 2].setIndividual(ant);
				ant.setPosition(stampX + rank / 2, stampY + rank / 2, Util.nextInt(4));
				
				stampX += vectX;
				stampY += vectY;
			}
			int temp = vectX;
			vectX = -vectY;
			vectY = temp;
			stamp = rotate90Degree(stamp);
		}

		// placing ant eater
		final int height = field.length;
		final int width = field[0].length;
		field[height / 2][width / 2].setIndividual(antEater);
		antEater.setPosition(width / 2, height / 2, Util.nextInt(4));
		return field;
	}

	private Cell[][] rotate90Degree(Cell[][] stamp) {
		Cell[][] res = new Cell[stamp.length][];
		for (int i = 0; i < stamp.length; i++) {
			res[i] = new Cell[stamp.length];
		}
		for (int i = 0; i < stamp.length; i++) {
			for (int j = 0; j < stamp.length; j++) {
				//2 1 -> 1 0
				res[i][stamp.length - j - 1] = stamp[j][i].clone();
			}
		}
		return res;
	}

	private void placeStamp(Cell[][] field, int stampX, int stampY, Cell[][] stamp) {
		for (int i = 0; i < stamp.length; i++) {
			for (int j = 0; j < stamp.length; j++) {
				field[stampY + j][stampX + i] = stamp[j][i].clone();
			}
		}
	}

	private Cell[][] createStamp() {
		int rank = width / 5;
		Cell[][] res = new Cell[rank][];
		for (int i = 0; i < rank; i++) {
			res[i] = new Cell[rank];
			for (int j = 0; j < rank; j++) {
				res[i][j] = new Cell(Util.dice(foodPercentage) ? Type.FOOD : Type.NOTHING);
			}
		}
		return res;
	}
}
