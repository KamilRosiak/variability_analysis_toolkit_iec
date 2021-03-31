package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationFactory;

/**
 * Helper class for the generation of random actions
 * @author Kamil Rosiak
 *
 */
public class ActionGenerator {
	public static final ConfigurationFactory factory = ConfigurationFactory.eINSTANCE;
	/**
	 * This mehtod generates a random action
	 * @return
	 */
	public static Action generateAction() {
		Action action = factory.createAction();
		action.setName(StringGenerator.getRandomStringOfLenght(40));
		return action;
	}

}
