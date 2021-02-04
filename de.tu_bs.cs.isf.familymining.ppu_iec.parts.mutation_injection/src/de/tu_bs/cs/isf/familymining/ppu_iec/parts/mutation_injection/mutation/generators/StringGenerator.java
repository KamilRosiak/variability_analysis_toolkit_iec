package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
/**
 * Helper class for the generation of random Strings.
 * @author Kamil Rosiak
 *
 */
public class StringGenerator {
	
	/**
	 * return a random string of the given length
	 * @param length
	 * @return
	 */
	public static String getRandomStringOfLenght(int length) {
		Random r = new Random();
		return RandomStringUtils.randomAlphabetic(r.nextInt(length));
	}
}
