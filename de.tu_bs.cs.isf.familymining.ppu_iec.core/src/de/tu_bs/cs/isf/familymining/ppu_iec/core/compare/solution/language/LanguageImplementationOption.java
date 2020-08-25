package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.OptionAttribute;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;

public class LanguageImplementationOption<ContainerType extends AbstractLanguageContainer<?>> extends IECAbstractOption<ContainerType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9004140654648074565L;

	public LanguageImplementationOption(MetricContainer metric, OptionAttribute optAttr) {
		super(metric, optAttr);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}
}
