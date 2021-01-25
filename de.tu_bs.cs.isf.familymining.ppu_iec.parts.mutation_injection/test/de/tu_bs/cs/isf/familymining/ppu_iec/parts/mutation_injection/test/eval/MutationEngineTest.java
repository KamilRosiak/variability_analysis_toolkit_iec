package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.eval;

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.MutationEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioStorage;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.VATContextTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.rcp_e4.EMFModelLoader.impl.EMFModelLoader;

public class MutationEngineTest extends VATContextTest {

	public static final String METRIC_PREF_BUNDLE = "de.tu_bs.cs.isf.familymining.ppu_iec.core";
	public static final String METRIC_PREF_KEY = "DEFAULT_METRIC_PREF";

	private MutationEngine mutationEngine;
	
	@BeforeEach
	private void initialize() {
		refreshContext();
		
		addMockToContext(ServiceContainer.class);
		
		// initialize the metric resource path
		InstanceScope.INSTANCE.getNode(METRIC_PREF_BUNDLE).put(METRIC_PREF_KEY, getResourcePath("test", "test-metric.metric"));
		IEclipsePreferences prefs = addToContext(EclipsePreferences.class, IEclipsePreferences.class);
		
		addToContext(MutationInjectionConfig.class);
		
		ScenarioStorage scenarioStorage = addToContext(ScenarioStorage.class);
		scenarioStorage.setMutationDirectory("testDir");
		
		mutationEngine = ContextInjectionFactory.make(MutationEngine.class, getEclipseCtx());
	}
	
	@Test
	public void smokeTest() {
		String scenarioPath = getResourcePath("test", "scenario24_small.project");
		
		mutationEngine.startMutation(() -> (Configuration) EMFModelLoader.load(scenarioPath, "project"));
	}
	
	@Test
	public void testExport() {		
		EvaluationResult evalResult = new EvaluationResult();
		evalResult.setName("test");
		evalResult.setDirectory("directory");
		evalResult.setTotalRuns(20);
		
		mutationEngine.export(evalResult);
	}
}
