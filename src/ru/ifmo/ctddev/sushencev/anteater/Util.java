package ru.ifmo.ctddev.sushencev.anteater;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Util {
	private Util() {
	}

	private static final Random rand = new Random();

	public static final long randomSeed;

	private static boolean quitPressed;

	public static boolean isQuitPressed() {
		return quitPressed;
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

	private static void startDaemonThread(Runnable runnable) {
		Thread th = new Thread(runnable);
		th.setDaemon(true);
		th.start();
	}

	static {
		randomSeed = rand.nextLong();
		//randomSeed = -6760928705423734968l;
		rand.setSeed(randomSeed);
		log("seed is " + randomSeed);

		startDaemonThread(() -> {
			while (true) {
				try {
					int byt = System.in.read();
					if (byt == 113) {// q
						log("q pressed. quitting");
						quitPressed = true;
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static int nextInt(int modulo) {
		return rand.nextInt(modulo);
	}
	
	public static float nextNormalDistribution(float mean, float deviation) {
		return (float) (rand.nextGaussian() * deviation + mean);
	}

	public static float normalDistribution(float mean, float deviation, float x) {
		float variance = deviation * deviation;
		return (float) (1 / Math.sqrt(2 * variance * Math.PI) * Math.exp(-(x - mean) * (x
				- mean) / (2 * variance)));
	}

	public static <T> T randomElement(T[] arr) {
		return arr[nextInt(arr.length)];
	}

	public static <T> T[] randomSubVector(T[] arr, int newSize, T[] res) {
		List<T> temp = new ArrayList<T>(Arrays.asList(arr));
		Collections.shuffle(temp);
		return temp.subList(0, newSize).toArray(res);
	}

	public static float dice() {
		return rand.nextFloat();
	}

	public static boolean dice(float prob) {
		return dice() < prob;
	}

	public static int[] generateRandomVector(int length, int maxValue) {
		int[] res = new int[length];
		for (int i = 0; i < res.length; i++) {
			res[i] = nextInt(maxValue);
		}
		return res;
	}
	
	public static float dist(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static int manhattanDist(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}
	
	public static boolean inBounds(int x, int l, int r) {
		return x >= l && x < r;
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

	private static String getCallerClassName() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement ste : stElements) {
			if (!ste.getClassName().equals(Util.class.getName()) && ste.getClassName().indexOf(
					"java.lang.Thread") != 0) {
				return ste.getFileName();
			}
		}
		return null;
	}

	private static int getCallerLineNumber() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement ste : stElements) {
			if (!ste.getClassName().equals(Util.class.getName()) && ste.getClassName().indexOf(
					"java.lang.Thread") != 0) {
				return ste.getLineNumber();
			}
		}
		return 0;
	}

	private static String getCallerInfo() {
		return String.format("(%s:%s)", getCallerClassName(), getCallerLineNumber());
	}

	public static void log(Object msg) {
		System.out.printf("%s: %s\n", getCallerInfo(), msg.toString());
	}

	public interface SerializablePredicate<T> extends Serializable {
		public boolean test(T t);
	}
}
