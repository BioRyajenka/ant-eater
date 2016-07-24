package world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Machine.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Util;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class World {
	protected Cell[][] field;

	protected int width;
	protected int height;
	protected int foodAmount;
	protected float crossingoverProbability;
	protected float mutationProbability;
	// protected int eliteSize;

	protected Individual antEater;
	private Iterator<Individual> antEaterIterator;
	protected List<Individual> antEaters = new ArrayList<>();
	protected List<Individual> ants = new ArrayList<>();

	public World(int width, int height, int foodAmount, int antsNumber,
			int antEatersNumber, float crossingoverProbability,
			float mutationProbability, /* int eliteSize,*/ int maxStatesInMachine,
			Sight antSight, Sight antEaterSight) {
		this.width = width;
		this.height = height;

		this.foodAmount = foodAmount;
		this.crossingoverProbability = crossingoverProbability;
		this.mutationProbability = mutationProbability;
		// this.eliteSize = eliteSize;

		field = new Cell[height][];
		for (int i = 0; i < height; i++) {
			field[i] = new Cell[width];
			for (int j = 0; j < width; j++) {
				field[i][j] = new Cell(Type.NOTHING);
			}
		}

		for (int i = 0; i < antEatersNumber; i++) {
			antEaters.add(new Individual(this, antEaterSight, maxStatesInMachine));
		}

		for (int i = 0; i < antsNumber; i++) {
			ants.add(new Individual(this, antSight, maxStatesInMachine));
		}

		antEaterIterator = antEaters.iterator();
		antEater = antEaterIterator.next();

		refreshWorld();
	}

	public void nextAge() {
		createNextGeneration(ants);
		createNextGeneration(antEaters);

		// summaryFitness = new HashMap<>();

		antEaterIterator = antEaters.iterator();
		antEater = antEaterIterator.next();

		refreshWorld();
	}

	// private Map<Individual, Integer> summaryFitness = new HashMap<>();

	public void nextAntEater() {
		antEater = antEaterIterator.next();
		refreshWorld();
	}

	public void nextTry() {
		// ants.forEach(a -> summaryFitness.compute(a, (k, v) -> v == null ? a
		// .getEatenFoodAmount() : v + a.getEatenFoodAmount()));

		refreshWorld();
	}

	private void refreshWorld() {
		clearWorld();
		generateFood(foodAmount);
		placeAnts();
		placeAntEater();
	}

	public void doStep() {
		for (Individual i : ants) {
			if (!i.isDead()) {
				processOutputSignal(i, i.doStep());
			}
		}

		processOutputSignal(antEater, antEater.doStep());
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
			Cell nc = field[pos.x][pos.y];
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
			field[i.getPosition().x][i.getPosition().y].setIndividual(null);
			i.setPosition(pos);
			break;
		}
	}

	private Position getForwardPosition(Position pos) {
		int x = pos.x;
		int y = pos.y;
		int rot = pos.rot;
		
		if (rot == 0) x--;
		if (rot == 1) y++;
		if (rot == 2) x++;
		if (rot == 3) y--;
		
		if (x >= height) x -= height;
		if (x < 0) x += height;
		if (y >= width) y -= width;
		if (y < 0) y += width;
		
		return new Position(x, y, rot);
	}

	private void createNextGeneration(List<Individual> indivs) {
		// sorting downwards
		indivs.sort((a, b) -> Integer.compare(b.getEatenFoodAmount(), a
				.getEatenFoodAmount()));
				/*// elitism
				List<Individual> elite = new ArrayList<>();
				for (int i = 0; i < eliteSize; i++) {
					elite.add(ants.get(i));
				}*/

		// crossingover
		int summaryFitness = indivs.stream().collect(Collectors.summingInt(i -> i
				.getEatenFoodAmount()));
		for (int i = 0; i < indivs.size(); i++) {
			Individual cur = indivs.get(i);
			if (!Util.dice(crossingoverProbability)) {
				continue;
			}
			float accFitness = (summaryFitness - cur.getEatenFoodAmount()) * Util.dice();
			for (int j = 0; j < indivs.size(); j++) {
				Individual match = indivs.get(j);
				if (match == cur) {
					continue;
				}
				accFitness -= match.getEatenFoodAmount();
				if (accFitness <= 0) {
					Pair<Individual, Individual> res = cur.cross(match);
					indivs.set(i, res.first);
					indivs.set(j, res.second);
					break;
				}
			}
		}

		// mutation
		indivs.stream().forEach(a -> {
			if (Util.dice(mutationProbability))
				a.mutate();
		});
		// refresh
		indivs.stream().forEach(a -> a.refresh());
	}

	private void clearWorld() {
		Arrays.stream(field).flatMap(a -> Arrays.stream(a)).forEach(c -> {
			c.setType(Type.NOTHING);
			c.setIndividual(null);
		});
	}

	private void doNTimes(int n, BiFunction<Integer, Integer, Boolean> failFunction,
			BiConsumer<Integer, Integer> action) {
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
		doNTimes(foodAmount, (x, y) -> field[x][y].getType() == Type.FOOD, (x,
				y) -> field[x][y].setType(Type.FOOD));
	}

	private void placeAnts() {
		if (width * height <= ants.size()) {
			throw new RuntimeException("too many ants for such small field");
		}
		Iterator<Individual> it = ants.iterator();
		doNTimes(ants.size(), (x, y) -> field[x][y].isOccupied() || (x == width / 2
				&& y == height / 2), (x, y) -> {
					Individual ant = it.next();
					field[x][y].setIndividual(ant);
					ant.setPosition(x, y, Util.nextInt(4));
				});
	}

	private void placeAntEater() {
		field[width / 2][height / 2].setIndividual(antEater);
		antEater.setPosition(width / 2, height / 2, Util.nextInt(4));
	}

	public WorldSnapshot takeSnapshot() {
		return new WorldSnapshot(this);
	}
}