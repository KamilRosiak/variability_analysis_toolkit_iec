package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public interface MutationInjection {
	
	MutationResult generateMutant(final Configuration scenario);
}
