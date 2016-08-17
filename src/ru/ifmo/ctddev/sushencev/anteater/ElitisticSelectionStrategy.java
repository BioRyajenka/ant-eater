package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Arrays;

public class ElitisticSelectionStrategy extends SelectionStrategy {
	private static final long serialVersionUID = 6908809396692580550L;

	private SelectionStrategy secondary;
	private int eliteSize;

	public ElitisticSelectionStrategy(SelectionStrategy secondary, int eliteSize) {
		super(secondary.crossingoverProbability, secondary.mutationProbability);
		this.secondary = secondary;
		this.eliteSize = eliteSize;
	}

	@Override
	protected Individual[] doSelection(Individual[] indivs) {
		// TODO: logging
		Arrays.sort(indivs, (a, b) -> Float.compare(b.getFitness(), a.getFitness()));
		Individual[] elite = Arrays.copyOf(indivs, eliteSize);
		Individual[] res = secondary.doSelection(indivs);
		for (int i = 0; i < eliteSize; i++) {
			res[i] = elite[i].copy();
		}
		return res;
	}

	@Override
	protected void doMutation(Individual[] indivs) {
		for (int i = eliteSize; i < indivs.length; i++) {
			indivs[i].mutate(secondary.mutationProbability);
		}
	}
}