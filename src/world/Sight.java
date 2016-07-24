package world;

import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;
import ru.ifmo.ctddev.sushencev.anteater.Machine.InputSignal;

public interface Sight {
	public InputSignal check(World world, Position position);
}