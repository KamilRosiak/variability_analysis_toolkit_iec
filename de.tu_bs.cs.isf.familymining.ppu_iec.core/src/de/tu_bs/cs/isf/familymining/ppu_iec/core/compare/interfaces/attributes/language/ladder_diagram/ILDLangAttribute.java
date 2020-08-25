package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes.language.ladder_diagram;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Diagram;

public interface ILDLangAttribute {
	/**
	 * This method is called by the framework during the comparison process and compares two Ladder Diagrams with each other.
	 */
	public ResultElement<Diagram> compare(Diagram source, Diagram target);
}
