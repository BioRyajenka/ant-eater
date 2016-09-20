package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class World {
	protected Cell[][] field;

	protected IndividualsContainer antsContainer;
	protected IndividualsContainer antEatersContainer;

	protected WorldGenerator worldGenerator;

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
	
	public void nextAntsPack() {
		antsContainer.nextPack();
		refreshWorld();
	}

	public void nextAntEatersPack() {
		antEatersContainer.nextPack();
		antsContainer.reset();
		refreshWorld();
	}
	
	public void nextTry() {
		antsContainer.reset();
		refreshWorld();
	}

	private void refreshWorld() {
		if (antsContainer == null)
			return;

		antsContainer.refreshPackAutomata();
		antEatersContainer.refreshPackAutomata();

		field = worldGenerator.generateWorld(antsContainer.getPack(), antEatersContainer
				.getPack());

		onWorldRefreshed();
	}

	protected void onWorldRefreshed() {
		
	}
	
	protected void doStep(Individual[] individuals) {
		for (Individual i : individuals) {
			if (!i.isDead()) {
				processOutputSignal(i, i.doStep(field, worldGenerator));
			}
		}
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
			IntPair pos = getForwardCoordinates(i.getPosition(), worldGenerator);
			if (pos == null) {
				i.die();
				break;
			}
			Cell nc = field[pos.second][pos.first];
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
			i.setPosition(new Position(pos.first, pos.second, i.getPosition().rot));
			break;
		}
	}

	public static IntPair getForwardCoordinates(Position pos, WorldGenerator wg) {
		int x = pos.x;
		int y = pos.y;
		int rot = pos.rot;

		if (rot == 0) y--;
		if (rot == 1) x++;
		if (rot == 2) y++;
		if (rot == 3) x--;
		
		return wg.correctCoordinates(x, y);
	}

	public Cell[][] getField() {
		return field;
	}
	
	public WorldGenerator getWorldGenerator() {
		return worldGenerator;
	}
	
	// for debug
	public Individual getCurrentAntEater() {
		return antEatersContainer.getPack()[0];
	}
}