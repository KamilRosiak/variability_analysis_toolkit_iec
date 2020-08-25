package de.tu_bs.cs.isf.familymining.ppu_iec.core.util;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractOption;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.PreferencesUtil;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.key_value.KeyValueNode;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.action.ActionCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.AbstractLanguageContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.ld.DiagramContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.ld.implementation.DiagramElementImplContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.ld.implementation.FBDNetworkImplContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.ld.implementation.LDNetworkImplContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.sfc.SFCLangContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.sfc.implementation.SFCImplContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.st.STLangContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.st.implementation.STImplContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou.POUCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.EnableGranularThresholds;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.MandatoryValueActionContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.MandatoryValueFBDContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.MandatoryValueLDContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.MandatoryValuePOUContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.MandatoryValueSFCContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.MandatoryValueSTContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.VariabilityThresholdsContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.PPUStringTable;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.VariabilityCategoryThresholds;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.functionblockdiagram.FBDElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.ladderdiagram.LLElement;

/**
 * This utility classes provides all thresholds from preferences.
 * @author {Kamil Rosiak}
 *
 */
public abstract class VariabilityThresholdsUtil {
	
	/**
	 * This method returns true if the "enable custom thresholds" option in the preferences is selected"
	 */
	public static boolean isGranularTH() {
		KeyValueNode enableGranularThresholds = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, EnableGranularThresholds.ENABLE_GRANULAR_THRESHOLDS,"false");
		return enableGranularThresholds.getBoolValue();
	}
	
	/**
	 * This method returns the general mandatory value.
	 */
	public static float getMandatoryValue() {
		KeyValueNode mandatoryValueGeneral = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, VariabilityThresholdsContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValueGeneral.getFloatValue();
	}
	
	/**
	 * This method returns the general optional value.
	 */
	public static float getOptionalValue() {
		KeyValueNode mandatoryValueGeneral = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, VariabilityThresholdsContribution.OPTIONAL_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValueGeneral.getFloatValue();
	}
	/**
	 * This method returns the specific pou mandatory value.
	 */
	public static float getMandatoryValuePOU() {
		KeyValueNode mandatoryValuePOU = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, MandatoryValuePOUContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.POU_MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValuePOU.getFloatValue();
	}
	
	/**
	 * This method returns the action mandatory value.
	 */
	public static float getMandatoryValueAction() {
		KeyValueNode mandatoryValueAction = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, MandatoryValueActionContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.ACTION_MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValueAction.getFloatValue();
	}	
	
	/**
	 * This method returns the ST mandatory value.
	 */
	public static float getMandatoryValueSTImpl() {
		KeyValueNode mandatoryValueAction = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, MandatoryValueSTContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.ST_MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValueAction.getFloatValue();
	}	
	
	/**
	 * This method returns the SFC mandatory value.
	 */
	public static float getMandatoryValueSFCImpl() {
		KeyValueNode mandatoryValueAction = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, MandatoryValueSFCContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.SFC_MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValueAction.getFloatValue();
	}
	
	/**
	 * This method returns the SFC mandatory value.
	 */
	public static float getMandatoryValueLDImpl() {
		KeyValueNode mandatoryValue = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, MandatoryValueLDContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.LD_MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValue.getFloatValue();
	}
	
	/**
	 * This method returns the FBD mandatory value.
	 */
	public static float getMandatoryValueFBDImpl() {
		KeyValueNode mandatoryValue = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, MandatoryValueFBDContribution.MANDATORY_VALUE_PREF,String.valueOf(VariabilityCategoryThresholds.FBD_MANDATORY_DEFAULT_THRESHOLD));
		return mandatoryValue.getFloatValue();
	}
	
	public static <T extends EObject,MetricType> boolean isMandatory(Object object) {
		float mandatoryValue = 0.0f;
		if(VariabilityThresholdsUtil.isGranularTH()) {
			if(object instanceof ActionCompareContainer) {
				mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueAction();
			} else if(object instanceof POUCompareContainer) {
				mandatoryValue = VariabilityThresholdsUtil.getMandatoryValuePOU();
			} else if(object instanceof AbstractLanguageContainer) {
				AbstractLanguageContainer<?> langContainer = (AbstractLanguageContainer<?>) object;
				if(langContainer.getFirst() != null) {
					mandatoryValue = getValueForLang(langContainer);
				} else if(langContainer.getSecond() != null) {
					mandatoryValue = getValueForLang(langContainer);
				}
			} else {
				mandatoryValue = VariabilityThresholdsUtil.getMandatoryValue();
			}
		} else {
			mandatoryValue = VariabilityThresholdsUtil.getMandatoryValue();
		}

		if (object instanceof AbstractContainer<?, ?>) {
			AbstractContainer<?, ?> container = (AbstractContainer<?, ?>) object; 
			return container.getSimilarity() >= (mandatoryValue / 100);
		} else if (object instanceof AbstractOption<?, ?>) {
			AbstractOption<?, ?> option = (AbstractOption<?, ?>) object;
			return option.getSimilarity() >= (mandatoryValue / 100);
		} else {
			throw new RuntimeException("The object must be of type AbstractContainer or AbstractOption.");
		}
		
	}
	
	public static <T extends EObject,MetricType> boolean isOptional(Object object) {
		if (object instanceof AbstractContainer<?, ?>) {
			AbstractContainer<?, ?> container = (AbstractContainer<?, ?>) object; 
			return container.getSimilarity() <= (VariabilityThresholdsUtil.getOptionalValue()/100);
		} else if (object instanceof AbstractOption<?, ?>) {
			AbstractOption<?, ?> option = (AbstractOption<?, ?>) object;
			return option.getSimilarity() <= (VariabilityThresholdsUtil.getOptionalValue() / 100);
		} else {
			throw new RuntimeException("The object must be of type AbstractContainer or AbstractOption.");
		} 
	}
	
	public static <T extends EObject,MetricType> boolean isAlternative(Object object) {
		return !isOptional(object) && !isMandatory(object);
	}
	
	/**
	 * This method returns the mandatory value for the given LanguageElement based on his type.
	 */
	private static float getValueForLang(AbstractLanguageContainer<?> langContainer){
		float mandatoryValue = 0.0f;
		if(langContainer instanceof STLangContainer || langContainer instanceof STImplContainer) {
			mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueSTImpl();
		} else if(langContainer instanceof SFCLangContainer || langContainer instanceof SFCImplContainer) {
			mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueSFCImpl();
		} else if(langContainer instanceof DiagramContainer || langContainer instanceof LDNetworkImplContainer){
			mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueLDImpl();
		} else if(langContainer instanceof FBDNetworkImplContainer) {
			mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueFBDImpl();
		} else if(langContainer instanceof DiagramElementImplContainer) {
			DiagramElementImplContainer diagramCont = (DiagramElementImplContainer)langContainer;
			if(diagramCont.getFirst() != null ) {
				if(diagramCont.getFirst() instanceof LLElement) {
					mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueLDImpl();
				} else if(diagramCont.getFirst() instanceof FBDElement) {
					mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueLDImpl();
				}
			} else if(diagramCont.getSecond() != null) {
				if(diagramCont.getFirst() instanceof LLElement) {
					mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueLDImpl();
				} else if(diagramCont.getFirst() instanceof FBDElement) {
					mandatoryValue = VariabilityThresholdsUtil.getMandatoryValueLDImpl();
				}
			}
		}
		return mandatoryValue;
	}
}
