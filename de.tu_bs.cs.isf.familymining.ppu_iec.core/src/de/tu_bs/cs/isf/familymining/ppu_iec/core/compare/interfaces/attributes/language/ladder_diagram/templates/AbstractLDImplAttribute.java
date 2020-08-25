package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes.language.ladder_diagram.templates;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractAttribute;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.interfaces.attributes.language.ladder_diagram.ILDImplAttribute;

/**
 * Template class for a ILDLangAttribute. Just implement the compare method and create an extension for the ILDImplAttribute extension point.
 * The attribute will be added to the configuration manager where its possible to add it to the configuration. 
 * @author {Kamil Rosiak}
 *
 */
public abstract class AbstractLDImplAttribute extends AbstractAttribute implements ILDImplAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6267435015274963455L;

	public AbstractLDImplAttribute(String attrName, String attrDescription) {
		super(attrName, attrDescription);
	}
}
