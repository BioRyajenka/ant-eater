package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;

public class Cell implements Serializable {
	private static final long serialVersionUID = -5883510131012824967L;

	public enum Type {
		FOOD, NOTHING
	}

	private Type type;
	private Individual individual;

	public Cell(Type type) {
		this.type = type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isOccupied() {
		return individual != null;
	}

	public void setIndividual(Individual individual) {
		this.individual = individual;
	}

	public Individual getIndividual() {
		return individual;
	}

	public boolean hasIndividual() {
		return individual != null;
	}

	public Type getType() {
		return type;
	}
	
	@Override
	public Cell clone() {
		try {
			Cell res = new Cell(type);
			res.individual = individual;
			return res;
		} catch (Exception e) {
		}
		return null;
	}
}