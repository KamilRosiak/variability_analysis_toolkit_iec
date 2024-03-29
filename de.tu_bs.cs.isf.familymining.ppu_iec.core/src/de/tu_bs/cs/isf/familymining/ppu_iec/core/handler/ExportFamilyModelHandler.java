package de.tu_bs.cs.isf.familymining.ppu_iec.core.handler;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import de.tu_bs.cs.isf.e4cf.core.file_structure.FileTreeElement;
import de.tu_bs.cs.isf.e4cf.core.stringtable.E4CStringTable;
import de.tu_bs.cs.isf.e4cf.core.util.RCPContentProvider;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.e4cf.core.util.services.RCPSelectionService;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.ConfigurationResultExporter;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.ExportedXMLTransformation;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.SolutionSerializer;

public class ExportFamilyModelHandler {
	
	@Execute
	public void execute(ServiceContainer services) {
		List<FileTreeElement> selectedFiles = services.rcpSelectionService.getCurrentSelectionsFromExplorer();
		for (FileTreeElement file : selectedFiles) {			
			ConfigurationResultRoot resultRoot = SolutionSerializer.decode(file.getAbsolutePath());
			String fileName = resultRoot.getChildren().get(0).getFirst().getIdentifier()+"_with_"+resultRoot.getChildren().get(0).getSecond().getIdentifier()+"_config_"+resultRoot.getMetric().getName();
			String path = RCPContentProvider.getCurrentWorkspacePath() + E4CStringTable.FAMILY_MODEL_DIRECTORY + "/" + fileName +"."+E4CStringTable.FILE_ENDING_FAMILY_MODEL_XML;
			ConfigurationResultExporter configResultExport = new ConfigurationResultExporter(resultRoot);
			configResultExport.saveMetricToXmi(path);
			ExportedXMLTransformation.transform(path);
		}
	}
	
	private boolean checkForMetric(List<FileTreeElement> selectedFiles) {
		return !selectedFiles.stream()
				.filter(element -> !element.isDirectory())
				.anyMatch(element -> !element.getExtension().equals(E4CStringTable.FILE_ENDING_FAMILY_MODEL));
	}
	
	@CanExecute
	public boolean canExecute(RCPSelectionService selectionService) {
		List<FileTreeElement> selectedFiles = selectionService.getCurrentSelectionsFromExplorer();
		return checkForMetric(selectedFiles);
	}
}
