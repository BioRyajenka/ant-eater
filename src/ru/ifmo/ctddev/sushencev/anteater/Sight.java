package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;

import java.io.Serializable;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;

public interface Sight extends Serializable {
	public InputSignal check(World world, Position position);
}