package ru.ifmo.ctddev.sushencev.anteater;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class Logger implements AutoCloseable {
	private FileOutputStream fos;
	private ObjectOutputStream oos;

	public Logger(String logFileName) throws IOException {
		fos = new FileOutputStream(logFileName);
		oos = new ObjectOutputStream(fos);
	}

	public void putNextGeneration(Map<String, Integer> description, Individual[] ants,
			Individual[] antEaters) {
		try {
			oos.reset();
			oos.writeObject(description);
			oos.writeObject(ants);
			oos.writeObject(antEaters);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void putField(Cell[][] field, Individual[] ants,
			Individual antEater) {
		try {
			// gen, ae, try
			//oos.writeObject(description);

			oos.writeObject(antEater);
			oos.writeInt(field.length);
			oos.writeInt(field[0].length);
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					oos.writeUnshared(field[i][j]);
				}
			}

			oos.writeInt(ants.length);
			for (Individual a : ants) {
				oos.writeInt(a.getPosition().rot);
			}
			oos.writeInt(antEater.getPosition().rot);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		oos.close();
		fos.close();
	}
}
