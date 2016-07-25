package world;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ru.ifmo.ctddev.sushencev.anteater.Cell;
import ru.ifmo.ctddev.sushencev.anteater.Individual;
import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;

public class Logger implements AutoCloseable {
	private Map<String, String> description = new HashMap<>();

	private FileWriter fw;
	private BufferedWriter bw;
	private PrintWriter out;

	public Logger(String logFileName) throws IOException {
		fw = new FileWriter(logFileName, true);
		bw = new BufferedWriter(fw);
		out = new PrintWriter(bw);

		out.println("{\n\t\"frames\": [");
	}

	public void updateDescription(String key, String value) {
		description.put(key, value);
	}

	private String descriptionToString() {
		return description.entrySet().stream().collect(() -> "", (acc, e) -> acc += e
				.getKey() + ": " + e.getValue() + "\n", (a, b) -> a += b);
	}

	private String cellToString(Cell c, Individual antEater) {
		if (c.hasIndividual()) {
			if (c.getIndividual() == antEater) {
				switch (c.getIndividual().getPosition().rot) {
				case 0:
					return "\u2191";
				case 1:
					return "\u2192";
				case 2:
					return "\u2193";
				case 3:
					return "\u2190";
				}
			} else {
				switch (c.getIndividual().getPosition().rot) {
				case 0:
					return "^";
				case 1:
					return ">";
				case 2:
					return "v";
				case 3:
					return "<";
				}
			}
		}
		if (c.getType() == Type.FOOD) {
			return "*";
		} else {
			return ".";
		}
	}

	private String[] worldToString(World w) {
		String[] res = new String[w.field.length];
		int i = 0;
		for (Cell[] cells : w.field) {
			res[i++] = Arrays.stream(cells).collect(() -> "", (acc, cell) -> {
				acc += cellToString(cell, w.antEater);
			} , (a, b) -> a += b);
		}
		return res;
	}

	private boolean firstSnapshot = true;

	public void saveWorldSnapshot(World world) {
		if (!firstSnapshot) {
			out.println(",");
		}
		firstSnapshot = false;

		out.print("\t\t{\n\t\t\t\"descrition\": ");
		out.print(descriptionToString());
		out.println(",\n\t\t\t\"snapshot\": [");
		String[] lines = worldToString(world);
		for (int i = 0; i < lines.length; i++) {
			out.print("\t\t\t\t");
			out.print(lines[i]);
			if (i != lines.length - 1) {
				out.println(",");
			}
		}
		out.print("\n\t\t\t]\n\t\t}");
	}

	@Override
	public void close() throws IOException {
		out.println("\t]\n}");

		fw.close();
		bw.close();
		out.close();
	}
}
