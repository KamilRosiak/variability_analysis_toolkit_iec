package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

/**
 * EObject Insertion:
 *  - oldValue is null, newValue is new eobject, attribute is null and ctxObject is a
 * 
 * @author Oliver Urbaniak
 *
 * @param <T>
 */
public class MutationDelta<T> {

	/**
	 * Associates mutation context with mutation operations
	 */
	private long ctxKey;
	
	private EObject ctxObject;
	private EAttribute attr;
	private T oldValue; 
	private T newValue;
}
