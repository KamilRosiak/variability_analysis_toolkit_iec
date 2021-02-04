package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.google.common.collect.Lists;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators.VariableGenerator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Declaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;

@Creatable
@Singleton
public class POUVariableInserter extends VariableMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {

		POUImpl pouToInsert = getRandomPOU(getRandomizedPOUs(mutCtx));

		if (pouToInsert != null) {
			Variable varToInsert = VariableGenerator.generateVariable();

			getRandomVarList(pouToInsert).add(varToInsert);
			mutCtx.logInsertion(pouToInsert, varToInsert);

			return true;

		} else {
			return false;
		}
	}

	/**
	 * Returns a random var list from a pou , e.g, internal , in and out variables.
	 * 
	 * @param pou
	 * @return
	 */
	private List<Variable> getRandomVarList(POUImpl pou) {
		Declaration decl = pou.getDeclaration();
		List<List<Variable>> varLists = new ArrayList<List<Variable>>(Lists.newArrayList(decl.getInOutVariables(),
				decl.getInputVariables(), decl.getInternalVariables(), decl.getOutputVariables()));
		Random r = new Randomization();
		return varLists.get(r.nextInt(varLists.size()));
	}

}
