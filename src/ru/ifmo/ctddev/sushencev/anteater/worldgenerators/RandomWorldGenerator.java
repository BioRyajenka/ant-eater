package ru.ifmo.ctddev.sushencev.anteater.worldgenerators;

import java.util.Arrays;
import java.util.Iterator;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Util;

public class RandomWorldGenerator extends AbstractWorldGenerator {
	private static final long serialVersionUID = -4108923221764776163L;

	protected transient float foodPercentage;

	public RandomWorldGenerator(int width, int height, float foodPercentage) {
		super(width, height);
		this.width = width;
		this.height = height;
		this.foodPercentage = foodPercentage;
	}

	@Override
	protected void generateFood(Cell[][] field) {
		int foodAmount = (int) (foodPercentage * width * height);

		doNTimes(foodAmount, (x, y) -> field[y][x].getType() == Type.FOOD, Util::nextInt, (x,
				y) -> field[y][x].setType(Type.FOOD));
	}

	@Override
	protected void placeAnts(Cell[][] field, Individual[] ants) {
		if (width * height <= ants.length) {
			throw new RuntimeException("too many ants for such small field");
		}
		Iterator<Individual> it = Arrays.asList(ants).iterator();
		doNTimes(ants.length, (x, y) -> field[y][x].isOccupied(), Util::nextInt, (x, y) -> {
			Individual ant = it.next();
			placeIndividualOnPosition(field, ant, x, y);
		});
	}

	@Override
	protected void placeAntEaters(Cell[][] field, Individual[] antEaters) {
		placeAnts(field, antEaters);
	}

	@Override
	protected void placeOneAntEater(Cell[][] field, Individual antEater) {
		placeIndividualOnPosition(field, antEater, width / 2, height / 2);
	}
	
	@Override
	public IntPair correctCoordinates(int x, int y) {
		if (y >= height) y -= height;
		if (y < 0) y += height;
		if (x >= width) x -= width;
		if (x < 0) x += width;
		return new IntPair(x, y);
	}
}
