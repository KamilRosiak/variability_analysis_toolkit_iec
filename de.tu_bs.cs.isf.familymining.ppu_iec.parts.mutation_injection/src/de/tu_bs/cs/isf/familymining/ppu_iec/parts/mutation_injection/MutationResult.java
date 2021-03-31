package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationRegistry;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public class MutationResult {
	
	private Configuration original;
	
	private Configuration mutated;
	
	private MutationRegistry mutRegistry;
	
	public MutationResult(Configuration original, Configuration mutated, MutationRegistry mutRegistry) {
		this.original = original;
		this.mutated = mutated;
		this.mutRegistry = mutRegistry;
	}
	
	public Configuration getOriginal() {
		return original;
	}

	public void setOriginal(Configuration original) {
		this.original = original;
	}

	public Configuration getMutated() {
		return mutated;
	}

	public void setMutated(Configuration mutated) {
		this.mutated = mutated;
	}

	public MutationRegistry getMutationRegistry() {
		return mutRegistry;
	}

	public void setMutationRegistry(MutationRegistry mutRegistry) {
		this.mutRegistry = mutRegistry;
	}
}
