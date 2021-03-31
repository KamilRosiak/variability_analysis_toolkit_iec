package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;

/**
 * This Mutator removes one POU at random.
 * 
 * @author Kamil Rosiak
 *
 */
@Creatable
@Singleton
public class POURemover extends POUMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		List<POUImpl> randomizedPOUContainers = getRandomizedPOUs(mutCtx);
	
		if (!randomizedPOUContainers.isEmpty()) {
			POUImpl pouToBeRemoved = getRandomPOU(randomizedPOUContainers);
			if (pouToBeRemoved != null) {
				mutCtx.logRemoval(pouToBeRemoved);
				mutCtx.getCtxObjects().remove(pouToBeRemoved);
				EcoreUtil.delete(pouToBeRemoved, true);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


}
