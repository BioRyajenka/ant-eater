package ru.ifmo.ctddev.sushencev.anteater;

public class EncodedGeneration {
	private int gen;
	private Individual[] ants;
	private Individual[] antEaters;

	public EncodedGeneration(int gen, Individual[] ants, Individual[] antEaters) {
		this.gen = gen;
		this.ants = ants;
		this.antEaters = antEaters;
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
