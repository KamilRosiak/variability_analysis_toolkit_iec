package de.tu_bs.cs.isf.familymining.ppu_iec.core.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import FeatureDiagram.FeatureDiagramm;
import de.tu_bs.cs.isf.e4cf.core.stringtable.E4CStringTable;
import de.tu_bs.cs.isf.e4cf.core.transform.Transformation;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.e4cf.core.util.services.RCPSelectionService;
import de.tu_bs.cs.isf.e4cf.featuremodel.core.string_table.FDEventTable;
import de.tu_bs.cs.isf.e4cf.featuremodel.core.string_table.FDStringTable;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.SolutionSerializer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.transformation.ConfigurationResultToFeatureModelTransform;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.util.FamilyModelTransformUtil;

public class GenerateFeatureDiagrammFromResultHandler {
	
	@Execute
	public void generateFeatureDiagram(ServiceContainer services) {
		services.partService.showPart(FDStringTable.BUNDLE_NAME);
		
		String filePath = services.rcpSelectionService.getCurrentSelectionFromExplorer().getAbsolutePath();
		
		ConfigurationResultRoot result = SolutionSerializer.decode(filePath);
		
		Transformation<FeatureDiagramm> familyToFeatureModel = new ConfigurationResultToFeatureModelTransform();
		services.eventBroker.send(FDEventTable.LOAD_FEATURE_DIAGRAM, familyToFeatureModel.apply(result));
		services.eventBroker.send(FDEventTable.FORMAT_DIAGRAM_EVENT, "");	
	}
	
	@CanExecute
	public boolean canExecute(RCPSelectionService selectionService) {
		if(selectionService.getCurrentSelectionFromExplorer() != null) {
			if(selectionService.getCurrentSelectionFromExplorer().isDirectory()) {
				return false;
			}
		} else {
			return false;
		}

		return selectionService.getCurrentSelectionFromExplorer().getExtension().equals(E4CStringTable.FILE_ENDING_FAMILY_MODEL);
	}
}
