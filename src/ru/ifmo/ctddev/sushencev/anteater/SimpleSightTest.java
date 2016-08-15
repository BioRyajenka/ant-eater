package ru.ifmo.ctddev.sushencev.anteater;

import org.junit.Before;
import org.junit.Test;

import ru.ifmo.ctddev.sushencev.anteater.Cell.Type;
import ru.ifmo.ctddev.sushencev.anteater.Individual.Position;

import static org.junit.Assert.*;

public class SimpleSightTest {
	private static Cell[][] buildField(String[] s) {
		Cell[][] res = new Cell[5][];
		for (int i = 0; i < 5; i++) {
			res[i] = new Cell[5];
			for (int j = 0; j < 5; j++) {
				res[i][j] = new Cell(s[i].charAt(j) == '#' ? Type.FOOD : Type.NOTHING);
			}
		}
		return res;
	}

	private SimpleSight sight;

	@Before
	public void setUp() {
		sight = new SimpleSight(c -> c.getType() == Type.FOOD, 2);
	}

	public void test(String[] test, int rot, int expected) {
		assertEquals(sight.check(buildField(test), new Position(2, 2, rot)).getMask(),
				expected);
	}

	@Test
	
	public void up_filled1() {
		test(new String[] { 
				"#####", 
				"#####", 
				"##@##",
				"#####",
				"#####"}, 0, 255);
	}

	@Test
	public void up_filled2() {
		test(new String[] { 
				"..#..", 
				".###.", 
				"##@##",
				".....",
				"....."}, 0, 255);
	}
	
	@Test
	public void up_clear() {
		test(new String[] { 
				".....", 
				".....", 
				"..@..",
				".....",
				"....."}, 0, 0);
	}
	
	@Test
	public void up_random() {
		test(new String[] { 
				"..#..", 
				".#.#.", 
				"#.@#.",
				".....",
				"....."}, 0, 0b01011011);
	}
	
	@Test
	public void left_random() {
		test(new String[] { 
				"..#..", 
				".#...", 
				"##@..",
				".....",
				"..#.."}, 3, 0b10011101);
	}
}
