package ru.ifmo.ctddev.sushencev.anteater;

public class WorldRepeater extends World {
	private static final long serialVersionUID = 2111390727759634411L;
	
	private Individual[] allAnts;
	private Individual[] allAntEaters;
	
	public WorldRepeater(Individual[] allAnts, Individual[] allAntEaters) {
		super(null, null, null);
		this.allAnts = allAnts;
		this.allAntEaters = allAntEaters;
	}
	
	public Individual[] getAntsPack() {
		return antsPack;
	}
	
	public Individual[] getAntEatersPack() {
		return antEatersPack;
	}

	public Individual[] getAllAnts() {
		return allAnts;
	}

	public Individual[] getAllAntEaters() {
		return allAntEaters;
	}

	private EncodedField initialField;
	private Individual[] antsPack;
	private Individual[] antEatersPack;

	public void setField(EncodedField eField) {
		EncodedField copy = (initialField = eField).clone();
		field = copy.getField();
		copy.updateIndividualsPositionsAndRefresh();
		antsPack = copy.getAnts().toArray(new Individual[copy.getAnts().size()]);
		antEatersPack = copy.getAntEaters().toArray(new Individual[copy.getAntEaters().size()]);
		
		steps = 0;
	}

	private int steps = 0;

	@Override
	public void doStep() {
		super.doStep(antsPack);
		super.doStep(antEatersPack);
		steps++;
	}

	public void goToStep(int step) {
		if (initialField == null)
			return;
		if (step == steps + 1) {
			doStep();
			return;
		}
		setField(initialField);
		for (int i = 0; i < step; i++) {
			doStep();
		}
	}

	@Deprecated
	public void nextAge() {
	}

	@Deprecated
	public void nextAntEater() {
	}

	@Deprecated
	public void nextTry() {
	}
}
