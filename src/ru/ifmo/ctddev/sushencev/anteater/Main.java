package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public class Main {
	public static void main(String[] args) throws IOException {
		int width = 25;
		int height = 25;
		float foodPercentage = .2f;
		int antPopulationSize = 12;
		int antEaterPopulationSize = 10;
		float crossingoverProbability = 0f;
		float mutationProbability = .15f;
		int maxStatesInMachine = 5;

		int steps = 100;
		int tries = 10;

		Sight antSight = new EightCellsSight(c -> c.getType() == Type.FOOD);
		Sight antEaterSight = new EightCellsSight(c -> c.hasIndividual());

		// SelectionStrategy selectionStrategy = new TwoRandomSelectionStrategy(
		// crossingoverProbability, mutationProbability);
		SelectionStrategy selectionStrategy = new ProportionalSelectionStrategy(
				crossingoverProbability, mutationProbability);
		selectionStrategy = new ElitisticSelectionStrategy(selectionStrategy, 3);

		WorldGenerator worldGenerator = new ArenaWorldGenerator(width, height, foodPercentage);

		String logFileName = "log_" + Util.randomSeed;// nextInt(1000_000_000);
		WorldLogger w = new WorldLogger(antPopulationSize, antEaterPopulationSize,
				maxStatesInMachine, antSight, antEaterSight, selectionStrategy, worldGenerator,
				logFileName, false);

		final int generations = 1000000;
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