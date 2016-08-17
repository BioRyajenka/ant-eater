package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.Arrays;

public abstract class SelectionStrategy implements Serializable {
	private static final long serialVersionUID = 1050086391502199663L;
	
	protected float crossingoverProbability;
	protected float mutationProbability;

	public SelectionStrategy(float crossingoverProbability, float mutationProbability) {
		this.crossingoverProbability = crossingoverProbability;
		this.mutationProbability = mutationProbability;
	}
	
	public abstract Individual[] doSelection(Individual[] indivs);

	public void doMutation(Individual[] indivs) {
		Arrays.stream(indivs).forEach(a -> {
			//if (Util.dice(mutationProbability)) {
				a.mutate(mutationProbability);
			//}
		});
	}

	public Individual[] doSelectionAndMutation(Individual[] indivs) {
		Individual[] res = doSelection(indivs);
		doMutation(indivs);
		return res;
	}
}
