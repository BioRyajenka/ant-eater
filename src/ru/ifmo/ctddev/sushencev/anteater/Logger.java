package ru.ifmo.ctddev.sushencev.anteater;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Logger implements AutoCloseable {
	private Map<String, Integer> presets = new HashMap<>();
	private Map<String, Integer> description = new HashMap<>();
	
	private FileOutputStream fos;
	private ObjectOutputStream oos;

	public Logger(String logFileName) throws IOException {
		fos = new FileOutputStream(logFileName);
		oos = new ObjectOutputStream(fos);
	}
	
	public void updatePresets(String key, Integer value) {
		presets.put(key, value);
	}

	public void updateDescription(String key, Integer value) {
		description.put(key, value);
	}

	private boolean presetsWritten = false;
	
	public void saveWorldSnapshot(World world) throws IOException {
		if (!presetsWritten) {
			oos.writeObject(presets);
			presetsWritten = true;
		}
		oos.writeObject(description);
		oos.writeObject(world);
		oos.reset();
	}

	@Override
	public void close() throws IOException {
		oos.close();
		fos.close();
	}
}
