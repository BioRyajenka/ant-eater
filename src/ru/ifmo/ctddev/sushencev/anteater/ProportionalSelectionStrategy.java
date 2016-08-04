package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Arrays;
import java.util.stream.Collectors;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class ProportionalSelectionStrategy implements SelectionStrategy {
	private static final long serialVersionUID = -3961266162926994876L;

	@Override
	public Individual[] doSelection(Individual[] indivs, float crossingoverProbability) {
		Arrays.sort(indivs, (a, b) -> Integer.compare(b.getEatenFoodAmount(), a
				.getEatenFoodAmount()));
		Individual[] res = new Individual[indivs.length];
		for (int i = 0; i < indivs.length; i += 2) {
			Individual a = getRandom(indivs);
			Individual b;
			do {
				b = getRandom(indivs);
			} while (a == b);
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
		int sum = Arrays.stream(indivs).collect(Collectors.summingInt(i -> i.getEatenFoodAmount()));
		int k = (int) (sum * Util.dice());
		for (Individual i : indivs) {
			k -= i.getEatenFoodAmount();
			if (k <= 0) {
				return i;
			}
		}
		return null;
	}
}
