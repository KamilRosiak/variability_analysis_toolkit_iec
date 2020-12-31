package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class MutationContext {

	List<EObject> ctxObjects;

	public List<EObject> getCtxObjects() {
		return ctxObjects;
	}

	public void setCtxObjects(List<EObject> ctxObjects) {
		this.ctxObjects = ctxObjects;
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
		} else if (!EcoreUtil.equals(ctxObjects, other.ctxObjects))
			return false;
		return true;
	}

	/**
	 * Deep copy of mutation context.<br><br>
	 * <b>Note</b>: copies the entire containment tree of eobjects.
	 */
	@Override
	public MutationContext clone() {
		MutationContext clone = new MutationContext();
		clone.setCtxObjects(ctxObjects.stream().map(EcoreUtil::copy).collect(Collectors.toList()));

		return clone;
	}

}
