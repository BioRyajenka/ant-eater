package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;

public class World implements Serializable {
	private static final long serialVersionUID = -1313054894531054367L;

	protected transient Cell[][] field;

	private int foodAmount;
	private float crossingoverProbability;
	private float mutationProbability;

	protected Individual antEater;
	protected int currentAntEater;
	protected Individual[] antEaters;
	protected Individual[] ants;

	private SelectionStrategy selectionStrategy;

	public World(int width, int height, int foodAmount, int antsNumber, int antEatersNumber,
			float crossingoverProbability, float mutationProbability, int maxStatesInMachine,
			Sight antSight, Sight antEaterSight, SelectionStrategy selectionStrategy) {

		this.foodAmount = foodAmount;
		this.crossingoverProbability = crossingoverProbability;
		this.mutationProbability = mutationProbability;
		this.selectionStrategy = selectionStrategy;

		field = new Cell[height][];
		for (int i = 0; i < height; i++) {
			field[i] = new Cell[width];
			for (int j = 0; j < width; j++) {
				field[i][j] = new Cell(Type.NOTHING);
			}
		}

		antEaters = new Individual[antEatersNumber];
		for (int i = 0; i < antEatersNumber; i++) {
			antEaters[i] = new Individual(this, antEaterSight, maxStatesInMachine, "ant-eater "
					+ i);
		}

		ants = new Individual[antsNumber];
		for (int i = 0; i < antsNumber; i++) {
			ants[i] = new Individual(this, antSight, maxStatesInMachine, "ant " + i);
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

	int gen = 0;

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
		if (field.length == 0)
			return;

		clearWorld();
		generateFood(foodAmount);

		Arrays.stream(ants).forEach(a -> a.refreshAutomata());
		antEater.refreshAutomata();

		placeAnts();
		placeAntEater();

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

		// processOutputSignal(antEater, antEater.doStep());
	}

	private void processOutputSignal(Individual i, OutputSignal out) {
		switch (out) {
		case LEFT:
			i.getPosition().rot = (i.getPosition().rot + 3) % 4;
			break;
		case RIGHT:
			i.getPosition().rot = (i.getPosition().rot + 1) % 4;
			break;
		case FORWARD:
			Position pos = getForwardPosition(i.getPosition());
			Cell nc = field[pos.y][pos.x];
			if (i == antEater) {
				if (nc.isOccupied()) {
					nc.getIndividual().die();
					antEater.incEatenFoodAmount();
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

	private Position getForwardPosition(Position pos) {
		final int height = field.length;
		final int width = field[0].length;

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
		Individual[] res = selectionStrategy.doSelectionAndMutation(indivs,
				mutationProbability, crossingoverProbability);

		// refresh
		Arrays.stream(res).forEach(a -> a.refresh());

		return res;
	}

	private void clearWorld() {
		Arrays.stream(field).flatMap(a -> Arrays.stream(a)).forEach(c -> {
			c.setType(Type.NOTHING);
			c.setIndividual(null);
		});
	}

	private void doNTimes(int n, BiFunction<Integer, Integer, Boolean> failFunction,
			BiConsumer<Integer, Integer> action) {
		final int height = field.length;
		final int width = field[0].length;
		for (int i = 0; i < n; i++) {
			int x = Util.nextInt(width);
			int y = Util.nextInt(height);
			if (failFunction.apply(x, y)) {
				i--;
				continue;
			}
			action.accept(x, y);
		}
	}

	private void generateFood(int foodAmount) {
		doNTimes(foodAmount, (x, y) -> field[y][x].getType() == Type.FOOD, (x,
				y) -> field[y][x].setType(Type.FOOD));
	}

	private void placeAnts() {
		final int height = field.length;
		final int width = field[0].length;

		if (width * height <= ants.length) {
			throw new RuntimeException("too many ants for such small field");
		}
		Iterator<Individual> it = Arrays.asList(ants).iterator();
		doNTimes(ants.length, (x, y) -> field[y][x].isOccupied() || field[y][x]
				.getType() == Type.FOOD || (x == width / 2 && y == height / 2), (x, y) -> {
					Individual ant = it.next();
					field[y][x].setIndividual(ant);
					ant.setPosition(x, y, Util.nextInt(4));
				});
	}

	private void placeAntEater() {
		final int height = field.length;
		final int width = field[0].length;
		field[width / 2][height / 2].setIndividual(antEater);
		antEater.setPosition(width / 2, height / 2, Util.nextInt(4));
	}

	public Cell[][] getField() {
		return field;
	}

	public Individual[] getAnts() {
		return ants;
	}

	public Individual getCurrentAntEater() {
		return antEater;
	}
}