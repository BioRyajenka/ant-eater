package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class Individual implements Serializable {
	private static final long serialVersionUID = -87288283039935538L;

	private transient Position position;
	private Sight sight;
	private Automata chromosome;
	
	private int ate = 0;
	private String name;
	private boolean antEater;
	private boolean dead = false;
	
	public void refresh() {
		ate = 0;
		dead = false;
		refreshAutomata();
	}
	
	public void refreshAutomata() {
		chromosome.refresh();
	}
	
	public Individual(Sight sight, int maxStates, String name, boolean antEater) {
		this(sight, new Automata(maxStates, sight.getInputSignalsNumber()), name, antEater);
	}
	
	public Individual(Sight sight, Automata chromosome, String name, boolean antEater) {
		this.sight = sight;
		this.chromosome = chromosome;
		this.name = name;
		this.antEater = antEater;
	}

	public InputSignal checkSight(Cell[][] field, WorldGenerator wg) {
		return sight.check(field, position, wg);
	}
	
	public OutputSignal checkStep(Cell[][] field, WorldGenerator wg) {
		if (dead) {
			return null;
		}
		return chromosome.checkStep(checkSight(field, wg));
	}

	public OutputSignal doStep(Cell[][] field, WorldGenerator wg) {
		if (dead) {
			throw new RuntimeException("dead stay dumb");
		}
		return chromosome.doStep(checkSight(field, wg));
	}

	public void incEatenFoodAmount() {
		ate++;
	}

	public float getFitness() {
		return ate;//2 * ate + distanceCovered;
	}

	public void setPosition(int x, int y, int rot) {
		setPosition(new Position(x, y, rot));
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public Automata getChromosome() {
		return chromosome;
	}
	
	public void die() {
		if (isAntEater()) {
			//Util.log
		}
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public Pair<Individual, Individual> cross(Individual match) {
		Pair<Automata, Automata> res = chromosome.cross(match.chromosome);
		Individual first = new Individual(sight, res.first, null, antEater);
		Individual second = new Individual(sight, res.second, null, antEater);
		return new Pair<Individual, Individual>(first, second);
	}
	
	public static class Position {
		public int x, y;
		/**
		 * 0 up
		 * 1 right
		 * 2 down
		 * 3 left
		 */
		public int rot;
		
		public Position(int x, int y, int rot) {
			this.x = x;
			this.y = y;
			this.rot = rot;
		}
	}

	public void mutate(float probability) {
		chromosome.mutate(probability);
	}
	
	public Individual copy() {
		return new Individual(sight, chromosome.copy(), null, antEater);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAntEater() {
		return antEater;
	}

	@Override
	public String toString() {
		return name;
	}
}