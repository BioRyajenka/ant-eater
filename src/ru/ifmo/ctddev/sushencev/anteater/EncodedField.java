package ru.ifmo.ctddev.sushencev.anteater;

public class EncodedField implements Cloneable {
	private Cell[][] field;
	private int[] antsRots;
	private int antEaterRot;
	private Individual antEater;
	
	public EncodedField(Cell[][] field, int[] antsRots, int antEaterRot, Individual antEater) {
		this.field = field;
		this.antsRots = antsRots;
		this.antEater = antEater;
		this.antEaterRot = antEaterRot;
	}
	
	public Cell[][] getField() {
		return field;
	}
	
	public void updateAntsAndAntEaterPosition(Individual[] ants) {
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				Cell c = field[i][j];
				if (c.hasIndividual()) {
					c.getIndividual().setPosition(j, i, 0);
				}
			}
		}
		for (int i = 0; i < antsRots.length; i++) {
			ants[i].getPosition().rot = antsRots[i];
		}
		antEater.getPosition().rot = antEaterRot;
	}
	
	public Individual getAntEater() {
		return antEater;
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
		}
		return null;
	}
}
