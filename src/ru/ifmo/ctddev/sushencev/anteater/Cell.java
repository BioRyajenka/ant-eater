package ru.ifmo.ctddev.sushencev.anteater;

public class Cell {
	public enum Type {FOOD, NOTHING}
	
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
}
