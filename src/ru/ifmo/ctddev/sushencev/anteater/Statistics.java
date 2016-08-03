package ru.ifmo.ctddev.sushencev.anteater;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics implements Serializable {
	private static final long serialVersionUID = -1059100044533188992L;

	private Map<Integer, Integer> data = new HashMap<>();
	private String description;
	
	public Statistics(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setPlot(int x, int y) {
		data.put(x, y);
	}
	
	public int get(int x) {
		return data.get(x);
	}
	
	public List<Integer> getAbscisses() {
		List<Integer> res = data.keySet().stream().collect(Collectors.toList());
		res.sort(Integer::compareTo);
		return res;
	}
}
