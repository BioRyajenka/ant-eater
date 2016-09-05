package ru.ifmo.ctddev.sushencev.anteater;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
	
	public void putWorldSettings(int tries, int aeps) {
		try {
			oos.writeByte(SETTINGS_BYTE);
			
			oos.writeInt(tries);
			oos.writeInt(aeps);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void putNextGeneration(int gen, Individual[] ants, Individual[] antEaters) {
		try {
			oos.reset();
			
			renameAnts(ants, "ant");
			renameAnts(antEaters, "antEater");
			
			oos.writeByte(GENERATION_BYTE);
			
			oos.writeInt(gen);
			
			oos.writeObject(ants);
			oos.writeObject(antEaters);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void putField(Cell[][] field, Individual[] ants, Individual[] antEaters) {
		try {
			oos.writeByte(FIELD_BYTE);

			oos.writeInt(field.length);
			oos.writeInt(field[0].length);
			for (Cell[] row : field) {
				for (Cell cell : row) {
					oos.writeUnshared(cell);
				}
			}
			
			writeIndividualsRots(ants);
			writeIndividualsRots(antEaters);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void writeIndividualsRots(Individual[] ants) throws IOException {
		oos.writeInt(ants.length);
		for (Individual a : ants) {
			if (a.getName() == null) {
				Util.log("wtf");
			}
			oos.writeInt(a.getPosition().rot);
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
