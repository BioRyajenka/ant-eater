package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class TwoRandomSelectionStrategy extends SelectionStrategy {
	private static final long serialVersionUID = -8925764351760559352L;

	public TwoRandomSelectionStrategy(float crossingoverProbability, float mutationProbability) {
		super(crossingoverProbability, mutationProbability);
	}
	
	@Override
	public Individual[] doSelection(Individual[] indivs) {
		Individual[] res = new Individual[indivs.length];
		for (int i = 0; i < indivs.length; i += 2) {
			Individual a = getRandom(indivs);
			Individual b = getRandom(indivs);
			Individual c = getRandom(indivs);
			Individual d = getRandom(indivs);
			if (a == b || c == d) {
				i -= 2;
				continue;
			}
			a = max(a, b);
			c = max(c, d);
			if (Util.dice(crossingoverProbability)) {
				Pair<Individual, Individual> p = a.cross(c);
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
		Individual a = indivs[Util.nextInt(indivs.length)];
		Individual b = indivs[Util.nextInt(indivs.length)];
		return max(a, b);
	}

	private Individual max(Individual a, Individual b) {
		if (b.getFitness() > a.getFitness()) {
			return b;
		}
		return a;
	}
}
