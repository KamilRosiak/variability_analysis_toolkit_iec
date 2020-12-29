package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.function.Function;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;

public interface Mutation extends Function<MutationContext, MutationContext> {
	
	public final static String MUTATION_PREF = "de.tu_bs.cs.isf.familymining.ppu_iec_parts.mutation_injection";

}
