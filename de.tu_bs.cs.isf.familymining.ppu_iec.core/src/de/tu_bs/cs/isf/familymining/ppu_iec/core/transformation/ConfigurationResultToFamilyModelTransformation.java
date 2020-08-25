package de.tu_bs.cs.isf.familymining.ppu_iec.core.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.model.FamilyModel.FamilyModel;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.model.FamilyModel.VariabilityCategory;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.model.FamilyModel.Variant;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.model.FamilyModel.VariantArtefact;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.model.FamilyModel.VariationPoint;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.util.FamilyModelBuilder;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.util.FamilyModelTransformation;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.model.ModelCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.util.VariabilityThresholdsUtil;

public class ConfigurationResultToFamilyModelTransformation extends AbstractIECTransformation<FamilyModel, VariationPoint> implements FamilyModelTransformation {

	private static final Variant[] EMPTY_VARIANT_ARRAY = new Variant[0];
	private static final VariationPoint[] EMPTY_VAR_POINT_ARRAY = new VariationPoint[0];
	private static final VariantArtefact[] EMPTY_VAR_ARTEFACT_ARRAY = new VariantArtefact[0];
	
	private FamilyModelBuilder fmBuilder;
	private FamilyModel familyModel;

	public ConfigurationResultToFamilyModelTransformation() {
		fmBuilder = new FamilyModelBuilder();
	}

	@Override
	public FamilyModel apply(Object object) {
		if (!(object instanceof ConfigurationResultRoot)) {
			return null;
		}
		
		ConfigurationResultRoot config = (ConfigurationResultRoot) object;
		
		// construct name from models
		List<ModelCompareContainer> modelContainers = config.getChildren();
		
		if (modelContainers.size() == 0) {
			return fmBuilder.createFamilyModel("EMPTY_FAMILY_MODEL", EMPTY_VAR_POINT_ARRAY, EMPTY_VARIANT_ARRAY);
		} else if (modelContainers.size() == 1) {
			ModelCompareContainer modelContainer = modelContainers.get(0);
			
			// create feature model root 
			String fmName = modelContainer.getLeftLabel() + "_-_" + modelContainer.getRightLabel();
			
			Variant firstVariant = fmBuilder.createVariant(modelContainer.getLeftLabel(), modelContainer.getFirst());
			Variant secondVariant = fmBuilder.createVariant(modelContainer.getRightLabel(), modelContainer.getSecond());
			
			familyModel = fmBuilder.createFamilyModel(fmName, EMPTY_VAR_POINT_ARRAY, new Variant[] {firstVariant, secondVariant});
			
			// process containers/options and build variationPoint hierarchy
			VariationPoint dummyVarPointRoot = 
					fmBuilder.createVariationPoint("dummy", VariabilityCategory.UNSET, EMPTY_VAR_ARTEFACT_ARRAY, EMPTY_VAR_POINT_ARRAY, null);
			
			processOption(modelContainer.getModelImplementationOption(), dummyVarPointRoot);
			processOption(modelContainer.getModelVariableOption(), dummyVarPointRoot);
			
			familyModel.getVariationPoints().addAll(dummyVarPointRoot.getChildren());
			
			EcoreUtil.delete(dummyVarPointRoot);
			
			return familyModel;
		} else {
			throw new RuntimeException("Not supported yet");
		}
	}
	
	@Override
	protected <T extends EObject> VariationPoint connect(IECAbstractContainer<T> container, VariationPoint parent) {
		// create the variant artefacts 
		List<VariantArtefact> varArtefacts = new ArrayList<>();
		
		T firstArtefact = container.getFirst();
		if (firstArtefact != null) {
			Variant firstOrigin = getOrigin(firstArtefact);
			VariantArtefact firstVarArtefact = fmBuilder.createVariantArtefact(container.getLeftLabel(), toArray(firstArtefact), toArray(firstOrigin));
			varArtefacts.add(firstVarArtefact);
		}
		
		T secondArtefact = container.getSecond();
		if (secondArtefact != null) {
			Variant secondOrigin = getOrigin(secondArtefact);
			VariantArtefact secondVarArtefact = fmBuilder.createVariantArtefact(container.getRightLabel(), toArray(secondArtefact), toArray(secondOrigin));
			varArtefacts.add(secondVarArtefact);
		}
		
		VariantArtefact[] varArtefactArray = new VariantArtefact[varArtefacts.size()];
		for (int i = 0; i < varArtefactArray.length; i++) {
			varArtefactArray[i] = varArtefacts.get(i);
		}
		
		// create the variation point
		VariationPoint varPoint = 
				fmBuilder.createVariationPoint(getLabel(container), classify(container), varArtefactArray, EMPTY_VAR_POINT_ARRAY, parent);
		
		return varPoint;
	}

	@Override
	protected <T extends IECAbstractContainer<?>> VariationPoint connect(IECAbstractOption<T> option, VariationPoint parent) {
		VariationPoint varPoint = fmBuilder.createVariationPoint(getLabel(option), VariabilityCategory.UNSET, EMPTY_VAR_ARTEFACT_ARRAY, EMPTY_VAR_POINT_ARRAY, parent);
		return varPoint;
	}

	@Override
	protected VariationPoint connect(String nodeName, VariationPoint parent) {
		VariationPoint varPoint = fmBuilder.createVariationPoint(nodeName, VariabilityCategory.UNSET, EMPTY_VAR_ARTEFACT_ARRAY, EMPTY_VAR_POINT_ARRAY, parent);
		return varPoint;
	}

	/**
	 * Assigns a variability category to the object based on its similarity value.
	 * 
	 * @param similarity
	 * @return
	 */
	protected VariabilityCategory classify(Object object) {		
		if (VariabilityThresholdsUtil.isOptional(object)) {
			return VariabilityCategory.OPTIONAL;
		} else if (VariabilityThresholdsUtil.isMandatory(object)) {
			return VariabilityCategory.MANDATORY;
		} else {
			return VariabilityCategory.ALTERNATIVE;
		}
	}

	/**
	 * Determines the origin of an EObject. There is exactly one origin per EObject.
	 * 
	 * @param eobject
	 * @return The variant associated with the EObject or <tt>null</tt> if there is none.
	 */
	protected Variant getOrigin(EObject eobject) {
		List<Variant> containingVariants =  familyModel.getVariants().stream()
				.filter((variant) -> EcoreUtil.isAncestor(variant.getInstance(), eobject))
				.collect(Collectors.toList());
		if (containingVariants.isEmpty()) {
			return null;
		} else if (containingVariants.size() == 1) {
			return containingVariants.get(0);
		} else {
			throw new RuntimeException("Eobjects can only have one origin variant.");			
		}
	}
	
	@SafeVarargs
	@SuppressWarnings("unchecked")
	protected final <T> T[] toArray(T... ts) {
		return ts;
	}
	
	@Override
	public boolean canTransform(Object object) {
		return object != null && object instanceof ConfigurationResultRoot;
	}
}
