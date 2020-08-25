package de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.tu_bs.cs.isf.e4cf.core.file_structure.util.Pair;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.PreferencesUtil;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.key_value.KeyValueNode;
import de.tu_bs.cs.isf.e4cf.core.stringtable.E4CStringTable;
import de.tu_bs.cs.isf.e4cf.core.util.RCPContentProvider;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.e4cf.graph.core.string_table.GraphEvents;
import de.tu_bs.cs.isf.e4cf.graph.core.string_table.GraphStringTable;
import de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction.util.ModelTransformUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction.util.ProjectCompareUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction.view.ExtractionView;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.model.ModelCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.model.ModelImplementaionOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou.POUCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.SolutionSerializer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.DefaultSettingContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.PPUEventTable;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.PPUStringTable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Resource;
import de.tu_bs.cs.isf.familymining.ppu_iec.rcp_e4.familymodel_view.string_table.FMStringTable;
import javafx.embed.swt.FXCanvas;

public class ExtractionViewController {
	private ServiceContainer services;
	private ExtractionView view;
	private String modelName = "";
	
	private List<POUCompareContainer> currentComparisonResults;
	
	@PostConstruct
	public void createControll(Composite parent, ServiceContainer services) {
		this.setServices(services);
		FXCanvas canvas = new FXCanvas(parent, SWT.NONE);
		view = new ExtractionView(canvas, this);
		this.currentComparisonResults = new ArrayList<>();
	}
	
	public ServiceContainer getServices() {
		return services;
	}

	public void setServices(ServiceContainer services) {
		this.services = services;
	}	

	@Inject
	@Optional
	public void showProjectCompareView(@UIEventTopic(PPUEventTable.PROJECT_COMPARE_EVENT) List<Configuration> models) {
		modelName = "";
		for(Configuration config : models) {
			modelName += config.getIdentifier() +" ";
		}
		view.showView(models);
	}
	
	@Inject
	@Optional
	public void comparePOUs(@UIEventTopic(PPUEventTable.COMPARE_POU_EVENT) List<POU> pous) {
		KeyValueNode weightedNode = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, DefaultSettingContribution.DEFAULT_WEIGHTED_PREF, "false");
		
		//Setting the compare mode of the preferences
		view.getBottomBar().getMetric().setWeighted(weightedNode.getBoolValue());
		
		
		currentComparisonResults = ProjectCompareUtil.comparePOUs(pous, view.getBottomBar().getMetric(), view.getBottomBar().getMatcher());
		//Filters the container that has a similarity that is smaller than the threshold.
		currentComparisonResults = currentComparisonResults.stream()
		.filter(container -> container.getSimilarity() >= view.getBottomBar().getThreshold())
		.collect(Collectors.toList());
		
		
		if(view.getToolBar().isStoreActive()) {
			storePOUContainer(currentComparisonResults);	
		}
		
		ProjectCompareUtil.createCSV(currentComparisonResults, modelName+"_compare_results");
		ProjectCompareUtil.findGroups(currentComparisonResults);
		services.partService.showPart(GraphStringTable.GRAPH_VIEW);
		services.eventBroker.send(GraphEvents.LOAD_GRAPH_MODEL, ModelTransformUtil.transformGraphModel(currentComparisonResults));
	}
	
	/**
	 * This method stores a family model of a POUContainer
	 * @param results
	 */
	private void storePOUContainer(List<POUCompareContainer> results) {
		for(POUCompareContainer container : results) {
			ConfigurationResultRoot root = buildConfgurationResultRoot(container);
			SolutionSerializer.encode(root, RCPContentProvider.getCurrentWorkspacePath() + E4CStringTable.FAMILY_MODEL_DIRECTORY,E4CStringTable.FILE_ENDING_FAMILY_MODEL, true);	
		}
	}

	private ConfigurationResultRoot buildConfgurationResultRoot(POUCompareContainer container) {
		ConfigurationResultRoot root = new ConfigurationResultRoot(view.getBottomBar().getMetric());
		
		Configuration configA = ConfigurationFactory.eINSTANCE.createConfiguration();
		configA.setIdentifier(container.getLeftLabel());
		Resource resourceA = ConfigurationFactory.eINSTANCE.createResource();
		resourceA.setName(container.getLeftLabel());
		configA.getResources().add(resourceA);
		
		Configuration configB = ConfigurationFactory.eINSTANCE.createConfiguration();
		configB.setIdentifier(container.getRightLabel());
		Resource resourceB = ConfigurationFactory.eINSTANCE.createResource();
		resourceB.setName(container.getRightLabel());
		configB.getResources().add(resourceB);

		ModelCompareContainer modelCont = new ModelCompareContainer(configA,configB, view.getBottomBar().getMetric());
		root.addChildren(modelCont);
		ModelImplementaionOption mImplOpt = new ModelImplementaionOption(view.getBottomBar().getMetric(), view.getBottomBar().getMetric().getModelImplementationOptionAttr());
		modelCont.setModelImplementationOption(mImplOpt);
		mImplOpt.addContainer(container);
		return root;
	}
	
	@Inject
	@Optional
	public void showPOUContainerAsFamilyModel(@UIEventTopic(PPUEventTable.SHOW_POU_PAIR_AS_FAMILY_MODEL) Pair<String, String> pouPair) {
		for (POUCompareContainer pouContainer: currentComparisonResults) {
			String pouName1 = pouContainer.getFirst().getIdentifier();
			String pouName2 = pouContainer.getSecond().getIdentifier();
			
			if ((pouName1.equals(pouPair.first) && pouName2.equals(pouPair.second)) ||
				(pouName1.equals(pouPair.second) && pouName2.equals(pouPair.first))) {
				ConfigurationResultRoot resultRoot = buildConfgurationResultRoot(pouContainer);
				
				services.partService.showPart(FMStringTable.PART_NAME);
				services.eventBroker.send(PPUEventTable.RESULT_EVENT, resultRoot);
			}
		}
	}
	
}
