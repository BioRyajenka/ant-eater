package ru.ifmo.ctddev.sushencev.anteater;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class Logger implements AutoCloseable {
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	
	public final static byte GENERATION_BYTE = 0;
	public final static byte FIELD_BYTE = 1;
	public final static byte STATISTICS_BYTE = 2;
	public final static byte SETTINGS_BYTE = 3;

	public Logger(String logFileName) throws IOException {
		fos = new FileOutputStream(logFileName);
		oos = new ObjectOutputStream(fos);
	}
	
	public void putWorldSettings(int aeps, int tries) {
		try {
			oos.writeByte(SETTINGS_BYTE);
			
			oos.writeInt(aeps);
			oos.writeInt(tries);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void putNextGeneration(Map<String, Integer> description, Individual[] ants,
			Individual[] antEaters) {
		try {
			oos.reset();
			
			renameAnts(ants, "ant");
			renameAnts(antEaters, "antEater");
			
			oos.writeByte(GENERATION_BYTE);
			
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
			
			//renameAnts(ants);
			
			oos.writeByte(FIELD_BYTE);

			oos.writeObject(antEater);
			oos.writeInt(field.length);
			oos.writeInt(field[0].length);
			for (Cell[] row : field) {
				for (Cell cell : row) {
					oos.writeUnshared(cell);
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
	
	private void renameAnts(Individual[] ants, String prefix) {
		for (int i = 0; i < ants.length; i++) {
			ants[i].setName(prefix + " " + i);
		}
	}

	public void putStatistics(Statistics antsStatistics) {
		try {
			oos.writeByte(STATISTICS_BYTE);
			
			oos.writeObject(antsStatistics);
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
