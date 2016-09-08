package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

import java.io.Serializable;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;

public interface Sight extends Serializable {
	public InputSignal check(Cell[][] field, Position position, WorldGenerator wg);
	
	public int getInputSignalsNumber();
}