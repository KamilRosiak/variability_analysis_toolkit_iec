package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;

public interface Mutator {
	
	void mutate(MutationContext ctx);
}
