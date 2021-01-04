package de.tu_bs.cs.isf.familymining.ppu_iec.core.transformation;

import org.eclipse.emf.ecore.EObject;

import FeatureDiagram.ArtifactReference;
import FeatureDiagram.Feature;
import FeatureDiagram.FeatureDiagramFactory;
import FeatureDiagram.FeatureDiagramm;
import FeatureDiagram.GraphicalFeature;
import FeatureDiagram.impl.FeatureDiagramFactoryImpl;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.model.ModelCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.util.VariabilityThresholdsUtil;

public class ConfigurationResultToFeatureModelTransform extends AbstractIECTransformation<FeatureDiagramm, Feature> {
	
	private FeatureDiagramm diagram;
	private ConfigurationResultRoot configResultRoot;

	@Override
	public FeatureDiagramm apply(Object configResultRootObject) {
		if (configResultRootObject instanceof ConfigurationResultRoot) {
			
			configResultRoot = (ConfigurationResultRoot) configResultRootObject;
			diagram = FeatureDiagramFactory.eINSTANCE.createFeatureDiagramm();
			
			ModelCompareContainer modelContainer = configResultRoot.getChildren().get(0);
			
			String sourceName = "";
			String targetName = "";
			if(!modelContainer.getResults().isEmpty()) {
				if(modelContainer.getResults().get(0).getFirst() != null) {
					sourceName = modelContainer.getResults().get(0).getFirst().getIdentifier();
				}
				
				if(modelContainer.getResults().get(0).getFirst() != null) {
					targetName = modelContainer.getResults().get(0).getSecond().getIdentifier();
				}
			}
			
			String rootName = sourceName +"_"+targetName+"_FeatureDiagram";
			
			Feature root = createFeature(rootName, VariabilityThresholdsUtil.isMandatory(modelContainer), VariabilityThresholdsUtil.isAlternative(modelContainer));
			root.setMandatory(true);
			
			processOption(modelContainer.getModelImplementationOption(), root);
			processOption(modelContainer.getModelVariableOption(), root);
			
			diagram.setRoot(root);
			return diagram;
		}
		return null;
	}
	
	@Override
	protected <T extends EObject> Feature connect(IECAbstractContainer<T> container, Feature parent) {
		Feature feature = createFeature(container);
		feature.setParent(parent);
		parent.getChildren().add(feature);
		return feature;
	}


	@Override
	protected <T extends IECAbstractContainer<?>> Feature connect(IECAbstractOption<T> option, Feature parent) {
		Feature feature = createFeature(option);
		feature.setParent(parent);
		parent.getChildren().add(feature);
		return feature;
	}


	@Override
	protected Feature connect(String nodeName, Feature parent) {
		Feature feature = createFeature(nodeName, true, false);
		parent.getChildren().add(feature);
		feature.setParent(parent);
		return feature;
	}
	
	public <T extends EObject> Feature createFeature(IECAbstractContainer<T> container) {
		String featureLabel = getLabel(container);
		return createFeature(featureLabel, VariabilityThresholdsUtil.isMandatory(container),
				VariabilityThresholdsUtil.isAlternative(container));
	}

	public <T extends AbstractContainer<?, MetricContainer>> Feature createFeature(IECAbstractOption<T> option) {
		String featureLabel = getLabel(option);
		return createFeature(featureLabel, VariabilityThresholdsUtil.isMandatory(option),
				VariabilityThresholdsUtil.isAlternative(option));
	}

	/**
	 * This method creates a new feature. With default name
	 */
	public Feature createFeature(String featureName, boolean mandatory, boolean alternative) {
		Feature feature = FeatureDiagramFactoryImpl.eINSTANCE.createFeature();
		feature.setName(featureName);
		feature.setMandatory(mandatory); 
		feature.setAlternative(false);
		feature.setOr(alternative); // TODO: reevaluate this distinction between ALTERNATIVE and OR
		feature.setAbstract(false);

		diagram.setIdentifierIncrement(diagram.getIdentifierIncrement() + 1);
		feature.setIdentifier(diagram.getIdentifierIncrement());

		GraphicalFeature graphicalFeature = FeatureDiagramFactory.eINSTANCE.createGraphicalFeature();
		feature.setGraphicalfeature(graphicalFeature);

		ArtifactReference artifactReference = FeatureDiagramFactoryImpl.eINSTANCE.createArtifactReference();
		artifactReference.setArtifactClearName(feature.getName());
		feature.getArtifactReferences().add(artifactReference);
		return feature;
	}
	
	@Override
	public boolean canTransform(Object object) {
		return object != null && object instanceof ConfigurationResultRoot;
	}
}
