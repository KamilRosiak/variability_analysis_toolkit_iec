package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.function.Function;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;

/**
 * Mutation operator that applies one kind of change to a given mutation context.
 * 
 * @author Oliver Urbaniak
 *
 */
public interface Mutation extends Function<MutationContext, Boolean> {
	
	public final static String MUTATION_PREF = "de.tu_bs.cs.isf.familymining.ppu_iec_parts.mutation_injection";

	/**
	 * Applies mutation to the given MutationContext.
	 * 
	 * @param mutCtx mutation context that records the changes caused by the mutation.
	 * @return true if the mutation operator has changed the mutation context. 
	 */
	@Override
	Boolean apply(MutationContext mutCtx);

	
}
