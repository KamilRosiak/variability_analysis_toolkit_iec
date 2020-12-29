package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Arrays;

import javax.inject.Inject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;

public class Type2Mutator implements Mutator {

	@Inject
	private NameChanger nameChanger;

	@Inject
	private EnumChanger enumChanger;

	@Inject
	private NumberChanger numberChanger;

	private static final int MAX_APPLIED_MUTATIONS = 1;

	@Override
	public void mutate(MutationContext ctx) {
		new RandomMutator(MAX_APPLIED_MUTATIONS, Arrays.asList(nameChanger, enumChanger, numberChanger)).mutate(ctx);

	}

}
