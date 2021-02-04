package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.VariableImpl;

public abstract class VariableMutation extends POUMutation {
	/**
	 * Return a list with all POUImpl objects from the context that is shuffled
	 * 
	 * @param mutCtx
	 * @return
	 */
	protected List<VariableImpl> getRandomizedVariables(MutationContext mutCtx) {
		// collect all pou container from the context
		List<VariableImpl> randomizedPOUContainers = mutCtx.getCtxObjects().stream().filter(variableContainers())
				.map(e -> (VariableImpl) e).collect(Collectors.toList());
		// Shuffle the list
		Collections.shuffle(randomizedPOUContainers);
		return randomizedPOUContainers;
	}

	/**
	 * Returns a random variable from a given list of variables
	 */
	protected VariableImpl getRandomVariable(List<VariableImpl> variables) {
		Randomization randomly = new Randomization();
		int randIndex = randomly.nextInt(variables.size());
		return variables.get(randIndex);
	}
	

	/**
	 * Predicate for VariableImpl.class
	 * 
	 * @return
	 */
	protected Predicate<EObject> variableContainers() {
		return (eobject) -> Stream.of(VariableImpl.class).anyMatch(clazz -> clazz.isAssignableFrom(eobject.getClass()));
	}
}
