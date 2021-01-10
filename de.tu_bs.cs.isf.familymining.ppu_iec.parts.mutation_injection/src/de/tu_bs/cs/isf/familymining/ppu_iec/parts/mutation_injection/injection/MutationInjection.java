package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;


public class MutationInjection {

	@Inject
	@Named("STMutator")
	private Mutator mutator;
	
	@Inject
	@Named("single")
	private ScenarioObjectClusterFactory factory;
	
	@Inject
	private ScenarioStorage scenarioStorage;
	
	private MutationRegistry mutationRegistry = new MutationRegistry();
		
	public Configuration loadScenario(String name) {
		Optional<Configuration> config = scenarioStorage.loadScenario(name);
		if (!config.isPresent()) {
			throw new RuntimeException("No configuration with name \"%s\" found in workspace. "
					+ "The name refers to the path configuration/resources[name] in the xml ");
		}
		return config.get();
	}

	public void saveScenario(Configuration scenario, String postfix) throws IOException {
		String scenarioName = scenarioStorage.getName(scenario); 
		System.out.println(String.format("Scenario \"%s\" stored in directory %s.", scenarioName, scenarioName, scenarioStorage.getMutantDirectoryName()));
		scenarioStorage.saveScenario(scenarioName+postfix, scenario);
	}

	/**
	 * Loads scenario, applies mutations as defined in the {@link Mutator}, 
	 * and writes the result back to the mutant directory. 
	 * 
	 * @param scenarioName
	 * @param mutantPostfix
	 * @throws IOException
	 * 
	 * @see Mutator
	 * @see ScenarioObjectClusterFactory
	 * @see ScenarioStorage#getMutantDirectoryName()
	 */
	public Configuration generateMutant(Configuration scenario) throws IOException {
		Configuration mutScenario = EcoreUtil.copy(scenario);
		TreeIterator<EObject> it = EcoreUtil.<EObject>getAllProperContents(mutScenario, true);
		
		mutateTree(it, scenario, mutScenario);
		
		return mutScenario;
	}
	
	private void mutateTree(TreeIterator<EObject> it, Configuration scenario, Configuration mutScenario) {
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
			
			MutationContext mutCtx = new MutationContext(scenario, mutScenario);
			mutCtx.getCtxObjects().addAll(cluster);

			if (mutCtx.sharesElementsWith(mutationRegistry.getMostRecentMutationContext())) {
				continue;
			}
			
			mutator.mutate(mutCtx);
			mutationRegistry.getMutCtxs().add(mutCtx);
			
			// remove sub tree since eobjects might have been deleted or inserted, a new iterator can handle these changes
			if (mutCtx.hasChangedTreeStructure()) {
				it.prune();
				TreeIterator<EObject> subtreeIt = EcoreUtil.getAllProperContents(eobject, true);
				subtreeIt.next();
				mutateTree(subtreeIt, scenario, mutScenario);				
			}
		}		
	}

	public static class MutationRegistry {
		List<MutationContext> mutCtxs = new ArrayList<>();

		public List<MutationContext> getMutCtxs() {
			return mutCtxs;
		}

		public void setMutCtxs(List<MutationContext> mutCtxs) {
			this.mutCtxs = mutCtxs;
		}
		
		public MutationContext getMostRecentMutationContext() {
			if (!mutCtxs.isEmpty()) {
				return mutCtxs.get(mutCtxs.size()-1);
			}
			return null;
		}
		
	}
}
