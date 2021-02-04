package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.POUActionInserter;

public class Type3Mutator implements Mutator {

	private RandomMutator randomMutator;

	private static final int MAX_APPLIED_MUTATIONS = 1;
/**
	@PostConstruct
	public void addMutations(StatementInserter stmtInserter, StatementRemover stmtRemover) {
		randomMutator = new RandomMutator(MAX_APPLIED_MUTATIONS, Arrays.asList(stmtInserter, stmtRemover));
	}
	**/
	@PostConstruct
	public void addMutations(POUActionInserter pouInsert) {
		randomMutator = new RandomMutator(MAX_APPLIED_MUTATIONS, Arrays.asList(pouInsert));
	}

	public void mutate(MutationContext ctx) {
		randomMutator.mutate(ctx);
	}

	@Override
	public MutatorType getMutatorType() {
		return MutatorType.TYPE_III;
	}
}
