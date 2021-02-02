package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;

import de.tu_bs.cs.isf.e4cf.core.file_structure.FileTreeElement;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.MutationEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data.EvaluationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type2Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type3Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.rcp_e4.EMFModelLoader.impl.EMFModelLoader;

/**
 * This handler starts the mutation process with the current selection as the
 * starting point. The mutation process can be adjusted on the respective
 * preference page.
 * 
 * @author Kamil Rosiak
 *
 */
public class MutationHandler {

	private static final String SCENARIO_FILE_EXT = "project";

	@Inject
	private Randomization randomly;

	/**
	 * Initiate the mutation generation and evaluation cycle.<br>
	 * <br>
	 * <b>HINT</b>: The MutationInjectionConfig parameter initializes the mutation
	 * injection when injected as parameter even if it is not used.
	 * 
	 * @param services
	 * @param config
	 * @param engine
	 */
	@Execute
	public void executeMutation(ServiceContainer services, MutationInjectionConfig config, MutationEngine engine, IEclipseContext context) {
		List<FileTreeElement> scenarioSelection = services.rcpSelectionService.getCurrentSelectionsFromExplorer();
		if (!scenarioSelection.isEmpty()) {
			/**
			context.set(Mutator.class, ContextInjectionFactory.make(Type2Mutator.class, context));
			EvaluationResult resultT2 = engine.startMutation(selectedSeedSupplier(scenarioSelection));
			System.out.println("---------T2-Cl0nZ-------------");
			System.out.println(resultT2);
			engine = ContextInjectionFactory.make(MutationEngine.class, context);
			*/
			context.set(Mutator.class, ContextInjectionFactory.make(Type3Mutator.class, context));
			EvaluationResult resultT3 = engine.startMutation(selectedSeedSupplier(scenarioSelection));
			System.out.println("---------T3-Cl0nZ-------------");
			System.out.println(resultT3);
			
		} else {
			engine.startMutation();
		}
	}

	/**
	 * Return true if only one element is selected and the selection is a file with
	 * the extension .project.
	 */
	@CanExecute
	@Evaluate
	public boolean canExecute(ServiceContainer services) {
		List<FileTreeElement> selection = services.rcpSelectionService.getCurrentSelectionsFromExplorer();
		if (selection.stream().noneMatch(FileTreeElement::isDirectory)
				&& selection.stream().allMatch(file -> file.getExtension().equals(SCENARIO_FILE_EXT))) {
			return true;
		} else {
			return false;
		}
	}

	private Supplier<Configuration> selectedSeedSupplier(List<FileTreeElement> scenarioFiles) {
		List<Configuration> selectedSeeds = new ArrayList<>();
		for (FileTreeElement scenarioFile : scenarioFiles) {
			Configuration scenario = (Configuration) EMFModelLoader.load(scenarioFile.getAbsolutePath(),
					SCENARIO_FILE_EXT);
			selectedSeeds.add(scenario);
		}

		return () -> {
			return randomly.pickFrom(selectedSeeds);
		};
	}

}
