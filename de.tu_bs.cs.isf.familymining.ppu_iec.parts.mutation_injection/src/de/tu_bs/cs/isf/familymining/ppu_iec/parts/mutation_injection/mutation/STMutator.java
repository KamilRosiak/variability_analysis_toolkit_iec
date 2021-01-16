package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementInserter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementRemover;

public class STMutator implements Mutator {
	@Inject
	private NameChanger nameChanger;

	@Inject
	private EnumChanger enumChanger;

	@Inject
	private NumberChanger numberChanger;
	
	private RandomMutator randomMutator;
	
	private static final int MAX_APPLIED_MUTATIONS = 1;

	@PostConstruct
	public void postConstruct(StatementInserter stmtInserter, StatementRemover stmtRemover) { 
		randomMutator = new RandomMutator(MAX_APPLIED_MUTATIONS, Arrays.asList(stmtInserter, stmtRemover));
	}
	

	@Override
	public void mutate(MutationContext ctx) {
		randomMutator.mutate(ctx);
	}
}
