package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.Arrays;

import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;

public class World implements Serializable {
	private static final long serialVersionUID = -1313054894531054367L;

	protected transient Cell[][] field;

	protected Individual antEater;
	protected int currentAntEater;
	protected Individual[] antEaters;
	protected Individual[] ants;

	private transient SelectionStrategy selectionStrategy;
	private transient WorldGenerator worldGenerator;

	public World(int antsNumber, int antEatersNumber, int maxStatesInMachine, Sight antSight,
			Sight antEaterSight, SelectionStrategy selectionStrategy,
			WorldGenerator worldGenerator) {
		this.selectionStrategy = selectionStrategy;
		this.worldGenerator = worldGenerator;

		antEaters = new Individual[antEatersNumber];
		for (int i = 0; i < antEatersNumber; i++) {
			antEaters[i] = new Individual(this, antEaterSight, maxStatesInMachine, "antEater "
					+ i, true);
		}

		ants = new Individual[antsNumber];
		for (int i = 0; i < antsNumber; i++) {
			ants[i] = new Individual(this, antSight, maxStatesInMachine, "ant " + i, false);
		}

		onGenerationCreated();

		// see WorldRepeater's constructor
		if (antEaters.length > 0) {
			antEater = antEaters[currentAntEater = 0];
		}

		refreshWorld();
	}

	protected void onGenerationCreated() {

	}

	public void nextAge() {
		ants = createNextGeneration(ants);
		antEaters = createNextGeneration(antEaters);

		onGenerationCreated();

		antEater = antEaters[currentAntEater = 0];

		refreshWorld();
	}

	public void nextAntEater() {
		antEater = antEaters[++currentAntEater];
		refreshWorld();
	}

	public void nextTry() {
		refreshWorld();
	}

	private void refreshWorld() {
		if (worldGenerator == null)
			return;

		Arrays.stream(ants).forEach(a -> a.refreshAutomata());
		if (antEater != null) {
			antEater.refreshAutomata();
			field = worldGenerator.generateWorld(ants, antEater);
		} else {
			field = worldGenerator.generateWorld(ants);
		}

		onWorldRefreshed();
	}

	protected void onWorldRefreshed() {

	}

	public void doStep() {
		for (Individual i : ants) {
			if (!i.isDead()) {
				processOutputSignal(i, i.doStep());
			}
		}
		if (antEater != null) {
			processOutputSignal(antEater, antEater.doStep());
		}
	}

	public Individual findAnt(int i) {
		for (Individual ind : ants) {
			if (ind.toString().equals("ant " + i)) {
				return ind;
			}
		}
		return null;
	}

	private void processOutputSignal(Individual i, OutputSignal out) {
		switch (out) {
		case NOTHING:
			break;
		case LEFT:
			i.getPosition().rot = (i.getPosition().rot + 3) % 4;
			break;
		case RIGHT:
			i.getPosition().rot = (i.getPosition().rot + 1) % 4;
			break;
		case FORWARD:
			Position pos = getForwardPosition(i.getPosition(), field[0].length, field.length);
			Cell nc = field[pos.y][pos.x];
			if (i.isAntEater()) {
				if (nc.isOccupied()) {
					if (nc.getIndividual().isAntEater()) {
						i.die();
						break;
					} else {
    					nc.getIndividual().die();
    					i.incEatenFoodAmount();
					}
				}
			} else {
				if (nc.isOccupied()) {
					i.die();
					break;
				}
				if (nc.getType() == Type.FOOD) {
					i.incEatenFoodAmount();
					nc.setType(Type.NOTHING);
				}
			}
			nc.setIndividual(i);
			field[i.getPosition().y][i.getPosition().x].setIndividual(null);
			i.setPosition(pos);
			break;
		}
	}

	protected static Position getForwardPosition(Position pos, int width, int height) {
		int x = pos.x;
		int y = pos.y;
		int rot = pos.rot;

		if (rot == 0)
			y--;
		if (rot == 1)
			x++;
		if (rot == 2)
			y++;
		if (rot == 3)
			x--;

		// torus
		if (y >= height)
			y -= height;
		if (y < 0)
			y += height;
		if (x >= width)
			x -= width;
		if (x < 0)
			x += width;

		return new Position(x, y, rot);
	}

	private Individual[] createNextGeneration(Individual[] indivs) {
		// see WorldRepeater's constructor
		if (selectionStrategy == null) {
			return indivs;
		}
		Individual[] res = selectionStrategy.doSelectionAndMutation(indivs);

		// refresh
		Arrays.stream(res).forEach(a -> a.refresh());

		return res;
	}

	public Cell[][] getField() {
		return field;
	}

	public Individual[] getAnts() {
		return ants;
	}
}