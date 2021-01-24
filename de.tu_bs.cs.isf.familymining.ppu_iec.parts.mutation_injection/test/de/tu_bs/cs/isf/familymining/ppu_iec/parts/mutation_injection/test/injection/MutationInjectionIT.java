package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.injection;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.AttributeFilter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.VATContextTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public class MutationInjectionIT extends VATContextTest {

	private ScenarioStorage scenarioStorage;

	private MutationInjection mutationInjection;	
	
	public MutationInjectionIT() {
		super();	
	}
	
	@BeforeEach
	public void beforeEach() {
		scenarioStorage = addToContext(ScenarioStorage.class);
		addMockToContext(AttributeFilter.class);

		ContextInjectionFactory.inject(new MutationInjectionConfig(), getEclipseCtx());
		mutationInjection = addToContext(MutationInjection.class);
	}
	
	/**
	 * Performs 20 repetitions of mutation generation for an example scenario
	 * 
	 * @param repetitionInfo
	 * @throws IOException
	 */
	@RepeatedTest(20)
	public void smokeTests(RepetitionInfo repetitionInfo) throws IOException {		
		String scenario24 = "Scenario24";
		Optional<Configuration> config = scenarioStorage.loadScenario(scenario24);

		MutationResult mutatedConfigResult = mutationInjection.generateMutant(config.get());
		
		scenarioStorage.saveScenario(mutatedConfigResult.getMutated(), scenario24+"_"+repetitionInfo.getCurrentRepetition());
	}
	
	
}
