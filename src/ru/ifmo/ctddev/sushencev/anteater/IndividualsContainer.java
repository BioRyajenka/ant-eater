package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Arrays;
import java.util.stream.Stream;

public class IndividualsContainer {
	private SelectionStrategy selectionStrategy;

	private Individual[] individuals;
	private Individual[] pack;

	public IndividualsContainer(int totalSize, int packSize, Sight sight,
			int maxStatesInMachine, SelectionStrategy selectionStrategy, boolean antEater) {
		this.selectionStrategy = selectionStrategy;

		individuals = new Individual[totalSize];
		for (int i = 0; i < totalSize; i++) {
			individuals[i] = new Individual(sight, maxStatesInMachine, null, antEater);
		}
		pack = new Individual[packSize];
		reset();
	}

	// public boolean hasNextPack() {
	// return false;
	// }

	public void reset() {
		singlePos = 0;
		nextPack();
	}

	private int singlePos;

	public void nextPack() {
		if (pack.length == 1) {
			// single mode
			pack[0] = individuals[singlePos++];
			return;
		}
		if (pack.length == individuals.length) {
			// conformity
			pack = individuals;
			return;
		}
		// multi mode
		pack = Util.randomSubVector(individuals, pack.length, new Individual[pack.length]);
	}

	public Individual[] getPack() {
		return pack;
	}

	public Individual[] getAll() {
		return individuals;
	}

	public Stream<Individual> stream() {
		return Arrays.stream(individuals);
	}

	public Stream<Individual> packStream() {
		return Arrays.stream(pack);
	}

	public void refreshPack() {
		Arrays.stream(pack).forEach(Individual::refreshAutomata);
	}

	public void nextGeneration() {
		individuals = selectionStrategy.doSelectionAndMutation(individuals);

		// refresh
		Arrays.stream(individuals).forEach(Individual::refresh);
	}
}
