package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.Arrays;

import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;

public class World implements Serializable {
	private static final long serialVersionUID = -1313054894531054367L;

	protected transient Cell[][] field;

	protected transient IndividualsContainer antsContainer;
	protected transient IndividualsContainer antEatersContainer;

	private transient WorldGenerator worldGenerator;

	public World(IndividualsContainer antsContainer, IndividualsContainer antEatersContainer,
			WorldGenerator worldGenerator) {
		this.worldGenerator = worldGenerator;

		this.antsContainer = antsContainer;
		this.antEatersContainer = antEatersContainer;

		onGenerationCreated();
		refreshWorld();
	}

	protected void onGenerationCreated() {

	}

	public void nextAge() {
		antsContainer.nextGeneration();
		antsContainer.reset();
		antEatersContainer.nextGeneration();
		antEatersContainer.reset();

		onGenerationCreated();

		refreshWorld();
	}
	
	//public boolean hasNextAntsPack() {
	//	return antsContainer.hasNextPack();
	//}
	
	public void nextAntsPack() {
		antsContainer.nextPack();
		refreshWorld();
	}
	
	//public boolean hasNextAntEatersPack() {
	//	return antEatersContainer.hasNextPack();
	//}

	public void nextAntEatersPack() {
		antEatersContainer.nextPack();
		refreshWorld();
	}

	public void nextTry() {
		refreshWorld();
	}

	private void refreshWorld() {
		if (worldGenerator == null)
			return;

		antsContainer.refreshPack();
		antEatersContainer.refreshPack();

		field = worldGenerator.generateWorld(antsContainer.getPack(), antEatersContainer
				.getPack());

		onWorldRefreshed();
	}

	protected void onWorldRefreshed() {

	}
	
	protected void doStep(Individual[] individuals) {
		Arrays.stream(individuals).forEach(i -> {
			if (!i.isDead()) {
				processOutputSignal(i, i.doStep(field));
			}
		});
	}

	public void doStep() {
		doStep(antsContainer.getPack());
		doStep(antEatersContainer.getPack());
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

	public Cell[][] getField() {
		return field;
	}
}
