package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public class RandomWorldGenerator implements WorldGenerator {
	private int width;
	private int height;
	private double foodPercentage;

	public RandomWorldGenerator(int width, int height, double foodPercentage) {
		this.width = width;
		this.height = height;
		this.foodPercentage = foodPercentage;
	}

	public Cell[][] generateWorld(Individual[] ants, Individual ... antEaters) {
		Cell[][] field = createEmptyField(width, height);
		int foodAmount = (int) (width * height * foodPercentage);

		generateFood(field, foodAmount);

		placeAnts(field, ants);
		if (antEaters.length == 1) {
			placeOneAntEater(field, antEaters[0]);
		} else {
			placeAnts(field, antEaters);
		}

		return field;
	}

	public void advanceFoodPercentage() {
		//currentIteration = (currentIteration + 1) % foodPeriod;
		//foodPercentage = minFood + (maxFood - minFood) * Math.cos((double) (currentIteration) / foodPeriod * Math.PI * 2);
	}

	private void generateFood(Cell[][] field, int foodAmount) {
		doNTimes(foodAmount, (x, y) -> field[y][x].getType() == Type.FOOD, (x,
				y) -> field[y][x].setType(Type.FOOD));
	}

	private void placeAnts(Cell[][] field, Individual[] ants) {
		final int height = field.length;
		final int width = field[0].length;

		if (width * height <= ants.length) {
			throw new RuntimeException("too many ants for such small field");
		}
		Iterator<Individual> it = Arrays.asList(ants).iterator();
		doNTimes(ants.length, (x, y) -> field[y][x].isOccupied() || field[y][x]
				.getType() == Type.FOOD || (x == width / 2 && y == height / 2), (x, y) -> {
					Individual ant = it.next();
					field[y][x].setIndividual(ant);
					ant.setPosition(x, y, Util.nextInt(4));
				});
	}

	private void placeOneAntEater(Cell[][] field, Individual antEater) {
		final int height = field.length;
		final int width = field[0].length;
		field[height / 2][width / 2].setIndividual(antEater);
		antEater.setPosition(width / 2, height / 2, Util.nextInt(4));
	}

	private void doNTimes(int n, BiFunction<Integer, Integer, Boolean> failFunction,
			BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < n; i++) {
			int x = Util.nextInt(width);
			int y = Util.nextInt(height);
			if (failFunction.apply(x, y)) {
				i--;
				continue;
			}
			action.accept(x, y);
		}
	}
}
