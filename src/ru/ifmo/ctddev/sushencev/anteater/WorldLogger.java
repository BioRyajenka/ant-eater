package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorldLogger extends World {
	private static final long serialVersionUID = -2487826639297154145L;
	
	private transient Logger logger;
	
	public WorldLogger(int width, int height, int foodAmount, int antsNumber,
			int antEatersNumber, float crossingoverProbability,
			float mutationProbability, int maxStatesInMachine,
			Sight antSight, Sight antEaterSight, String logFileName, 
			SelectionStrategy selectionStrategy) throws IOException {
		super(width, height, foodAmount, antsNumber, antEatersNumber,
				crossingoverProbability, mutationProbability, maxStatesInMachine,
				antSight, antEaterSight, selectionStrategy);
		logger = new Logger(logFileName);

		onGenerationCreated();
		
		// see WorldRepeater's constructor
		if (antEaters.length > 0) {
			antEater = antEaters[currentAntEater = 0];
		}

		onWorldRefreshed();
	}
	
	private transient int gen = 0;
	//private transient int age = 0;
	//private transient int ae = 0;
	//private transient int tri = 0;
	
	/*@Override
	public void nextAge() {
		age++;
		ae = tri = 0;
		super.nextAge();
	}
	
	@Override
	public void nextAntEater() {
		ae++;
		tri = 0;
		super.nextAntEater();
	}
	
	@Override
	public void nextTry() {
		tri++;
		super.nextTry();
	}
	*/
	
	public void startLogging() {
		logging = true;
	}
	
	private boolean logging = false;
	
	@Override
	protected void onGenerationCreated() {
		if (logger == null) return;
		Map<String, Integer> description = new HashMap<>();
		description.put("gen", gen++);
		
		if (logging) {
			logger.putNextGeneration(description, ants, antEaters);
		}
	}
	
	@Override
	protected void onWorldRefreshed() {
		if (logger == null) return;
		//Map<String, Integer> description = new HashMap<>();
		//description.put("age", age);
		//description.put("ae", ae);
		//description.put("tri", tri);
		if (logging) {
			logger.putField(field, ants, antEater);
		}
	}
	
	public void closeLogger() throws IOException {
		logger.close();
	}
}