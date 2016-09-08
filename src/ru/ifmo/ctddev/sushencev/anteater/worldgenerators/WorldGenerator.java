package ru.ifmo.ctddev.sushencev.anteater.worldgenerators;

import java.io.Serializable;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;

public interface WorldGenerator extends Serializable {
	public Cell[][] generateWorld(Individual[] ants, Individual[] antEaters);
	
	/**
	 * Corrects coordinates.
	 * E.g. if generator generates square field of size 3x3, 
	 * then (-1;1) will be corrected to (2;1)
	 * @param x
	 * @param y
	 * @return null if individual should die or corrected coordinates otherwise
	 */
	public IntPair correctCoordinates(int x, int y);
}
