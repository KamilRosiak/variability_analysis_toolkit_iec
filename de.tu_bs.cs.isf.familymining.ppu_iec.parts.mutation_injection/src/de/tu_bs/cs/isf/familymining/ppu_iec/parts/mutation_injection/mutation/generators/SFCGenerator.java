package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChartFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;

public class SFCGenerator {
	private static SequentialFunctionChartFactory factory = SequentialFunctionChartFactory.eINSTANCE;

	public static Step generateStep() {
		Step step = factory.createStep();
		step.setName(StringGenerator.getRandomStringOfLenght(20));
		return step;
	}
	
	/**
	 * Connects a inital step with a step after
	 */
	public static void connectStepBehind(Step initialStep, Step stepBehind) {
		Transition trans = factory.createTransition();
		trans.getSourceStep().add(initialStep);
		trans.getTargetStep().add(stepBehind);
		initialStep.getOutgoingTransitions().add(trans);
		stepBehind.getIncomingTransitions().add(trans);
	}

	/**
	 * Connects a step between two other steps
	 */
	public static void connectStepBetween(Step leftStep, Step middelStep, Step rightStep) {
		connectStepBehind(leftStep, middelStep);
		middelStep.setStepLevel(leftStep.getStepLevel() + 1);
		connectStepBehind(middelStep, rightStep);
	}

}
