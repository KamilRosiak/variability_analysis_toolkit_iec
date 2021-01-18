package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;

public class EvaluationIterator extends ContainerIterator {

	private MutationResult mutResult;
	
	public EvaluationIterator(MutationResult mutResult) {
		this.mutResult = mutResult;
	}

	@Override
	protected <T extends EObject> void process(IECAbstractContainer<T> container) {
		
	}

	@Override
	protected <T extends IECAbstractContainer<?>> void process(IECAbstractOption<T> option) {
		System.out.println(option.getLabel()+"\n");		
	}

}
