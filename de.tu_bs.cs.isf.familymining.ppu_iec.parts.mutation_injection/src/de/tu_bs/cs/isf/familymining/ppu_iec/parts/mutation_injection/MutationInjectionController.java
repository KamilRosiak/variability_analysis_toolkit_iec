package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation.MUTATION_PREF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public class MutationInjectionController {

	private MutationInjection mutationInjection;
	
	private ScenarioStorage scenarioStorage;
	
	private List<MutationResult> mutationResults = new ArrayList<>();
	
	@Inject
	public void fillContext(IEclipseContext context, @Preference(nodePath = MUTATION_PREF) IEclipsePreferences prefs) {
		scenarioStorage = ContextInjectionFactory.make(ScenarioStorage.class, context);
		
		ContextInjectionFactory.inject(new MutationInjectionConfig(), context);
		mutationInjection = ContextInjectionFactory.make(MutationInjection.class, context);
	}

	@PostConstruct
	public void init() {
		demo();
	}

	public void demo() {
		try {
			String scenario24_small = "scenario24_small";
			Optional<Configuration> config = scenarioStorage.loadScenario(scenario24_small);
			
			MutationResult mutatedConfigResult = mutationInjection.generateMutant(config.get());
			
			mutationResults.add(mutatedConfigResult);
			
			scenarioStorage.saveScenario(mutatedConfigResult.getMutated(), "-mutated");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
