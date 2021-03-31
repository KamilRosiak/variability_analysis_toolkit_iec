package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.injection;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.RandomizedClusterFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.ComplexAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SimpleAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.VariableExpression;

public class RandomizedClusterFactoryTest extends ScenarioTest {

	@Test
	public void testClusterFiltering_singleObjects() {
		// create factory that creates cluster on every createrFrom* invocation
		RandomizedClusterFactory clusterFactory = new RandomizedClusterFactory();		
		clusterFactory.setAcceptanceChances(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
	
		SoftAssertions soft = new SoftAssertions();
		
		VariableExpression var = steFactory.createVariableExpression();
		List<EObject> exprCluster = clusterFactory.createFromSTExpression(var);
		soft.assertThat(exprCluster).isEmpty();
		
		Transition trans = sfcFactory.createTransition();
		List<EObject> transCluster = clusterFactory.createFromSFCTransition(trans);
		soft.assertThat(transCluster).isEmpty();
		
		SimpleAction simpleAction = sfcFactory.createSimpleAction();
		List<EObject> simpleActionCluster = clusterFactory.createFromSFCAction(simpleAction);
		soft.assertThat(simpleActionCluster).isEmpty();
		
		ComplexAction complextAction = sfcFactory.createComplexAction();
		List<EObject> complexActionCluster = clusterFactory.createFromSFCAction(complextAction);
		soft.assertThat(complexActionCluster).isEmpty();
		
		soft.assertAll();
	}
	
	@Test
	public void testClusterFiltering_sfcCluster() {
		// create factory that creates cluster on every createrFrom* invocation
		RandomizedClusterFactory clusterFactory = new RandomizedClusterFactory();		
		clusterFactory.setAcceptanceChances(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);		
		
		SimpleAction simpleAction = sfcFactory.createSimpleAction();
		ComplexAction complextAction = sfcFactory.createComplexAction();
		Step step = sfcFactory.createStep();
		step.getActions().add(simpleAction);
		step.getActions().add(complextAction);
		
		Transition trans = sfcFactory.createTransition();		
		
		SequentialFunctionChart sfc = sfcFactory.createSequentialFunctionChart();
		sfc.getSteps().add(step);
		sfc.getTransitions().add(trans);
		
		List<EObject> sfcCluster = clusterFactory.createFromSFC(sfc);
		
		Assertions.assertThat(sfcCluster).contains(sfc);
	}

}
