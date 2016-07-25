package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import world.EightCellsSight;
import world.Logger;
import world.Sight;
import world.World;

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

		for (int gen = 0; gen < generations; gen++) {
			logger.updateDescription("generation", gen + "/" + generations);
			for (int aei = 0; aei < antEaterPopulationSize; aei++) {
				logger.updateDescription("ant eater number", aei + "/"
						+ antEaterPopulationSize);
				for (int tri = 0; tri < tries; tri++) {
					logger.updateDescription("try", tri + "/" + tries);
					for (int step = 0; step < steps; step++) {
						logger.updateDescription("step", step + "/" + steps);
						logger.saveWorldSnapshot(w);
						w.doStep();
					}
					w.nextTry();
				}
				w.nextAntEater();
			}
			w.nextAge();
		}
		
		logger.close();
	}
}