package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.Arrays;

public interface SelectionStrategy extends Serializable {
	public Individual[] doSelection(Individual[] indivs, float crossingoverProbability);

	public default void doMutation(Individual[] indivs, float mutationProbability) {
		Arrays.stream(indivs).forEach(a -> {
			if (Util.dice(mutationProbability)) {
				a.mutate();
			}
		});
	}

	public default Individual[] doSelectionAndMutation(Individual[] indivs,
			float mutationProbability, float crossingoverProbability) {
		Individual[] res = doSelection(indivs, crossingoverProbability);
		doMutation(indivs, mutationProbability);
		return res;
	}
}
