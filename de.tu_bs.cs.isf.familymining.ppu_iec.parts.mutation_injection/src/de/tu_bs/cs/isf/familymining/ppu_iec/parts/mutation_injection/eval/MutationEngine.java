package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation.MUTATION_PREF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.Preference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.util.RCPContentProvider;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.ConfigurationCompareUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult.RunResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type2Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type3Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorageException;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

@Creatable
public class MutationEngine {
	private static final int RUNS = 10;

	private static final String[] SCENARIO_SEEDS = { "Scenario_1", "Scenario_2", "Scenario_3", "Scenario_4a",
			"Scenario_4b", "Scenario_5", "Scenario_6", "Scenario_7", "Scenario_8", "Scenario_9", "Scenario_10",
			"Scenario_11", "Scenario_12", "Scenario_13", "Scenario_14", "Scenario_15", "Scenario_16", "Scenario_17",
			"Scenario_18", "Scenario_19", "Scenario_20", "Scenario21", "Scenario24" };

	@Preference(nodePath = MUTATION_PREF)
	private IEclipsePreferences prefs;

	@Inject
	private Type2Mutator type2Mutator;

	@Inject
	private Type3Mutator type3Mutator;

	@Inject
	private ScenarioStorage scenarioStorage;

	@Inject
	private ServiceContainer services;

	private Randomization randomly;

	private MutationInjection mutationInjection;

	/**
	 * This method initializes the mutationInjection
	 */
	@Inject
	public void initMutationInjection(IEclipseContext context,
			@Preference(nodePath = MUTATION_PREF) IEclipsePreferences prefs) {
		ContextInjectionFactory.inject(new MutationInjectionConfig(), context);
		mutationInjection = ContextInjectionFactory.make(MutationInjection.class, context);
		this.randomly = new Randomization();

	}

	public void startMutation() {
		// prepare result structure
		EvaluationResult evalResult = new EvaluationResult();

		// prepare scenarios for eval cycles
		String resultDirectory = "paper-eval";
		scenarioStorage.setMutationDirectory(resultDirectory);

		for (int run = 1; run <= RUNS; run++) {
			Configuration seed = selectSeed();
			RunResult runResult = mutationCycle(seed, run);
			evalResult.getResult().add(runResult);
		}

		// export the results
		evalResult.setName(
				"result_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "_"));
		evalResult.setDirectory(resultDirectory);
		evalResult.setTotalRuns(RUNS);

		export(evalResult);

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

	public Configuration selectSeed() {
		// String seedName = randomly.pickFrom(SCENARIO_SEEDS);
		String seedName = randomly.pickFrom("ST_Evolution_1");
		Optional<Configuration> configuration = scenarioStorage.loadScenario(seedName);
		if (!configuration.isPresent()) {
			throw new ScenarioStorageException(String.format("Scenario \"%s\" could not be loaded", seedName));
		}
		return configuration.get();
	}

	private RunResult mutationCycle(Configuration seed, int run) {
		// Select mutator 50/50
		Mutator mutator = randomly.pickFrom(type2Mutator, type3Mutator);
		mutationInjection.setMutator(mutator);

		// generate and rename mutant
		MutationResult mutationResult = mutationInjection.generateMutant(seed);
		Configuration mutant = mutationResult.getMutated();

		// save the mutant
		String mutantName = name(seed) + "_run-" + run;
		try {
			scenarioStorage.saveScenario(mutant, name(seed) + "_run-" + run);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// find changes
		ConfigurationResultRoot result = ConfigurationCompareUtil.compare(seed, mutant);
		List<AbstractContainer> changeList = ConfigurationCompareUtil.findChanges(result);

		// search for matches between mutants and found changes
		List<MutationPair> totalMutants = mutationResult.getMutationRegistry().getMutationPairs();

		System.out.println(
				"RUN: " + run + " NumberMutants: " + totalMutants.size() + " ChangesFound: " + changeList.size());
		System.out.println("MUTANTS__________________________");
		for (MutationPair pair : totalMutants) {
			System.out.println("Original: " + pair.getOrigin());
			System.out.println("Mutant: " + pair.getMutant());
			System.out.println("-------------------------------------------");
		}
		System.out.println("Changes__________________________");
		for (AbstractContainer pair : changeList) {
			System.out.println("First: " + pair.getFirst());
			System.out.println("Second: " + pair.getSecond());
			System.out.println("-------------------------------------------");
		}

		int foundMutants = searchForMutants(changeList, totalMutants);
		System.out.println("KILLED: " + foundMutants);

		// export the results to the mutation folder
		RunResult runResult = new RunResult();
		runResult.setRun(run);
		runResult.setName(mutantName);
		runResult.setCompareContainersFound(changeList.size());
		runResult.setTotalMutations(totalMutants.size());
		runResult.setMutationsFound(foundMutants);

		return runResult;
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

				// Added artifact
				if (mutantPair.getOrigin() == null && mutantPair.getMutant() != null
						&& currentContainer.getFirst() == null && currentContainer.getSecond() != null
						&& mutantPair.getMutant().equals(currentContainer.getSecond())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					break;
				}

				// Removed artifact
				if (mutantPair.getOrigin() != null && mutantPair.getMutant() == null
						&& currentContainer.getFirst() != null && currentContainer.getSecond() == null
						&& mutantPair.getOrigin().equals(currentContainer.getFirst())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					break;
				}

				// Changed artifact
				if (mutantPair.getOrigin() != null && mutantPair.getMutant() != null
						&& currentContainer.getFirst() != null && currentContainer.getSecond() != null
						&& mutantPair.getOrigin().equals(currentContainer.getFirst())
						&& mutantPair.getMutant().equals(currentContainer.getSecond())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					break;
					// TODO: Create an evaluation of all runs with detailed information
				}
			}
		}
		return foundMutants;
	}

	private String name(Configuration scenario) {
		return scenario.getResources().get(0).getName();
	}

	public ServiceContainer getServices() {
		return services;
	}

	public void setServices(ServiceContainer services) {
		this.services = services;
	}

	public MutationInjection getMutationInjection() {
		return mutationInjection;
	}

	public void setMutationInjection(MutationInjection mutationInjection) {
		this.mutationInjection = mutationInjection;
	}
}
