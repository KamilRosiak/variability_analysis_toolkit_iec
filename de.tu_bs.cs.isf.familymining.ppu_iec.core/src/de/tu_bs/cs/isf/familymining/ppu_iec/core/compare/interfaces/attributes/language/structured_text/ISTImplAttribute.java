package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes.language.structured_text;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;

public interface ISTImplAttribute {
	/**
	 * This method is called by the framework during the comparison process and compares two Statements with each other.
	 */
	public ResultElement<Statement> compare(Statement source, Statement target);
}
