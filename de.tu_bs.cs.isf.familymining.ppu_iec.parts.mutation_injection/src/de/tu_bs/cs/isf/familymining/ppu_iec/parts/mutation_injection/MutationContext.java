package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class MutationContext {

	List<EObject> ctxObjects;

	public List<EObject> getCtxObjects() {
		return ctxObjects;
	}

	public void setCtxObjects(List<EObject> ctxObjects) {
		this.ctxObjects = ctxObjects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ctxObjects == null) ? 0 : ctxObjects.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutationContext other = (MutationContext) obj;
		if (ctxObjects == null) {
			if (other.ctxObjects != null)
				return false;
		} else if (!ctxObjects.equals(other.ctxObjects))
			return false;
		return true;
	}
	
}
