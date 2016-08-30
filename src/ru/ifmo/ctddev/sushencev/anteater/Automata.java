package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class Automata implements Serializable {
	private static final long serialVersionUID = -9202081597965540637L;

	public static class InputSignal {
		private int mask;

		public InputSignal(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}
	}

	public enum OutputSignal {
		LEFT, RIGHT, FORWARD, NOTHING;
	}

	private transient int maxStates;
	public State[] data;
	private State curState;

	public Automata(int maxStates, int inputSignalsNumber) {
		this.maxStates = maxStates;
		final int states = Util.nextInt(maxStates) + 1;
		data = new State[states];
		for (int i = 0; i < states; i++) {
			data[i] = new State(inputSignalsNumber);
		}
		for (State s : data) {
			for (int i = 0; i < inputSignalsNumber; i++) {
				int next = Util.nextInt(states);
				s.nextState[i] = next;
				s.output[i] = Util.nextInt(OutputSignal.values().length);
			}
		}

		refresh();
	}

	private Automata(int maxStates, State[] data) {
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
		return Arrays.stream(Arrays.copyOfRange(data, from, to)).map(State::copy).collect(Collectors.toList());
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
		p1s1.addAll(p2s2);

		redirectLinks(p2s1, 0, 0, l2, l2 + r1);
		redirectLinks(p1s2, l2, l1, l1 + r1, l2 + r1);
		p2s1.addAll(p1s2);

		return new Pair<>(new Automata(maxStates, p1s1.toArray(new State[p1s1.size()])),
				new Automata(maxStates, p2s1.toArray(new State[p2s1.size()])));
	}

	/**
	 * Redirrects all the internal and external links in automata subgraph
	 */
	private void redirectLinks(List<State> list, int newFrom, int prevFrom, int prevTo,
			int newSize) {
		for (State s : list) {
			for (int i = 0; i < s.nextState.length; i++) {
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

	public void mutate(float probability) {
		for (State s : data) {
			if (!Util.dice(probability)) continue;
			for (int i = 0; i < s.nextState.length; i++) {
				s.nextState[i] = Util.nextInt(data.length);
				s.output[i] = Util.nextInt(OutputSignal.values().length);
			}
		}
	}

	public static class State implements Serializable {
		private static final long serialVersionUID = -8396697025935926778L;
		public int[] nextState;
		public int[] output;
		
		public State(int inputSignalsNumber) {
			nextState = new int[inputSignalsNumber];
			output = new int[inputSignalsNumber];
		}

		public State copy() {
			State res = new State(1);
			res.nextState = Arrays.copyOf(nextState, nextState.length);
			res.output = Arrays.copyOf(output, output.length);
			return res;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(nextState);
			result = prime * result + Arrays.hashCode(output);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			return Arrays.equals(nextState, other.nextState) && Arrays.equals(output, other.output);
		}
	}

	public int getCurStateNumber() {
		return getStateNumber(curState);
	}

	public int getStateNumber(State s) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == s) {
				return i;
			}
		}
		return -1;
	}

	public State[] getData() {
		return data;
	}

	public int getStatesNumber() {
		return data.length;
	}

	public Automata copy() {
		State[] newData = new State[data.length];
		for (int i = 0; i < data.length; i++) {
			newData[i] = data[i].copy();
		}
		return new Automata(maxStates, newData);
	}
}