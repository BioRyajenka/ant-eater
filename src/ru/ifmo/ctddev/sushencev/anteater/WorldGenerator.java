package ru.ifmo.ctddev.sushencev.anteater;

public interface WorldGenerator {
	public Cell[][] generateWorld(Individual[] ants, Individual antEater);
}
