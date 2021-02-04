package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators.ActionGenerator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;
@Creatable
@Singleton
public class POUActionInserter extends POUActionMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		List<POUImpl> pous = getRandomizedPOUs(mutCtx);
		
		if (!pous.isEmpty()) {
			POUImpl pouToInsert = getRandomPOU(pous);
			Action actionToInsert = ActionGenerator.generateAction();
			
			if (actionToInsert != null) {
				pouToInsert.getActions().add(actionToInsert);

				mutCtx.logInsertion(pouToInsert, actionToInsert);
				mutCtx.getCtxObjects().add(actionToInsert);
				mutCtx.setChangedTreeStructure(true);

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	

	
	
	
	
	

}
