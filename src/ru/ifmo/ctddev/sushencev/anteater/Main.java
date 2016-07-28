package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

		int generations = 10000;
		int steps = 100;
		int tries = 10;

		int foodAmount = (int) (width * height * foodPercentage);

		Sight antSight = new EightCellsSight(c -> c.getType() == Type.FOOD);
		Sight antEaterSight = new EightCellsSight(c -> c.hasIndividual());

		World w = new World(width, height, foodAmount, antPopulationSize,
				antEaterPopulationSize, crossingoverProbability, mutationProbability,
				maxStatesInMachine, antSight, antEaterSight);

		String logFileName = "log" + Util.nextInt(1000_000_000);
		Logger logger = new Logger(logFileName);
		
		logger.updatePresets("generations", generations);
		logger.updatePresets("ant-eaters", antEaterPopulationSize);
		logger.updatePresets("tries", tries);
		logger.updatePresets("frames", steps);

		for (int gen = 0; gen < generations; gen++) {
			System.out.println("Age " + gen);
			logger.updateDescription("generation", gen);
			for (int aei = 0; aei < antEaterPopulationSize; aei++) {
				logger.updateDescription("ant-eater", aei);
				for (int tri = 0; tri < tries; tri++) {
					logger.updateDescription("try", tri);
					for (int step = 0; step < steps; step++) {
						logger.updateDescription("frame", step);
						logger.saveWorldSnapshot(w);
						w.doStep();
					}
					w.nextTry();
				}
				if (aei != antEaterPopulationSize - 1) {
					w.nextAntEater();
				}
			}
			w.nextAge();
		}

		logger.close();
	}
}