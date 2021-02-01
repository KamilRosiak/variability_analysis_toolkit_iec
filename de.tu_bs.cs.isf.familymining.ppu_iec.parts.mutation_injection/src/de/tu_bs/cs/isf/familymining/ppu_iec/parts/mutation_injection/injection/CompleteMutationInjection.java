package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;

/**
 * Mutation injection entry point. Let's clients mutate a scenario.
 * The mutation is parameterized by the concrete {@link Mutator} and {@link ScenarioObjectClusterFactor} as well
 * as the preferences within the eclipse context.
 * For easier initialization, inject {@link MutationInjectionConfig} into the eclipse context or use it as a template 
 * for injecting your objects.
 * 
 * @author Oliver Urbaniak
 *
 * @see MutationContext
 */
@Creatable
public class CompleteMutationInjection implements MutationInjection {
	
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
		
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(scenario, mutScenario);
		
		mutateTree(it, scenario, mutScenario, mutRegistry, mapping);
		
		return new MutationResult(scenario, mutScenario, mutRegistry);
	}
	
	private void mutateTree(TreeIterator<EObject> it, Configuration scenario, Configuration mutScenario, MutationRegistry mutRegistry, BiMap<EObject, EObject> mapping) {
		while (it.hasNext()) {
			EObject eobject = it.next();
			
			List<EObject> cluster = createClusterFrom(eobject);
			
			if (cluster.isEmpty()) {
				continue;
			}
		
			// create mutation context
			MutationContext mutCtx = new MutationContext(mapping);
			mutCtx.getCtxObjects().addAll(cluster);

			Optional<MutationContext> recentMutCtx = mutRegistry.getMostRecentMutationContext();
			if (recentMutCtx.isPresent() && mutCtx.sharesElementsWith(recentMutCtx.get())) {
				continue;
			}
			
			// apply mutations
			mutator.mutate(mutCtx);
			if (!mutCtx.getMutationPairs().isEmpty()) {
				mutRegistry.getMutCtxs().add(mutCtx);
			}
			
			// remove sub tree since eobjects might have been deleted or inserted, a new iterator can handle these changes
			if (mutCtx.hasChangedTreeStructure()) {
				it.prune();
				TreeIterator<EObject> subtreeIt = EcoreUtil.getAllProperContents(eobject, true);
				if (subtreeIt.hasNext()) {
					subtreeIt.next();
					mutateTree(subtreeIt, scenario, mutScenario, mutRegistry, mapping);				
				}
			}
		}		
	}

	/**
	 * Creates a cluster with <i>eobject</i> as the root. A cluster is a connected set of EObjects.
	 * Two EObjects are connected iff they are in a containment relationship.
	 * 
	 * @param eobject
	 * @return eobject cluster
	 */
	private List<EObject> createClusterFrom(EObject eobject) {
		// create a cluster from a key scenario object
		List<EObject> cluster = new ArrayList<>();
		if (eobject instanceof POU) {
			cluster = factory.createFromPOU((POU) eobject);
		} else if (eobject instanceof Action) {
			cluster = factory.createFromAction((Action) eobject);
		} else if (eobject instanceof StructuredText) {
			cluster = factory.createFromST((StructuredText) eobject);
		} else if (eobject instanceof Statement) {
			cluster = factory.createFromSTStatement((Statement) eobject);
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
		return cluster;
	}
	
	/**
	 * Method transfers object ids from origin to copy
	 */
	private BiMap<EObject, EObject> constructOriginalToMutatedTreeMapping(Configuration original, Configuration mutated) {
		BiMap<EObject, EObject> mapping = HashBiMap.create();
		
		TreeIterator<EObject> origIt = EcoreUtil.getAllProperContents(original, true);
		TreeIterator<EObject> mutatedIt = EcoreUtil.getAllProperContents(mutated, true);
		while (origIt.hasNext() && mutatedIt.hasNext()) {
			EObject origObject = origIt.next();
			EObject mutatedObject = mutatedIt.next();
			mapping.put(origObject, mutatedObject);
		}
		
		if (origIt.hasNext() || mutatedIt.hasNext()) {
			throw new InequalTreeException("Trees do not have the same structure.");
		}
		
		return mapping;
	}

	public Mutator getMutator() {
		return mutator;
	}

	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}

	public ScenarioObjectClusterFactory getFactory() {
		return factory;
	}

	public void setFactory(ScenarioObjectClusterFactory factory) {
		this.factory = factory;
	}
}
