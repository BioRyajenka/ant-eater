package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Automata.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class Individual implements Serializable {
	private static final long serialVersionUID = -87288283039935538L;

	private transient Position position;
	private World habitat;
	private Sight sight;
	private Automata chromosome;
	
	private int ate = 0;
	private int distanceCovered = 0;
	
	private boolean dead = false;
	
	private int id;

	public void refresh() {
		ate = distanceCovered = 0;
		dead = false;
		refreshAutomata();
	}
	
	public void refreshAutomata() {
		chromosome.refresh();
	}
	
	public void setHabitat(World habitat) {
		this.habitat = habitat;
	}
	
	public Individual(World habitat, Sight sight, int maxStates, int id) {
		this(habitat, sight, new Automata(maxStates), id);
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "ant " + id;
	}
	
	public Individual(World habitat, Sight sight, Automata chromosome, int id) {
		this.habitat = habitat;
		this.sight = sight;
		this.chromosome = chromosome;
		this.id = id;
	}
	
	public InputSignal checkSight() {
		return sight.check(habitat.field, position);
	}
	
	public OutputSignal doStep() {
		if (dead) {
			throw new RuntimeException("dead stay dumb");
		}
		OutputSignal res = chromosome.doStep(checkSight());
		if (res == OutputSignal.FORWARD) {
			distanceCovered++;
		}
		return res;
	}
	
	public void incEatenFoodAmount() {
		ate++;
	}
	
	public float getFitness() {
		return 2 * ate + distanceCovered;
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
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public Pair<Individual, Individual> cross(Individual match) {
		Pair<Automata, Automata> res = chromosome.cross(match.chromosome);
		Individual first = new Individual(habitat, sight, res.first, -1);
		Individual second = new Individual(habitat, sight, res.second, -1);
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
		Individual res = new Individual(habitat, sight, chromosome.copy(), -1);
		return res;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}