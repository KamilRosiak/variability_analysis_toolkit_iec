package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.eval;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.MutationEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type2Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type3Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.AttributeFilter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.VATContextTest;

public class MutationEngineTest extends VATContextTest {

	
	@Test
	public void testExport() {
		addMockToContext(IEclipsePreferences.class);
		addMockToContext(AttributeFilter.class);
		addMockToContext(Type2Mutator.class);
		addMockToContext(Type3Mutator.class);
		addMockToContext(ServiceContainer.class);
		addMockToContext(IEclipsePreferences.class);
		
		ScenarioStorage scenarioStorage = addMockToContext(ScenarioStorage.class);
		Mockito.when(scenarioStorage.getMutationDirectory()).thenReturn("testDir");
		
		MutationEngine engine = ContextInjectionFactory.make(MutationEngine.class, getEclipseCtx());
		
		EvaluationResult evalResult = new EvaluationResult();
		evalResult.setName("test");
		evalResult.setDirectory("directory");
		evalResult.setTotalRuns(20);
		
		engine.export(evalResult);
	}
}
