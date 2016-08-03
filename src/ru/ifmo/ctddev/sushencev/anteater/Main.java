package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public class Main {
	public static void main(String[] args) throws IOException {
		int width = 26;
		int height = 26;
		float foodPercentage = .4f;
		int antPopulationSize = 30;
		int antEaterPopulationSize = 30;
		float crossingoverProbability = .5f;
		float mutationProbability = .05f;
		int maxStatesInMachine = 30;

		int steps = 100;
		int tries = 10;

		int foodAmount = (int) (width * height * foodPercentage);

		Sight antSight = new EightCellsSight(c -> c.getType() == Type.FOOD);
		Sight antEaterSight = new EightCellsSight(c -> c.hasIndividual());

		SelectionStrategy selectionStrategy = new TwoRandomSelectionStrategy();

		String logFileName = "log" + Util.nextInt(1000_000_000);
		WorldLogger w = new WorldLogger(width, height, foodAmount, antPopulationSize,
				antEaterPopulationSize, crossingoverProbability, mutationProbability,
				maxStatesInMachine, antSight, antEaterSight, logFileName,
				selectionStrategy, false);

		final int generations = 10000;
		for (int gen = 0; gen < generations; gen++) {
			Util.log("age " + gen);

			for (int aei = 0; aei < antEaterPopulationSize; aei++) {
				for (int tri = 0; tri < tries; tri++) {
					//Util.log("try " + tri);
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
			if (gen == generations - 2) {
				w.startLogging();
			}
			
			if (gen != generations - 1) {
				w.nextAge();
			}
		}
		w.closeLogger();
	}
}