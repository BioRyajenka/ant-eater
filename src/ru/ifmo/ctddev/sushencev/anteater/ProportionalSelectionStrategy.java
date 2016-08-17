package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Arrays;
import java.util.stream.Collectors;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class ProportionalSelectionStrategy extends SelectionStrategy {
	private static final long serialVersionUID = -3961266162926994876L;

	public ProportionalSelectionStrategy(float crossingoverProbability,
			float mutationProbability) {
		super(crossingoverProbability, mutationProbability);
	}

	@Override
	public Individual[] doSelection(Individual[] indivs) {
		Arrays.sort(indivs, (a, b) -> Float.compare(b.getFitness(), a.getFitness()));
		Individual[] res = new Individual[indivs.length];
		for (int i = 0; i < indivs.length; i += 2) {
			Individual a = getRandom(indivs);
			Individual b;
			int tries = 0;
			do {
				b = getRandom(indivs);
			} while (a == b && tries++ != 100);
			if (Util.dice(crossingoverProbability)) {
				Pair<Individual, Individual> p = a.cross(b);
				res[i] = p.first;
				res[i + 1] = p.second;
			} else {
				res[i] = a.copy();
				res[i + 1] = b.copy();
			}
		}
		return res;
	}

	private Individual getRandom(Individual[] indivs) {
		float sum = (float) Arrays.stream(indivs).collect(Collectors.summingDouble(i -> i
				.getFitness())).doubleValue();
		int k = (int) (sum * Util.dice());
		for (Individual i : indivs) {
			k -= i.getFitness();
			if (k <= 0) {
				return i;
			}
		}
		return null;
	}
}
