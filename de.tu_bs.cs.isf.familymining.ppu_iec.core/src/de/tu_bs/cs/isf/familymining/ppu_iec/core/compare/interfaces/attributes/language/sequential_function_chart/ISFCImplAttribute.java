package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes.language.sequential_function_chart;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;

public interface ISFCImplAttribute {
	/**
	 * This method is called by the framework during the comparison process and compares two Steps with each other.
	 */
	public ResultElement<Step> compare(Step source, Step target);
}
