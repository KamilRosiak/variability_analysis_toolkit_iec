package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.handler;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import de.tu_bs.cs.isf.e4cf.core.file_structure.FileTreeElement;
import de.tu_bs.cs.isf.e4cf.core.stringtable.E4CStringTable;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.MutationEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.rcp_e4.EMFModelLoader.impl.EMFModelLoader;

/**
 * This handler starts the mutation process with the current selection as the starting point.
 * The mutation process can be adjusted on the respective preference page.
 * @author Kamil Rosiak
 *
 */
public class MutationHandler {

	@Execute
	public void executeMutation(ServiceContainer services, MutationEngine engine) {
		List<FileTreeElement> currentSelections = services.rcpSelectionService.getCurrentSelectionsFromExplorer();
		engine.startMutation((Configuration) EMFModelLoader.load(currentSelections.get(0).getAbsolutePath(), E4CStringTable.FILE_ENDING_CONFIGURATION));
	}
	
	/**
	 * Return true if only one element is selected and the selection is a file with the extension .project.
	 */
	@CanExecute
	@Evaluate
	public boolean canExecute(ServiceContainer services) {
		if(services.rcpSelectionService.getCurrentSelectionsFromExplorer().size() == 1 &&
			!services.rcpSelectionService.getCurrentSelectionFromExplorer().isDirectory() && 
			services.rcpSelectionService.getCurrentSelectionFromExplorer().getExtension().equals("project")) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
}
