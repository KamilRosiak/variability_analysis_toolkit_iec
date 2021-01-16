package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import org.eclipse.emf.ecore.EObject;

/**
 * This object holds references on the origin EObject an a mutation of it.
 * @author Kamil Rosiak
 *
 */
public class MutationPair {
	private EObject origin;
	private EObject mutant;
	
	public MutationPair(EObject origin, EObject mutant) {
		setMutant(mutant);
		setOrigin(origin);
	}

	public EObject getOrigin() {
		return origin;
	}

	public void setOrigin(EObject origin) {
		this.origin = origin;
	}

	public EObject getMutant() {
		return mutant;
	}

	public void setMutant(EObject mutant) {
		this.mutant = mutant;
	}
}
