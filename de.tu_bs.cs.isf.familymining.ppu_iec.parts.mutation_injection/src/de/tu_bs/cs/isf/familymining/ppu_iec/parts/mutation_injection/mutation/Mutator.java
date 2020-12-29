package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;

public interface Mutator {
	
	final static String MUTATOR_PREF = "de.tu_bs.cs.isf.familymining.ppu_iec_parts.mutation_injection";
	
	void mutate(MutationContext ctx);
}
