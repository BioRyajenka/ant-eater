package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldLogger extends World {
	private static final long serialVersionUID = -2487826639297154145L;

	private transient Logger logger;

	public WorldLogger(int antsNumber, int antEatersNumber, int maxStatesInMachine,
			Sight antSight, Sight antEaterSight, SelectionStrategy selectionStrategy,
			WorldGenerator worldGenerator, String logFileName, boolean logging,
			int antEaterPopulationSize, int tries)
			throws IOException {
		super(antsNumber, antEatersNumber, maxStatesInMachine, antSight, antEaterSight,
				selectionStrategy, worldGenerator);
		logger = new Logger(logFileName);

		this.logging = logging;
		
		logger.putWorldSettings(antEaterPopulationSize, tries);

		onGenerationCreated();

		// see WorldRepeater's constructor
		if (antEaters.length > 0) {
			antEater = antEaters[currentAntEater = 0];
		}

		onWorldRefreshed();
	}

	private transient int gen = 0;

	public void startLogging() {
		logging = true;
	}

	public boolean isLogging() {
		return logging;
	}

	private boolean logging = false;

	private Statistics antsStatistics = new Statistics("ants");
	private Statistics antEatersStatistics = new Statistics("ant-eaters");

	private void collectStatistics() {
		int antsRes = Arrays.stream(ants).collect(Collectors.summingDouble(Individual::getFitness)).intValue();
		int antEatersRes = Arrays.stream(antEaters).collect(Collectors.summingDouble(Individual::getFitness)).intValue();

		antsStatistics.setPlot(gen - 1, antsRes);
		antEatersStatistics.setPlot(gen - 1, antEatersRes);
	}

	@Override
	public void nextAge() {
		collectStatistics();			

		super.nextAge();
	}

	@Override
	protected void onGenerationCreated() {
		if (logger == null)
			return;
		Map<String, Integer> description = new HashMap<>();
		description.put("gen", gen++);

		if (logging) {
			logger.putNextGeneration(description, ants, antEaters);
		}

	}

	@Override
	protected void onWorldRefreshed() {
		if (logger == null)
			return;
		if (logging) {
			logger.putField(field, ants, antEater);
		}
	}

	public void closeLogger() throws IOException {
		collectStatistics();
		Util.log("saving statistics");
		logger.putStatistics(antsStatistics);
		logger.putStatistics(antEatersStatistics);
		logger.close();
	}
}