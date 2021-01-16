package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;

/**
 * Mutation injection entry point. Let's clients mutate a scenario.
 * The mutation is parameterized by the concrete {@link Mutator} and {@link ScenarioObjectClusterFactor} as well
 * as the preferences within the eclipse context.
 * 
 * @author Oliver Urbaniak
 *
 * @see MutationContext
 */
public class MutationInjection {

	@Inject
	private Mutator mutator;
	
	@Inject
	private ScenarioObjectClusterFactory factory;
	
	/**
	 * Applies mutations as defined in the {@link Mutator} generating a mutant scenario. 
	 * 
	 * @param scenario 
	 * @throws IOException
	 * 
	 * @return the mutation result contains the scenario pair (original, mutated) and the mutation registry
	 * 
	 * @see Mutator
	 * @see MutationRegistry
	 * @see ScenarioObjectClusterFactory
	 */
	public MutationResult generateMutant(final Configuration scenario) {
		Configuration mutScenario = EcoreUtil.copy(scenario);
		TreeIterator<EObject> it = EcoreUtil.<EObject>getAllProperContents(mutScenario, true);
		MutationRegistry mutRegistry = new MutationRegistry();
		
		mutateTree(it, scenario, mutScenario, mutRegistry);
		
		return new MutationResult(scenario, mutScenario, mutRegistry);
	}
	
	private void mutateTree(TreeIterator<EObject> it, Configuration scenario, Configuration mutScenario, MutationRegistry mutRegistry) {
		while (it.hasNext()) {
			EObject eobject = it.next();
			
			// create a cluster from a key scenario object
			List<EObject> cluster = new ArrayList<>();
			if (eobject instanceof POU) {
				cluster = factory.createFromPOU((POU) eobject);
			} else if (eobject instanceof Action) {
				cluster = factory.createFromAction((Action) eobject);
			} else if (eobject instanceof StructuredText) {
				cluster = factory.createFromST((StructuredText) eobject);
			} else if (eobject instanceof Expression) {
				cluster = factory.createFromSTExpression((Expression) eobject);
			} else if (eobject instanceof SequentialFunctionChart) {
				cluster = factory.createFromSFC((SequentialFunctionChart) eobject);
			} else if (eobject instanceof Step) {
				cluster = factory.createFromSFCStep((Step) eobject);
			} else if (eobject instanceof AbstractAction) {
				cluster = factory.createFromSFCAction((AbstractAction) eobject);
			} else if (eobject instanceof Transition) {
				cluster = factory.createFromSFCTransition((Transition) eobject);
			}
			
			if (cluster.isEmpty()) {
				continue;
			}
		
			// create mutation context
			MutationContext mutCtx = new MutationContext(scenario, mutScenario);
			mutCtx.getCtxObjects().addAll(cluster);

			if (mutCtx.sharesElementsWith(mutRegistry.getMostRecentMutationContext().get())) {
				continue;
			}
			
			// apply mutations
			mutator.mutate(mutCtx);
			mutRegistry.addMutationContext(mutCtx);
			
			// remove sub tree since eobjects might have been deleted or inserted, a new iterator can handle these changes
			if (mutCtx.hasChangedTreeStructure()) {
				it.prune();
				TreeIterator<EObject> subtreeIt = EcoreUtil.getAllProperContents(eobject, true);
				subtreeIt.next();
				mutateTree(subtreeIt, scenario, mutScenario, mutRegistry);				
			}
		}		
	}
}
