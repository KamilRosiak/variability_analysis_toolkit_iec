package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;

public interface ICompare {
	
	public ResultElement<? extends EObject> compare(EObject first, EObject second);
}
