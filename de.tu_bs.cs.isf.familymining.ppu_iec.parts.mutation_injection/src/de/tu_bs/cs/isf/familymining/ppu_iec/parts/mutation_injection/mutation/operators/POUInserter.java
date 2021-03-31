package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators.POUGenerator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Resource;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;

/**
 * This Mutation operation inserts a random generated POU
 * @author Kamil Rosiak
 *
 */
@Creatable
@Singleton
public class POUInserter extends POUMutation {

	@Override
	public Boolean apply(MutationContext mutCtx) {
		List<POUImpl> pou = getRandomizedPOUs(mutCtx);
		
		if(!pou.isEmpty()) {
			Resource res = getResource((Configuration)EcoreUtil.getRootContainer(pou.get(0)));
			POU generatedPOU = POUGenerator.generateRandomPOU();
			res.getPous().add(generatedPOU);
			
			mutCtx.logInsertion(res, generatedPOU);
			mutCtx.setChangedTreeStructure(true);
			return true;
		} else {
			return false;
		}
	}
}
