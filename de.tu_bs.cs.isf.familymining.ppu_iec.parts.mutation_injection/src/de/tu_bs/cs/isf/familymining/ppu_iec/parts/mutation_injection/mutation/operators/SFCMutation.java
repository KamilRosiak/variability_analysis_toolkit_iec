package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.impl.SequentialFunctionChartImpl;
/**
 * Base class for the implementation of sequential function chart mutation operations
 * @author Kamil Rosiak
 *
 */
public abstract class SFCMutation implements Mutation{
	private Random r = new Randomization();
	
	/**
	 * Returns all SequentialFunctionChartImpl from the mutation context.
	 */
	protected List<SequentialFunctionChartImpl> getAllSFCImplementations(MutationContext ctx){
		return ctx.getCtxObjects().stream().filter(e -> (e instanceof SequentialFunctionChartImpl)).map(e->(SequentialFunctionChartImpl)e).collect(Collectors.toList());	
	}
	
	/**
	 * This method returns a random SFC Implementation from the mutation context if exists else null.
	 */
	protected SequentialFunctionChartImpl getRandomSFCImpl(MutationContext ctx) {
		if(getAllSFCImplementations(ctx).isEmpty()) {
			return null;
		}
		return getRandomSFCImpl(getAllSFCImplementations(ctx));
	}
	
	/**
	 * This method returns a random SFC Implementation from a list of SFC Implementations.
	 */
	protected SequentialFunctionChartImpl getRandomSFCImpl(List<SequentialFunctionChartImpl> sfcs) {
		if(sfcs.isEmpty()) {
			return null;
		}
		return sfcs.get(r.nextInt(sfcs.size()));
	}
	/**
	 * Returns a random step from a SFC implementation.
	 */
	protected Step getARandomStepFromSFC(SequentialFunctionChartImpl sfc) {
		return sfc.getSteps().get(r.nextInt(sfc.getSteps().size()));
	}
	
	
}
