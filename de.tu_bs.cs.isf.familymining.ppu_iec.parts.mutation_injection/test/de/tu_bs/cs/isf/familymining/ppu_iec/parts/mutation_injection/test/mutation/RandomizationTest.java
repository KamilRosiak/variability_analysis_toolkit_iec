package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;

public class RandomizationTest {

	@Test
	public void testPickFromIterable() throws Exception {
		Randomization randomly = new Randomization();
		List<String> strings = Arrays.asList("var1", "var2", "var3", "var4");

		Set<String> selected = new TreeSet<>();
		for (int i = 0; i < 12; i++) {
			String s = randomly.pickFrom(strings);
			selected.add(s);
			assertThat(s).isNotNull().isNotEmpty();
		}
		assertThat(selected).as("has picked the same element 12 times!").hasSizeGreaterThanOrEqualTo(2);
	}
}
