package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Arrays;
import java.util.List;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;

public class Type2Mutator extends RandomMutator {

	private static final int MAX_APPLIED_MUTATIONS = 1;
	
	private static final int MAX_AFFECTED_SYMBOLS = 3;
	
	private static final int GENERATED_DIGIT_LENGTH = 3;
	
	private static final List<Mutation> TYPE2_MUTATIONS = Arrays.asList(
			new NameChanger(MAX_AFFECTED_SYMBOLS),
			new EnumChanger(MAX_AFFECTED_SYMBOLS),
			new NumberChanger(MAX_AFFECTED_SYMBOLS, GENERATED_DIGIT_LENGTH)
			);
	
	public Type2Mutator() {
		super(MAX_APPLIED_MUTATIONS, TYPE2_MUTATIONS);
	}
}
