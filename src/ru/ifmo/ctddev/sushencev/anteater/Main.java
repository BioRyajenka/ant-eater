package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public class Main {
	public static void main(String[] args) throws IOException {
		int width = 25;
		int height = 25;
		float foodPercentage = .5f;
		int antPopulationSize = 20;
		int antEaterPopulationSize = 10;
		float crossingoverProbability = .3f;
		float mutationProbability = .2f;
		int maxStatesInMachine = 5;

		int steps = 100;
		int tries = 10;

		Sight antSight = new SimpleSightWithObstacles(c -> c.getType() == Type.FOOD, 1);
		Sight antEaterSight = new SimpleSight(Cell::hasIndividual, 1);

		SelectionStrategy selectionStrategy = new TwoRandomSelectionStrategy(
				crossingoverProbability, mutationProbability);
		//SelectionStrategy selectionStrategy = new ProportionalSelectionStrategy(
		//		crossingoverProbability, mutationProbability);
		selectionStrategy = new ElitisticSelectionStrategy(selectionStrategy, 3);

		final int generations = 1000000;
		WorldGenerator worldGenerator = new RandomWorldGenerator(width, height, foodPercentage, Math.exp(Math.log(0.1) / generations));

		String logFileName = "log_" + Util.randomSeed;// nextInt(1000_000_000);
		WorldLogger w = new WorldLogger(antPopulationSize, antEaterPopulationSize,
				maxStatesInMachine, antSight, antEaterSight, selectionStrategy, worldGenerator,
				logFileName, false);

		for (int gen = 0; gen < generations; gen++) {
			if (gen % 100 == 0) {
				Util.log("age " + gen);
			}

			for (int aei = 0; aei < antEaterPopulationSize; aei++) {
				for (int tri = 0; tri < tries; tri++) {
					// Util.log("try " + tri);
					for (int step = 0; step < steps; step++) {
						w.doStep();
					}
					if (tri != tries - 1) {
						w.nextTry();
					}
				}
				if (aei != antEaterPopulationSize - 1) {
					w.nextAntEater();
				}
			}
			if (Util.isSmthPressed() && w.isLogging()) {
				break;
			}
			if (Util.isSmthPressed()) {
				w.startLogging();
			}
			if (gen == generations - 2) {
				w.startLogging();
			}

			if (gen != generations - 1) {
				w.nextAge();
			}
		}
		w.closeLogger();
		Util.log("Fin");
	}
}