package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;

import de.tu_bs.cs.isf.e4cf.core.file_structure.FileTreeElement;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.MutationEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
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

	@Execute
	public void executeMutation(ServiceContainer services, MutationInjectionConfig config, MutationEngine engine) {
		List<FileTreeElement> scenarioSelection = services.rcpSelectionService.getCurrentSelectionsFromExplorer();
		if (!scenarioSelection.isEmpty()) {
			engine.startMutation(selectedSeedSupplier(scenarioSelection));
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
