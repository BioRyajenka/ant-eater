package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;

public interface SelectionStrategy extends Serializable {
	public Individual[] doSelection(Individual[] indivs, float crossingoverProbability);
}
