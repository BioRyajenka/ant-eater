package ru.ifmo.ctddev.sushencev.anteater.worldgenerators;

import java.util.Arrays;
import java.util.Iterator;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Util;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;

public class WallWorldGenerator extends AbstractWorldGenerator {
	private static final long serialVersionUID = -1659021905758111816L;

	public WallWorldGenerator(int width, int height) {
		super(width, height);
	}

	@Override
	protected void generateFood(Cell[][] field) {
		final int foodAmount = width * height / 3;

		doNTimes(foodAmount, (x, y) -> field[y][x].isOccupied(), size -> {
			int res = (int) (Math.abs(Util.nextNormalDistribution(0, .4f)) * size * 2 / 3);
			res = Math.max(Math.min(res, size - 1), 0);
			return size - res - 1;
		}, (x, y) -> {
			field[y][x].setType(Type.FOOD);
		});
	}

	@Override
	protected void placeAnts(Cell[][] field, Individual[] ants) {
		Iterator<Individual> it = Arrays.asList(ants).iterator();

		doNTimes(ants.length, (x, y) -> field[y][x].isOccupied(), size -> {
			int res = (int) (Math.abs(Util.nextNormalDistribution(0, .4f)) * size / 4);
			return Math.max(Math.min(res, size - 1), 0);
		}, (x, y) -> {
			Individual ant = it.next();
			placeIndividualOnPosition(field, ant, x, y);
		});
	}

	@Override
	protected void placeAntEaters(Cell[][] field, Individual[] antEaters) {
		// TODO
	}

	@Override
	protected void placeOneAntEater(Cell[][] field, Individual antEater) {
		placeIndividualOnPosition(field, antEater, width / 2, height / 2);
	}

	@Override
	public IntPair correctCoordinates(int x, int y) {
		if (Util.inBounds(x, 0, width) && Util.inBounds(y, 0, height)) {
			return new IntPair(x, y);
		}
		return null;
	}
}