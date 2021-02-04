package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators.SFCGenerator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.impl.SequentialFunctionChartImpl;

@Creatable
@Singleton
public class StepInserter extends SFCMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		SequentialFunctionChartImpl randomSFCImpl = getRandomSFCImpl(mutCtx);

		if (randomSFCImpl != null) {
			Step generatedStep = SFCGenerator.generateStep();
			SFCGenerator.connectStepBetween(getARandomStepFromSFC(randomSFCImpl), generatedStep,
					getARandomStepFromSFC(randomSFCImpl));
			mutCtx.logInsertion(randomSFCImpl, generatedStep);
			mutCtx.setChangedTreeStructure(true);
			return true;
		} else {
			return null;
		}
	}

}
