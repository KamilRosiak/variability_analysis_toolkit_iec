package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;

public class Type2Mutator implements Mutator {

	private RandomMutator randomMutator;

	private static final int MAX_APPLIED_MUTATIONS = 1;

	@PostConstruct
	public void addMutations(NameChanger nameChanger, EnumChanger enumChanger, NumberChanger numberChanger) {
		randomMutator = new RandomMutator(MAX_APPLIED_MUTATIONS,
				Arrays.asList(nameChanger, enumChanger, numberChanger));
	}

	@Override
	public void mutate(MutationContext ctx) {
		randomMutator.mutate(ctx);
	}

	@Override
	public MutatorType getMutatorType() {
		return MutatorType.TYPE_II;
	}

}
