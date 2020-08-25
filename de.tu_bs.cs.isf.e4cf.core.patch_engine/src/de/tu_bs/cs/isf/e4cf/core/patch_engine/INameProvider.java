package de.tu_bs.cs.isf.e4cf.core.patch_engine;

import org.eclipse.emf.ecore.EObject;

public interface INameProvider {
	
	public class DefaultNameProvider implements INameProvider {
		public String getName(EObject object) {
			return object.eClass().getName();
		}
	};
	
	/**
	 * Returns a textual representation of <i>object</i>.
	 * 
	 * @param object
	 * @return name or null, if object is null
	 */
	public String getName(EObject object);
}
