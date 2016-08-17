package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SelectionStrategy implements Serializable {
	private static final long serialVersionUID = 1050086391502199663L;

	protected float crossingoverProbability;
	protected float mutationProbability;

	public SelectionStrategy(float crossingoverProbability, float mutationProbability) {
		this.crossingoverProbability = crossingoverProbability;
		this.mutationProbability = mutationProbability;
	}

	protected abstract Individual[] doSelection(Individual[] indivs);

	protected void doMutation(Individual[] indivs) {
		Arrays.stream(indivs).forEach(a -> {
			// if (Util.dice(mutationProbability)) {
			a.mutate(mutationProbability);
			// }
		});
	}

	public Individual[] doSelectionAndMutation(Individual[] indivs) {
		clearLog();
		Individual[] res = doSelection(indivs);
		doMutation(res);
		return res;
	}

	private List<String> log = new ArrayList<>();
	
	protected void clearLog() {
		log.clear();
	}

	protected void appendLog(Individual mother, Individual father, int son) {
		log.add(String.format("(%d, %d) -> %d", mother.getId(), father.getId(), son));
	}

	protected void appendLog(Individual donor, int son) {
		log.add(String.format("%d -> %d", donor.getId(), son));
	}

	public List<String> getLog() {
		return log;
	}
}
