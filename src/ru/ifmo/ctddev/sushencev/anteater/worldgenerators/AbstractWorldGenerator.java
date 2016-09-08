package ru.ifmo.ctddev.sushencev.anteater.worldgenerators;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Util;

public abstract class AbstractWorldGenerator implements WorldGenerator {
	private static final long serialVersionUID = 8159567879265444400L;

	protected int width;
	protected int height;
	

	public AbstractWorldGenerator(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Cell[][] generateWorld(Individual[] ants, Individual[] antEaters) {
		Cell[][] field = createEmptyField(width, height);
		generateFood(field);

		if (antEaters.length == 1) {
			placeOneAntEater(field, antEaters[0]);
		} else {
			placeAntEaters(field, antEaters);
		}

		placeAnts(field, ants);

		return field;
	}

	protected abstract void generateFood(Cell[][] field);

	protected abstract void placeAnts(Cell[][] field, Individual[] ants);

	protected abstract void placeAntEaters(Cell[][] field, Individual[] antEaters);

	protected abstract void placeOneAntEater(Cell[][] field, Individual antEater);
	
	protected void placeIndividualOnPosition(Cell[][] field, Individual individual, int x,
			int y) {
		field[y][x].setType(Type.NOTHING);
		field[y][x].setIndividual(individual);
		individual.setPosition(x, y, Util.nextInt(4));
	}

	protected static Cell[][] createEmptyField(int width, int height) {
		Cell[][] field = new Cell[height][];
		for (int i = 0; i < height; i++) {
			field[i] = new Cell[width];
			for (int j = 0; j < width; j++) {
				field[i][j] = new Cell(Type.NOTHING);
			}
		}
		return field;
	}

	protected void doNTimes(int n, BiFunction<Integer, Integer, Boolean> failFunction,
			Function<Integer, Integer> distribution, BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < n; i++) {
			int x = distribution.apply(width);
			int y = distribution.apply(height);
			if (failFunction.apply(x, y)) {
				i--;
				continue;
			}
			action.accept(x, y);
		}
	}

	protected void doForEach(BiConsumer<Integer, Integer> consumer) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				consumer.accept(j, i);
			}
		}
	}
}