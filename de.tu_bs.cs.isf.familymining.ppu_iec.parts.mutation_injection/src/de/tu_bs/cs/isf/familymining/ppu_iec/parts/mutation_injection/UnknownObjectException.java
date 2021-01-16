package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import org.eclipse.emf.ecore.EObject;

public class UnknownObjectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownObjectException(EObject object, MutationContext mutCtx) {
		super(errorMessage(object, mutCtx));
	}

	public UnknownObjectException(String message) {
		super(message);
	}

	private static String errorMessage(EObject object, MutationContext mutCtx) {
		return String.format("Object \"%s\" isn't part of the mutation context \"%s\".", object, mutCtx.getCtxObjects());
	}
	
}
