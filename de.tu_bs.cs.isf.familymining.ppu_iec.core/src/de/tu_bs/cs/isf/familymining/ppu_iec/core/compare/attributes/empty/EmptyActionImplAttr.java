package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.attributes.empty;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.templates.AbstractActionAttribute;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;

public class EmptyActionImplAttr extends  AbstractActionAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 119095714130805799L;
	public static final String ATTRIBUTE_NAME = "Empty Implementation Attribute";
	public static final String ATTRIBUTE_DESCRIPTION = "If Action has no implementation.";
	
	public EmptyActionImplAttr() {
		super(ATTRIBUTE_NAME, ATTRIBUTE_DESCRIPTION);
	}

	@Override
	public ResultElement<Action> compare(Action action1, Action action2) {
		return new ResultElement<Action>(action1,action2,1.0f,this) ;
	}
}
