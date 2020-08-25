package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes;


import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;

public interface IVariableAttribute {
	public ResultElement<Variable> compare(Variable sourceVar, Variable targetVar, String varType);
}
