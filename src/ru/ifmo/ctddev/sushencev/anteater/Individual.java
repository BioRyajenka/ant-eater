package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Machine.OutputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;
import world.Sight;
import world.World;

public class Individual {
	private Position position = null;
	private World habitat;
	private Sight sight;
	private Machine chromosome;
	
	private int ate = 0;
	
	private boolean dead = false;

	public void refresh() {
		ate = 0;
		chromosome.refresh();
	}
	
	private String tag;
	
	public Individual(World habitat, Sight sight, int maxStates, String tag) {
		this(habitat, sight, new Machine(maxStates));
		this.tag = tag;
	}
	
	@Override
	public String toString() {
		return tag;
	}
	
	public Individual(World habitat, Sight sight, Machine chromosome) {
		this.habitat = habitat;
		this.sight = sight;
		this.chromosome = chromosome;
	}
	
	public OutputSignal doStep() {
		if (dead) {
			throw new RuntimeException("dead stay dumb");
		}
		return chromosome.doStep(sight.check(habitat, position));
	}
	
	public void incEatenFoodAmount() {
		ate++;
	}
	
	public int getEatenFoodAmount() {
		return ate;
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
	
	public void die() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public Pair<Individual, Individual> cross(Individual match) {
		Pair<Machine, Machine> res = chromosome.cross(match.chromosome);
		Individual first = new Individual(habitat, sight, res.first);
		Individual second = new Individual(habitat, sight, res.second);
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

	public void mutate() {
		chromosome.mutate();
	}
}