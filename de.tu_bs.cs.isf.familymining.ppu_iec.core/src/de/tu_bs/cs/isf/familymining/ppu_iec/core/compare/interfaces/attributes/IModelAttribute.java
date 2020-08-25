package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public interface IModelAttribute {
	public ResultElement<Configuration> compare(Configuration modell1, Configuration modell2);
}
