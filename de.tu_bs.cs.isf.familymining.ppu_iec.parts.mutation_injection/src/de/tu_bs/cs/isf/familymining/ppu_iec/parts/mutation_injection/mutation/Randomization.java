package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Iterator;
import java.util.Random;

public class Randomization {

	private final Random rand;
	
	public Randomization() {
		rand = new Random(System.currentTimeMillis());		
	}
	
	/**
	 * Randomly selects one element out of the given <i>elements</i>. 
	 * The probability is uniformly distributed.
	 * 
	 * @param <T>
	 * @param elements
	 * @return selected element
	 */
	@SuppressWarnings("unchecked")
	public <T> T pickFrom(T... elements) {
		int index = rand.nextInt(elements.length);
		return elements[index];
	}
	
	/**
	 * Randomly selects one element out of the given <i>elements</i>. 
	 * The probability is uniformly distributed.
	 * 
	 * @param <T>
	 * @param elements
	 * @return selected element
	 */
	public <T> T pickFrom(Iterable<T> elements) {
		int size = (int) elements.spliterator().getExactSizeIfKnown();
		if (size <= 0) {
			throw new IllegalArgumentException("The number of elements is either zero or undefined");
		}
		
		int index = rand.nextInt(size);
		Iterator<T> it = elements.iterator();
		for (int i = 0; i < index; i++) {
			it.next();
		}
		return it.next();
	}
	
}
