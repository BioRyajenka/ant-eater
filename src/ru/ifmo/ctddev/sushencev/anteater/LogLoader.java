package ru.ifmo.ctddev.sushencev.anteater;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import ru.ifmo.ctddev.sushencev.anteater.Util.IntPair;
import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class LogLoader implements AutoCloseable {
	private FileInputStream fis;
	private ObjectInputStream ois;

	public LogLoader(File file) throws IOException {
		fis = new FileInputStream(file);
		ois = new ObjectInputStream(fis);
	}

	public int getNextTokenType() {
		try {
			return ois.readByte();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IntPair getSettings() {
		try {
			int aeis = ois.readInt();
			int tries = ois.readInt();
			return new IntPair(aeis, tries);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public EncodedGeneration getNextGeneration() {
		try {
			Map<String, Integer> description = (Map<String, Integer>) ois.readObject();

			Individual[] ants = (Individual[]) ois.readObject();
			Individual[] antEaters = (Individual[]) ois.readObject();
			return new EncodedGeneration(description, new Pair<>(ants, antEaters));
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public EncodedField getField() {
		try {
			// Map<String, Integer> description = (Map<String, Integer>)
			// ois.readObject();
			Individual antEater = (Individual) ois.readObject();
			int height = ois.readInt();
			int width = ois.readInt();
			Cell[][] field = new Cell[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					field[i][j] = (Cell) ois.readObject();
				}
			}

			int antsNum = ois.readInt();
			int[] antsRots = new int[antsNum];
			for (int i = 0; i < antsNum; i++) {
				antsRots[i] = ois.readInt();
			}
			int antEaterRot = ois.readInt();
			return new EncodedField(field, antsRots, antEaterRot, antEater);
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Statistics getStatistics() {
		try {
			return (Statistics) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		ois.close();
		fis.close();
	}

	public static class LGeneration {
		public WorldRepeater world;
		public Map<Integer, LAntEater> antEaters = new HashMap<>();

		public LGeneration(WorldRepeater world) {
			this.world = world;
		}
	}

	public static class LAntEater {
		public Map<Integer, EncodedField> tries = new HashMap<>();
	}

	@SuppressWarnings("resource")
	public static Pair<Map<Integer, LGeneration>, Pair<Statistics, Statistics>> loadFile(
			File file) {
		LogLoader logLoader;
		try {
			logLoader = new LogLoader(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Map<Integer, LGeneration> data = new HashMap<>();

		Pair<Statistics, Statistics> stRes = null;

		int tries = 0;
		int aeis = 0;
		while (true) {
			try {
				int type = logLoader.getNextTokenType();

				switch (type) {
				case Logger.SETTINGS_BYTE:
					Util.IntPair settings = logLoader.getSettings();
					aeis = settings.first;
					tries = settings.second;
					break;
				case Logger.GENERATION_BYTE:
					EncodedGeneration p = logLoader.getNextGeneration();
					int gen = p.first.get("gen");
					Util.log("loading gen: " + gen);
					data.put(gen, new LGeneration(new WorldRepeater(p.second.first,
							p.second.second)));

					Map<Integer, LAntEater> aes = data.get(gen).antEaters;
					for (int aei = 0; aei < aeis; aei++) {
						aes.putIfAbsent(aei, new LAntEater());
						for (int tri = 0; tri < tries; tri++) {
							logLoader.getNextTokenType();
							//assert logLoader.getNextTokenType() == Logger.FIELD_BYTE;
							
							aes.get(aei).tries.put(tri, logLoader.getField());
						}
					}
					break;
				case Logger.STATISTICS_BYTE:
					Statistics antsStatistics = logLoader.getStatistics();
					logLoader.getNextTokenType();
					//assert logLoader.getNextTokenType() == Logger.STATISTICS_BYTE;
					Statistics antEatersStatistics = logLoader.getStatistics();
					stRes = new Pair<>(antsStatistics, antEatersStatistics);
					break;
				}
			} catch (RuntimeException e) {
				if (!(e.getCause() instanceof EOFException)) {
					throw e;
				}
				break;
			}
		}
		try {
			logLoader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return new Pair<>(data, stRes);
	}
}