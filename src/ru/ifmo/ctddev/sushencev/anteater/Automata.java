package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class Automata implements Serializable {
	private static final long serialVersionUID = -9202081597965540637L;

	public static class InputSignal {
		private static final int SIGNALS_NUMBER = 256;

		private int mask;

		public InputSignal(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}

		@Override
		public int hashCode() {
			return mask;
		}
	}

	public enum OutputSignal {
		LEFT, RIGHT, FORWARD;
	}

	private transient int maxStates;
	private State[] data;
	private State curState;

	public Automata(int maxStates) {
		this.maxStates = maxStates;
		final int states = Util.nextInt(maxStates) + 1;
		data = new State[states];
		for (int i = 0; i < states; i++) {
			data[i] = new State();
		}
		for (State s : data) {
			for (int i = 0; i < InputSignal.SIGNALS_NUMBER; i++) {
				int next = Util.nextInt(states);
				s.nextState[i] = next;
				s.output[i] = Util.nextInt(OutputSignal.values().length);
			}
		}

		refresh();
	}

	public Automata(int maxStates, State[] data) {
		this.maxStates = maxStates;
		this.data = data;

		refresh();
	}

	public OutputSignal doStep(InputSignal inputSignal) {
		int nextState = curState.nextState[inputSignal.mask];
		int output = curState.output[inputSignal.mask];
		curState = data[nextState];
		return OutputSignal.values()[output];
	}

	public void refresh() {
		curState = data[0];
	}

	private List<State> copySubArrayToList(State[] data, int from, int to) {
		return Arrays.stream(Arrays.copyOfRange(data, from, to)).map(s -> s.copy())
				.collect(Collectors.toList());
	}

	public Pair<Automata, Automata> cross(Automata rhs) {
		// crossover points 1 and 2
		int cp1, cp2;

		// number of nodes on left and on right from cp
		int l1, r1, l2, r2;
		do {
			cp1 = Util.nextInt(this.data.length);
			cp2 = Util.nextInt(rhs.data.length);

			l1 = cp1;
			r1 = this.data.length - l1;
			l2 = cp2;
			r2 = rhs.data.length - l2;
		} while (l1 + r2 > maxStates || l2 + r1 > maxStates);

		List<State> p1s1 = copySubArrayToList(this.data, 0, l1);
		List<State> p1s2 = copySubArrayToList(this.data, l1, l1 + r1);
		List<State> p2s1 = copySubArrayToList(rhs.data, 0, l2);
		List<State> p2s2 = copySubArrayToList(rhs.data, l2, l2 + r2);

		redirectLinks(p1s1, 0, 0, l1, l1 + r2);
		redirectLinks(p2s2, l1, l2, l2 + r2, l1 + r2);
		p1s1.addAll(new ArrayList<>(p2s2));

		redirectLinks(p2s1, 0, 0, l2, l2 + r1);
		redirectLinks(p1s2, l2, l1, l1 + r1, l2 + r1);
		p2s1.addAll(new ArrayList<>(p1s2));

		return new Pair<>(new Automata(maxStates, p1s1.toArray(new State[p1s1.size()])), 
						new Automata(maxStates, p2s1.toArray(new State[p2s1.size()])));
	}

	/**
	 * Redirrects all the internal and external links in automata subgraph
	 */
	private void redirectLinks(List<State> list, int newFrom, int prevFrom, int prevTo,
			int newSize) {
		for (State s : list) {
			for (int i = 0; i < InputSignal.SIGNALS_NUMBER; i++) {
				int nextState = s.nextState[i];
				if (nextState >= prevFrom && nextState < prevTo) {
					// internal link
					nextState -= prevFrom - newFrom;
				} else {
					// external link
					nextState = Util.nextInt(newSize);
				}
				s.nextState[i] = nextState;
			}
		}
	}

	public void mutate() {
		State s = data[Util.nextInt(data.length)];
		int gen = Util.nextInt(InputSignal.SIGNALS_NUMBER);
		if (Util.dice(.5f)) {
			s.nextState[gen] = Util.nextInt(data.length);
		} else {
			s.output[gen] = Util.nextInt(OutputSignal.values().length);
		}
	}

	private static class State implements Serializable {
		private static final long serialVersionUID = -8396697025935926778L;
		int[] nextState = new int[InputSignal.SIGNALS_NUMBER];
		int[] output = new int[InputSignal.SIGNALS_NUMBER];

		public State copy() {
			State res = new State();
			res.nextState = Arrays.copyOf(nextState, nextState.length);
			res.output = Arrays.copyOf(output, output.length);
			return res;
		}
	}

	public int getCurStateNumber() {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == curState) {
				return i;
			}
		}
		return -1;
	}

	public int getStatesNumber() {
		return data.length;
	}
}