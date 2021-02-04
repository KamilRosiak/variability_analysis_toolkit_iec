package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.List;
import java.util.stream.Collectors;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;

/**
 * Base class for operations on POU actions
 * @author Kamil Rosiak
 *
 */
public abstract class POUActionMutation extends POUMutation {

	/**
	 * Returns only POUs with actions
	 * @param mutCtx
	 * @return
	 */
	protected List<POUImpl> getPOUsWithActions(MutationContext mutCtx) {
		List<POUImpl> randomizedPOUList = getRandomizedPOUs(mutCtx);
		List<POUImpl> pousWithAction = randomizedPOUList.stream().map(e -> (POUImpl)e).collect(Collectors.toList());
		pousWithAction = pousWithAction.stream().filter(e -> !e.getActions().isEmpty()).collect(Collectors.toList());
		return pousWithAction;
	}
	
	/**
	 * Returns a random action from the given POU
	 * @param pou
	 * @return
	 */
	protected Action getRandomAction(POUImpl pou) {
		return pou.getActions().get(getRandomly().nextInt(pou.getActions().size()));	
	}
}
