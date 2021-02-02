package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;
/**
 * Container for detected changed object and the assigned clone type
 * @author Kamil Rosiak
 *
 */
public class HitContainer {
	private AbstractContainer container;
	private MutationPair mutantPair;
	private CloneType detectedType = CloneType.UNDEFINED;
	
	public HitContainer(AbstractContainer currentContainer, MutationPair mutantPair, CloneType detectedType) {
		setDetectedType(detectedType);
	}

	public CloneType getDetectedType() {
		return detectedType;
	}

	public void setDetectedType(CloneType detectedType) {
		this.detectedType = detectedType;
	}

	public AbstractContainer getContainer() {
		return container;
	}

	public void setContainer(AbstractContainer container) {
		this.container = container;
	}

	public MutationPair getMutantPair() {
		return mutantPair;
	}

	public void setMutantPair(MutationPair mutantPair) {
		this.mutantPair = mutantPair;
	}
}