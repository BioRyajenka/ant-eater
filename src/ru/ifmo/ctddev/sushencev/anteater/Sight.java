package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;

public interface Sight {
	public InputSignal check(World world, Position position);
}