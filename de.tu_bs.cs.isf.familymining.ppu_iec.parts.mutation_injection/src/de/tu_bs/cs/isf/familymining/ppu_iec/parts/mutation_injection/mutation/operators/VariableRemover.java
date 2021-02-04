package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.VariableImpl;
/**
 * Removes a random variable
 * @author NoLimit
 *
 */
@Creatable
@Singleton
public class VariableRemover extends VariableMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		List<VariableImpl> contextVariables = getRandomizedVariables(mutCtx);
		if (!contextVariables.isEmpty()) {
			VariableImpl varToRemove = getRandomVariable(contextVariables);
			
			if (varToRemove != null) {
				mutCtx.logRemoval(varToRemove);
				mutCtx.getCtxObjects().remove(varToRemove);
				EcoreUtil.delete(varToRemove, true);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
