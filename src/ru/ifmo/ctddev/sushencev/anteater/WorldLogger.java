package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.util.stream.Collectors;

import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class WorldLogger extends World {
	private transient Logger logger;

	public WorldLogger(IndividualsContainer antsContainer,
			IndividualsContainer antEatersContainer, WorldGenerator worldGenerator,
			String logFileName, boolean logging, int tries, int aeps) throws IOException {
		super(antsContainer, antEatersContainer, worldGenerator);

		logger = new Logger(logFileName);
		this.logging = logging;

		logger.putWorldSettings(tries, aeps);

		onGenerationCreated();
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
		int antsRes = antsContainer.stream().collect(Collectors.summingDouble(
				Individual::getFitness)).intValue();
		int antEatersRes = antEatersContainer.stream().collect(Collectors.summingDouble(
				Individual::getFitness)).intValue();

		antsStatistics.setPlot(gen - 1, antsRes);
		antEatersStatistics.setPlot(gen - 1, antEatersRes);
	}

	@Override
	public void nextAge() {
		collectStatistics();
		antEatersContainer.reset();

		super.nextAge();
	}

	@Override
	protected void onGenerationCreated() {
		if (logger == null)
			return;

		if (logging) {
			logger.putNextGeneration(gen, antsContainer.getAll(), antEatersContainer.getAll(), worldGenerator);
		}

		gen++;
		
		if (gen == 2000) {
			gen = 20* 100;
		}
	}

	@Override
	protected void onWorldRefreshed() {
		if (logger == null)
			return;
		if (logging) {
			logger.putField(field, antsContainer.getPack(), antEatersContainer.getPack());
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