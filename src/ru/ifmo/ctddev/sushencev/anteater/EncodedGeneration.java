package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class EncodedGeneration {
	private int gen;
	private Individual[] ants;
	private Individual[] antEaters;
	private WorldGenerator worldGenerator;

	public EncodedGeneration(int gen, Individual[] ants, Individual[] antEaters, WorldGenerator worldGenerator) {
		this.gen = gen;
		this.ants = ants;
		this.antEaters = antEaters;
		this.worldGenerator = worldGenerator;
	}
	
	public WorldGenerator getWorldGenerator() {
		return worldGenerator;
	}
	
	public int getGenerationNumber() {
		return gen;
	}
	
	public Individual[] getAnts() {
		return ants;
	}
	
	public Individual[] getAntEaters() {
		return antEaters;
	}
}
