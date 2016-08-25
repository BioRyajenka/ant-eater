package ru.ifmo.ctddev.sushencev.anteater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import ru.ifmo.ctddev.sushencev.anteater.Util.Pair;

public class LogLoader implements AutoCloseable {
	private FileInputStream fis;
	private ObjectInputStream ois;

	public LogLoader(File file) throws IOException {
		fis = new FileInputStream(file);
		ois = new ObjectInputStream(fis);
	}

	public Object getSmth() {
		try {
			int byt = ois.readByte();
			switch (byt) {
			case Logger.GENERATION_BYTE:
				return getNextGeneration();
			case Logger.FIELD_BYTE:
				return getField();
			case Logger.STATISTICS_BYTE:
				return getStatistics();
			default:
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private EncodedGeneration getNextGeneration() {
		try {
			Map<String, Integer> description = (Map<String, Integer>) ois.readObject();

			Individual[] ants = (Individual[]) ois.readObject();
			Individual[] antEaters = (Individual[]) ois.readObject();
			return new EncodedGeneration(description, new Pair<>(ants, antEaters));
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private EncodedField getField() {
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
	
	private Statistics getStatistics() {
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
}