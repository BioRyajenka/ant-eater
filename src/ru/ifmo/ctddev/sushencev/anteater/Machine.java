package ru.ifmo.ctddev.sushencev.anteater;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class Machine {
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

	private int maxStates;

	public Machine(int maxStates) {
		this.maxStates = maxStates;
		final int states = Util.nextInt(maxStates) + 1;
		data = new ArrayList<>();
		for (int i = 0; i < states; i++) {
			data.add(new State());
		}
		for (State s : data) {
			for (int i = 0; i < InputSignal.SIGNALS_NUMBER; i++) {
				int next = Util.nextInt(states);
				OutputSignal out = OutputSignal.values()[Util.nextInt(
						OutputSignal.values().length)];
				s.links.add(new Pair<>(next, out));
			}
		}
		
		refresh();
	}

	public Machine(int maxStates, List<State> data) {
		this.maxStates = maxStates;
		this.data = data;
		
		refresh();
	}

	public OutputSignal doStep(InputSignal inputSignal) {
		Pair<Integer, OutputSignal> p = curState.links.get(inputSignal.mask);
		curState = data.get(p.first);
		return p.second;
	}

	public void refresh() {
		curState = data.get(0);
	}

	public Pair<Machine, Machine> cross(Machine rhs) {
		// crossover points 1 and 2
		int cp1, cp2;

		// number of nodes on left and on right from cp
		int l1, r1, l2, r2;

		do {
			cp1 = Util.nextInt(this.data.size());
			cp2 = Util.nextInt(rhs.data.size());

			l1 = cp1;
			r1 = this.data.size() - l1;
			l2 = cp2;
			r2 = rhs.data.size() - l2;
		} while (l1 + r2 > maxStates || l2 + r1 > maxStates);

		List<State> p1s1 = this.data.subList(0, l1);
		List<State> p1s2 = this.data.subList(l1, l1 + r1);
		List<State> p2s1 = rhs.data.subList(0, l2);
		List<State> p2s2 = rhs.data.subList(l2, l2 + r2);

		redirectLinks(p1s1, 0, 0, l1, l1 + r2);
		redirectLinks(p2s2, l1, l2, l2 + r2, l1 + r2);
		List<State> newC1 = new ArrayList<>(p1s1);
		newC1.addAll(new ArrayList<>(p2s2));

		redirectLinks(p2s1, 0, 0, l2, l2 + r1);
		redirectLinks(p1s2, l2, l1, l1 + r1, l2 + r1);
		List<State> newC2 = new ArrayList<>(p2s1);
		newC2.addAll(new ArrayList<>(p1s2));

		return new Pair<>(new Machine(maxStates, newC1), new Machine(maxStates, newC2));
	}

	/**
	 * Redirrects all the internal and external links in automata subgraph
	 */
	private void redirectLinks(List<State> list, int newFrom, int prevFrom, int prevTo,
			int newSize) {
		for (State s : list) {
			for (Pair<Integer, OutputSignal> p : s.links) {
				if (p.first >= prevFrom && p.first < prevTo) {
					// internal link
					p.first -= prevFrom - newFrom;
				} else {
					// external link
					p.first = Util.nextInt(newSize);
				}
			}
		}
	}

	public void mutate() {
		Pair<Integer, OutputSignal> p = data.get(Util.nextInt(data.size())).links.get(
				Util.nextInt(InputSignal.SIGNALS_NUMBER));
		if (Util.dice(.5f)) {
			p.first = Util.nextInt(data.size());
		} else {
			p.second = OutputSignal.values()[Util.nextInt(OutputSignal.values().length)];
		}
	}

	private List<State> data;

	private State curState;

	private static class State {
		List<Pair<Integer, OutputSignal>> links = new ArrayList<>();
	}
}