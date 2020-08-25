package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;

public interface IActionAttribute {
	
	public ResultElement<Action> compare(Action action1, Action action2);
}
