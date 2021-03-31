package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutatorParameters.MTR_MAX_MUTATIONS;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation.MUTATION_PREF;

import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementInserter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementRemover;

/**
 * Applies all mutations selected at random.
 * 
 * @author Oliver Urbaniak
 *
 */
public class CompoundMutator implements Mutator {

	private RandomMutator randomMutator;

	@Inject
	public void setMutatorSettings(@Preference(nodePath = MUTATION_PREF, value = MTR_MAX_MUTATIONS) int maxMutations,
			NameChanger nameChanger, EnumChanger enumChanger, NumberChanger numberChanger,
			StatementInserter stmtInserter, StatementRemover stmtRemover) {
		randomMutator = new RandomMutator(maxMutations,
				Arrays.asList(nameChanger, enumChanger, numberChanger, stmtInserter, stmtRemover));
	}

	@Override
	public void mutate(MutationContext ctx) {
		randomMutator.mutate(ctx);
	}

	@Override
	public MutatorType getMutatorType() {
		return MutatorType.MIXED;
	}

}
