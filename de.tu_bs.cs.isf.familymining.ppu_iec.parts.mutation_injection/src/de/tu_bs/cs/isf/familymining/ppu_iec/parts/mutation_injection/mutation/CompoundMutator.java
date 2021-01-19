package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementInserter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementRemover;

public class CompoundMutator implements Mutator {

	private RandomMutator randomMutator;

	private static final int MAX_APPLIED_MUTATIONS = 3;

	@PostConstruct
	public void postConstruct(NameChanger nameChanger, EnumChanger enumChanger, NumberChanger numberChanger,
			StatementInserter stmtInserter, StatementRemover stmtRemover) {
		randomMutator = new RandomMutator(MAX_APPLIED_MUTATIONS,
				Arrays.asList(nameChanger, enumChanger, numberChanger, stmtInserter, stmtRemover));
	}

	@Override
	public void mutate(MutationContext ctx) {
		randomMutator.mutate(ctx);
	}

}
