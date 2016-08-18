package ru.ifmo.ctddev.sushencev.anteater;

import ru.ifmo.ctddev.sushencev.anteater.Automata.InputSignal;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Util.SerializablePredicate;

public class SimpleSightWithObstacles extends SimpleSight {
	private static final long serialVersionUID = -3706879132121519471L;

	public SimpleSightWithObstacles(SerializablePredicate<Cell> isFoodFunction, int range) {
		super(isFoodFunction, range);
	}
	
    @Override
    public InputSignal check(Cell[][] field, Position position) {
    	int mask = super.check(field, position).getMask();
    	Position fp = World.getForwardPosition(position, field[0].length, field.length);
		if (field[fp.y][fp.x].hasIndividual()) {
			mask += super.getInputSignalsNumber();
		}
		return new InputSignal(mask);
    }
    
    @Override
	public int getInputSignalsNumber() {
		return super.getInputSignalsNumber() * 2;
	}
}
