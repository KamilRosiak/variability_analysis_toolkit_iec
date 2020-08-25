package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.sfc.implementation;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.OptionAttribute;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;

public class SFCActionOption<K extends EObject> extends IECAbstractOption<IECAbstractContainer<K>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3464826065621551281L;

	public SFCActionOption(MetricContainer metric, OptionAttribute optionAttr) {
		super(metric, optionAttr);
	}

	@Override
	public String getLabel() {
		return "SFC ACTION";
	}

}
