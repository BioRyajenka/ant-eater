package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.Set;

public class Util {
	private Util() {
	}

	private static final Random rand = new Random();

	public static final long randomSeed;

	private static boolean smthPressed;

	public static boolean isSmthPressed() {
		return smthPressed;
	}

	@SuppressWarnings("unused")
	private static boolean isMainThreadFinished() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet) {
			if (t.getId() == 1) {
				return true;
			}
		}
		return false;
	}
	
	static {
		randomSeed = rand.nextLong();
		// randomSeed = -8904520990100767751l;
		rand.setSeed(randomSeed);
		log("seed is " + randomSeed);

		(new Thread(() -> {
			while (true) {
				try {
					int byt = System.in.read();
					if (byt == 113) {//q
						log("q pressed. quitting");
						smthPressed = true;
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		})).start();
	}

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

	public static void log(String msg) {
		System.out.println(msg);
	}

	public interface SerializablePredicate<T> extends Serializable {
		public boolean test(T t);
	}
}
