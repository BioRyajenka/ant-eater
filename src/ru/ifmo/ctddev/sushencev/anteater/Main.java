package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.RandomWorldGenerator;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class Main {
	public static void main(String[] args) throws IOException {
		int width = 25;
		int height = 25;
		int antPopulationSize = 20;
		int antEaterPopulationSize = 10;
		float crossingoverProbability = .3f;
		float mutationProbability = .2f;
		int maxStatesInMachine = 5;

		int steps = 100;
		int tries = 10;

		Sight antSight = new SimpleSightWithObstacles(c -> c.getType() == Type.FOOD, 1, c -> c
				.hasIndividual());
		Sight antEaterSight = new SimpleSightWithObstacles(c -> c.hasIndividual() && !c
				.getIndividual().isAntEater(), 1, c -> false);

		SelectionStrategy selectionStrategy = new TwoRandomSelectionStrategy(
				crossingoverProbability, mutationProbability);
		// SelectionStrategy selectionStrategy = new
		// ProportionalSelectionStrategy(
		// crossingoverProbability, mutationProbability);
		selectionStrategy = new ElitisticSelectionStrategy(selectionStrategy, 3);

		final int generations = 10000;
		WorldGenerator worldGenerator = new RandomWorldGenerator(width, height, 0.5f);
		// WorldGenerator worldGenerator = new WallWorldGenerator(width,
		// height);

		String logFileName = "log_" + Util.randomSeed;

		IndividualsContainer antsContainer = new IndividualsContainer(antPopulationSize,
				antPopulationSize, antSight, maxStatesInMachine, selectionStrategy, false);
		IndividualsContainer antEatersContainer = new IndividualsContainer(
				antEaterPopulationSize, 1, antEaterSight, maxStatesInMachine,
				selectionStrategy, true);

		WorldLogger w = new WorldLogger(antsContainer, antEatersContainer, worldGenerator,
				logFileName, false, tries, antEaterPopulationSize);

		for (int gen = 0; gen < generations; gen++) {
			if (gen % 100 == 0) {
				Util.log("age " + gen);
			}

			for (int aei = 0; aei < antEaterPopulationSize; aei++) {
				// Util.log("aei " + aei);
				for (int tri = 0; tri < tries; tri++) {
					// Util.log("try " + tri);
					for (int step = 0; step < steps; step++) {
						// Util.log("step " + step);
						w.doStep();
					}
					if (tri != tries - 1) {
						w.nextTry();
					}
				}
				if (aei != antEaterPopulationSize - 1) {
					w.nextAntEatersPack();
				}
			}

			if (Util.isQuitPressed() && w.isLogging()) {
				break;
			}
			if (Util.isQuitPressed()) {
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
