package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.PreferencesUtil;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.key_value.KeyValueNode;
import de.tu_bs.cs.isf.e4cf.core.util.RCPContentProvider;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.util.MetricContainerSerializer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult.RunResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorageException;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.stringtable.MutationST;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

@Creatable
public class MutationEngine {
	private static final String[] IEC_SCENARIO_SEEDS = { "Scenario_1", "Scenario_2", "Scenario_3", "Scenario_4a",
			"Scenario_4b", "Scenario_5", "Scenario_6", "Scenario_7", "Scenario_8", "Scenario_9", "Scenario_10",
			"Scenario_11", "Scenario_12", "Scenario_13", "Scenario_14", "Scenario_15", "Scenario_16", "Scenario_17",
			"Scenario_18", "Scenario_19", "Scenario_20", "Scenario21", "Scenario24" };

	@Inject
	private ScenarioStorage scenarioStorage;

	@Inject
	private MutationInjection mutationInjection;

	@Inject
	private ScenarioComparator scenarioComparator;

	@Inject
	private Randomization randomly;

	/**
	 * Start the mutation cycle with all the PPU IEC Scenarios as seeds.
	 */
	public void startMutation() {
		startMutation(this::selectSeed);
	}

	public void startMutation(Supplier<Configuration> seedSupplier) {
		// prepare scenarios for eval cycles
		String resultDirectory = "paper-eval";
		scenarioStorage.setMutationDirectory(resultDirectory);

		// prepare result structure
		EvaluationResult evalResult = new EvaluationResult();
		int runs = PreferencesUtil.getValueWithDefault(MutationST.BUNDLE_NAME, MutationST.NUMBER_RUNS_PREF, 1)
				.getIntValue();

		// load metric container
		List<MetricContainer> metrics = loadMetrics();

		// run the mutation iteration
		for (int run = 1; run <= runs; run++) {
			Configuration seed = seedSupplier.get();
			//System.out.println("RUN__" + run + "____________________________");
			mutationCycle(seed, run, metrics, evalResult);
		}
		System.out.println(evalResult);	

		// export the results
		evalResult.setName(
				"result_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "_"));
		evalResult.setDirectory(resultDirectory);
		evalResult.setTotalRuns(runs);

		export(evalResult);
	}

	/**
	 * This method loads metrics that are selected in the preferences
	 * 
	 * @return
	 */
	private List<MetricContainer> loadMetrics() {
		List<MetricContainer> metric = new ArrayList<MetricContainer>();
		KeyValueNode firstMetric = PreferencesUtil.getValueWithDefault(MutationST.BUNDLE_NAME,
				MutationST.FIRST_METRIC_KEY, "");
		KeyValueNode secondMetric = PreferencesUtil.getValueWithDefault(MutationST.BUNDLE_NAME,
				MutationST.SECOND_METRIC_KEY, "");

		metric.add(MetricContainerSerializer.decode(firstMetric.getStringValue()));
		metric.add(MetricContainerSerializer.decode(secondMetric.getStringValue()));
		return metric;
	}

	public Configuration selectSeed() {
		String seedName = randomly.pickFrom(IEC_SCENARIO_SEEDS);
		Optional<Configuration> configuration = scenarioStorage.loadScenario(seedName);
		if (!configuration.isPresent()) {
			throw new ScenarioStorageException(String.format("Scenario \"%s\" could not be loaded", seedName));
		}
		return configuration.get();
	}

	private void mutationCycle(Configuration seed, int run, List<MetricContainer> metrics,
			EvaluationResult evalResult) {
		// generate, rename and store mutant
		MutationResult mutationResult = mutationInjection.generateMutant(seed);
		Configuration mutant = mutationResult.getMutated();
		String mutantName = name(seed) + "_run-" + run;
		try {
			scenarioStorage.saveScenario(mutant, name(seed) + "_run-" + run);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int counter = 0;
		for (MetricContainer metric : metrics) {
			// create result for this run
			RunResult runResult = new RunResult();
			// export the results to the mutation folder
			runResult.setRun(run);
			runResult.setName(mutantName + "_" + metric.getName());
			// find changes
			ConfigurationResultRoot result = scenarioComparator.compare(seed, mutant, metric);
			List<AbstractContainer> changeList = scenarioComparator.findChanges(result);
			List<MutationPair> mutantList = new ArrayList<MutationPair>(mutationResult.getMutationRegistry().getMutationPairs());
			runResult.setNumberMutations(mutantList.size());
			runResult.setNumberChangesFound(changeList.size());
			//printObjects(run, changeList, mutantList);

			// search for matches between mutants and found changes
			int foundMutants = searchForMutants(changeList, mutantList);
			// evaluate
			runResult.getClassisfication().setTruePositives(foundMutants);
			runResult.getClassisfication().setFalseNegatives(mutantList.size());
			runResult.getClassisfication().setFalsePositives(changeList.size());
			if (counter == 0) {
				evalResult.getResultFirstMetric().add(runResult);
			} else {
				evalResult.getResultSecondMetric().add(runResult);
			}
			counter++;
			System.out.println(runResult);
		}
	}

	private void printObjects(int run, List<AbstractContainer> changeList, List<MutationPair> mutantList) {
		System.out.println("RUN: " + run);
		System.out.println("Changes________________");
		for (AbstractContainer container : changeList) {
			System.out.println("first: " + container.getFirst());
			System.out.println("second: " + container.getSecond() + "\n");
		}
		System.out.println("Mutants________________");
		for (MutationPair mutantPair : mutantList) {
			System.out.println("Origin: " + mutantPair.getOrigin());
			System.out.println("Mutant: " + mutantPair.getMutant() + "\n");
		}
	}

	/**
	 * This method iterates over both list and removes pairs if elements are
	 * matching
	 */
	private int searchForMutants(List<AbstractContainer> changeList, List<MutationPair> totalMutants) {
		// TRUE POSITIVE
		int foundMutants = 0;

		Iterator<AbstractContainer> changeIterator = changeList.iterator();
		while (changeIterator.hasNext()) {
			AbstractContainer currentContainer = changeIterator.next();
			Iterator<MutationPair> mutantsIterator = totalMutants.iterator();
			while (mutantsIterator.hasNext()) {
				MutationPair mutantPair = mutantsIterator.next();

				// Added artifact //type III
				if (mutantPair.getOrigin() == null && mutantPair.getMutant() != null
						&& currentContainer.getFirst() == null && currentContainer.getSecond() != null
						&& mutantPair.getMutant().equals(currentContainer.getSecond())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					break;
				}

				// Removed artifact //type III
				if (mutantPair.getOrigin() != null && mutantPair.getMutant() == null
						&& currentContainer.getFirst() != null && currentContainer.getSecond() == null
						&& mutantPair.getOrigin().equals(currentContainer.getFirst())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					break;
				}

				// Changed artifact //type II
				if (mutantPair.getOrigin() != null && mutantPair.getMutant() != null
						&& currentContainer.getFirst() != null && currentContainer.getSecond() != null
						&& mutantPair.getOrigin().equals(currentContainer.getFirst())
						&& mutantPair.getMutant().equals(currentContainer.getSecond())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					break;
				}
			}
		}
		return foundMutants;
	}

	public void export(EvaluationResult evalResult) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(evalResult);

			// create folder if not yet created
			String mutationDirectoryPath = RCPContentProvider.getCurrentWorkspacePath()
					+ scenarioStorage.getMutationDirectory();
			File mutationDirectory = new File(mutationDirectoryPath);
			if (!mutationDirectory.exists()) {
				mutationDirectory.mkdir();
			}

			// create result file
			String resultPath = mutationDirectoryPath + "/" + evalResult.getName() + ".json";
			File resultFile = new File(resultPath);
			if (!resultFile.exists()) {
				resultFile.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(resultFile);
			fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
			fos.close();

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String name(Configuration scenario) {
		return scenario.getResources().get(0).getName();
	}

	public MutationInjection getMutationInjection() {
		return mutationInjection;
	}

	public void setMutationInjection(MutationInjection mutationInjection) {
		this.mutationInjection = mutationInjection;
	}
}
