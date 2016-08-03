package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Map;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class EncodedGeneration extends Pair<Map<String, Integer>, Pair<Individual[], Individual[]>> {

	public EncodedGeneration(Map<String, Integer> first, Pair<Individual[], Individual[]> second) {
		super(first, second);
	}

}
