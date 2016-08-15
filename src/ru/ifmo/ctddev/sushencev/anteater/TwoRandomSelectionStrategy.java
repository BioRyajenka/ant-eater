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
			Individual a = indivs[Util.nextInt(indivs.length)];
			Individual b = indivs[Util.nextInt(indivs.length)];
			Individual c = indivs[Util.nextInt(indivs.length)];
			Individual d = indivs[Util.nextInt(indivs.length)];
			if (a == b || c == d) {
				i -= 2;
				continue;
			}
			if (b.getEatenFoodAmount() > a.getEatenFoodAmount()) {
				a = b;
			}
			if (d.getEatenFoodAmount() > c.getEatenFoodAmount()) {
				c = d;
			}
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
}
