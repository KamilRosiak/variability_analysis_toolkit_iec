package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes.language.function_block_diagram;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.DiagramElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.functionblockdiagram.FBDElement;

public interface IFBDImplAttribute {
	/*
	 * This method compare two function blocks and returns a DiagramImplResult that represents a bind in form of a similarity value between those blocks. 
	 */
	public ResultElement<DiagramElement> compare(FBDElement first, FBDElement second);
}
