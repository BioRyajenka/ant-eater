package ru.ifmo.ctddev.sushencev.anteater;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;
import ru.ifmo.ctddev.sushencev.anteater.worldgenerators.WorldGenerator;

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

	public WorldSettings getSettings() {
		try {
			int tries = ois.readInt();
			int aeps = ois.readInt();
			return new WorldSettings(tries, aeps);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public EncodedGeneration getNextGeneration() {
		try {
			int gen = ois.readInt();

			Individual[] ants = (Individual[]) ois.readObject();
			Individual[] antEaters = (Individual[]) ois.readObject();
			Util.log("loaded " + antEaters.length + " ant eaters");
			WorldGenerator worldGenerator = (WorldGenerator) ois.readObject();
			return new EncodedGeneration(gen, ants, antEaters, worldGenerator);
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public EncodedField getField() {
		try {
			// Map<String, Integer> description = (Map<String, Integer>)
			// ois.readObject();
			// Individual antEater = (Individual) ois.readObject();
			int height = ois.readInt();
			int width = ois.readInt();
			Cell[][] field = new Cell[height][width];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					field[i][j] = (Cell) ois.readObject();
				}
			}

			int antsRots[] = readIndividualsRots();
			int antEatersRots[] = readIndividualsRots();

			return new EncodedField(field, antsRots, antEatersRots);
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private int[] readIndividualsRots() throws IOException {
		int length = ois.readInt();
		int[] rots = new int[length];
		for (int i = 0; i < length; i++) {
			rots[i] = ois.readInt();
		}
		return rots;
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
		int aeps = 0;
		while (true) {
			try {
				int type = logLoader.getNextTokenType();

				switch (type) {
				case Logger.SETTINGS_BYTE:
					WorldSettings settings = logLoader.getSettings();
					tries = settings.tries;
					aeps = settings.aeps;
					break;
				case Logger.GENERATION_BYTE:
					Util.log("gen");
					EncodedGeneration p = logLoader.getNextGeneration();
					int gen = p.getGenerationNumber();
					Util.log("loading gen: " + gen);
					data.put(gen, new LGeneration(new WorldRepeater(p.getAnts(), p
							.getAntEaters(), p.getWorldGenerator())));

					Map<Integer, LAntEater> aes = data.get(gen).antEaters;
					for (int aei = 0; aei < aeps; aei++) {
						aes.putIfAbsent(aei, new LAntEater());
						for (int tri = 0; tri < tries; tri++) {
							logLoader.getNextTokenType();

							aes.get(aei).tries.put(tri, logLoader.getField());
						}
					}
					break;
				case Logger.STATISTICS_BYTE:
					Statistics antsStatistics = logLoader.getStatistics();
					logLoader.getNextTokenType();
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

	private static class WorldSettings {
		private int tries;
		private int aeps;

		public WorldSettings(int tries, int aeps) {
			this.tries = tries;
			this.aeps = aeps;
		}
	}
}