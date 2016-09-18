package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;
import ru.ifmo.ctddev.sushencev.anteater.Util.SerializablePredicate;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

public class SightWithObstaclesAndAllyDistinction extends SimpleSight {
	private static final long serialVersionUID = -3706879132121519471L;
	
	private SerializablePredicate<Cell> isObstacleFunction;

	public SightWithObstaclesAndAllyDistinction(SerializablePredicate<Cell> isFoodFunction, int range, SerializablePredicate<Cell> isObstacleFunction) {
		super(isFoodFunction, range);
		this.isObstacleFunction = isObstacleFunction;
	}
	
    @Override
    public InputSignal check(Cell[][] field, Position position, WorldGenerator wg) {
    	int mask = super.check(field, position, wg).getMask();
    	IntPair fp = World.getForwardCoordinates(position, wg);
		if (fp == null || isObstacleFunction.test(field[fp.second][fp.first])) {
			mask += super.getInputSignalsNumber();
		} else {
			if (field[fp.second][fp.first].hasIndividual()) {//ally 
				mask += 2 * super.getInputSignalsNumber();
			}
		}
		return new InputSignal(mask);
    }
    
    @Override
	public int getInputSignalsNumber() {
		return super.getInputSignalsNumber() * 3;
	}
}
