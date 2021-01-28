package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import de.tu_bs.cs.isf.familymining.ppu_iec.core.CompareEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.model.ModelCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.CompleteMutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public class ScenarioEvalution implements Runnable {

	@Inject
	private CompleteMutationInjection mutationInjection;
	
	@Inject
	private Supplier<Configuration> scenarioSupplier;
	
	@Inject
	private ScenarioStorage scenarioStorage;
	
	private MetricContainer metric;
	private CompareEngine compareEngine;
	
//	/**
//	 * Maps scenario name specified in <i>Configuration/resource[name]</i> to the respective mutations
//	 */
//	private ListMultimap<String, String> scenariosByName = ArrayListMultimap.create();
		
	@PostConstruct
	public void createCompareEngine() {
		metric = new MetricContainer("configName", "description"); // TODO: load metric
		compareEngine = new CompareEngine(null);	
	}
	
	@Override
	public void run() {
		Configuration scenario = null;
		while ((scenario = scenarioSupplier.get()) != null) {
			// mutate scenario
			MutationResult mutResult = mutationInjection.generateMutant(scenario);
			
			// compare both scenarios
			ModelCompareContainer modelContainer = compareEngine.compareModels(mutResult.getOriginal(), mutResult.getMutated(), metric);
			
			// evaluate the compare container with the mutation result
			evaluateCompareResult(mutResult, modelContainer);
		}
	}
	
	private void evaluateCompareResult(MutationResult mutResult, ModelCompareContainer modelContainer) {
		// TODO: write evaluation code
		EvaluationIterator evalIt = new EvaluationIterator(mutResult);
		evalIt.process(modelContainer);
	}
}
