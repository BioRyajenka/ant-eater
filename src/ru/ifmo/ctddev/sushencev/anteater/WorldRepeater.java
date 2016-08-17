package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.util.Arrays;

public class WorldRepeater extends World {
	private static final long serialVersionUID = 2111390727759634411L;

	public WorldRepeater(Individual[] ants, Individual[] antEaters) throws IOException {
		super(0, 0, 0, null, null, null, null);
		this.ants = ants;
		this.antEaters = antEaters;

		Arrays.stream(ants).forEach(a -> a.setHabitat(this));
		Arrays.stream(antEaters).forEach(a -> a.setHabitat(this));
	}

	public Individual[] getAnts() {
		return ants;
	}

	private EncodedField initialField;

	public void setField(EncodedField eField) {
		EncodedField copy = (initialField = eField).clone();
		field = copy.getField();
		copy.updateAntsAndAntEaterPositions(ants);
		antEater = copy.getAntEater();
		Arrays.stream(ants).forEach(a -> a.refresh());
		antEater.refresh();
		steps = 0;
	}

	private int steps = 0;

	@Override
	public void doStep() {
		super.doStep();
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
