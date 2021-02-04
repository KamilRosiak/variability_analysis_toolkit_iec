package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.impl.SequentialFunctionChartImpl;
@Creatable
@Singleton
public class StepRemover extends SFCMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		SequentialFunctionChartImpl randomSFCImpl = getRandomSFCImpl(mutCtx);
		
		if(randomSFCImpl != null) {
			Step stepToRemove = getARandomStepFromSFC(randomSFCImpl);
			if(stepToRemove != null) {
				randomSFCImpl.getSteps().remove(stepToRemove);
				mutCtx.logRemoval(stepToRemove);
				EcoreUtil.delete(stepToRemove,true);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

}
