package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;
@Creatable
@Singleton
public class POUActionRemover extends POUActionMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		List<POUImpl> pousWithActions = getPOUsWithActions(mutCtx);
		
		if (!pousWithActions.isEmpty()) {
			Action actionToRemove = getRandomAction(getRandomPOU(pousWithActions));
			if (actionToRemove != null) {
				mutCtx.logRemoval(actionToRemove);
				mutCtx.getCtxObjects().remove(actionToRemove);
				EcoreUtil.delete(actionToRemove, true);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	

	
	
	
	
	

}
