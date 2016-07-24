package ru.ifmo.ctddev.sushencev.anteater;

import java.util.Random;

public class Util {
	private Util() {}
	
	public static Random rand = new Random();
	
	public static int nextInt(int modulo) {
		return rand.nextInt(modulo);
	}
	
	public static float dice() {
		return rand.nextFloat();
	}
	
	public static boolean dice(float prob) {
		return dice() < prob;
	}
	
	public static int mdist(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}
	
	public static class IntPair extends Pair<Integer, Integer> {
		public IntPair(Integer first, Integer second) {
			super(first, second);
		}
	}
	
	public static class Pair<F, S> {
		public F first;
		public S second;
		
		public Pair(F first, S second) {
			this.first = first;
			this.second = second;
		}
	}
}
