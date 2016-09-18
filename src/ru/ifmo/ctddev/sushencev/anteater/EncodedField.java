package ru.ifmo.ctddev.sushencev.anteater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncodedField implements Cloneable {
	private Cell[][] field;
	private int[] antsRots;
	private int[] antEatersRots;

	public EncodedField(Cell[][] field, int[] antsRots, int[] antEatersRots) {
		this.field = field;
		this.antsRots = antsRots;
		this.antEatersRots = antEatersRots;
	}

	public Cell[][] getField() {
		return field;
	}

	public void updateIndividualsPositionsAndRefresh() {
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				Cell c = field[i][j];
				if (c.getIndividual() != null) {
					c.getIndividual().setPosition(j, i, 0);
				}
			}
		}
		List<Individual> ants = getAnts();
		updateRots(ants, antsRots);
		List<Individual> antEaters = getAntEaters();
		updateRots(antEaters, antEatersRots);
		ants.forEach(Individual::refresh);
		antEaters.forEach(Individual::refresh);
	}
	
	private void updateRots(List<Individual> individuals, int[] rots) {
		if (individuals.size() != rots.length) {
			throw new AssertionError("individuals.size() = " + individuals.size() + " rots.length = " + rots.length);
		}
		
		for (int i = 0; i < rots.length; i++) {
			individuals.get(i).getPosition().rot = rots[i];
		}
	}

	private List<Individual> getIndividuals(boolean antEater) {
		List<Individual> res = new ArrayList<>();
		Arrays.stream(field).flatMap(row -> Arrays.stream(row)).filter(c -> c.hasIndividual()
				&& c.getIndividual().isAntEater() == antEater).map(c -> c.getIndividual())
				.forEach(i -> res.add(i));
		return res;
	}

	protected List<Individual> getAnts() {
		return getIndividuals(false);
	}

	protected List<Individual> getAntEaters() {
		return getIndividuals(true);
	}

	@Override
	public EncodedField clone() {
		try {
			EncodedField res = (EncodedField) super.clone();
			res.field = new Cell[field.length][field[0].length];
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					res.field[i][j] = field[i][j].clone();
				}
			}
			return res;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
