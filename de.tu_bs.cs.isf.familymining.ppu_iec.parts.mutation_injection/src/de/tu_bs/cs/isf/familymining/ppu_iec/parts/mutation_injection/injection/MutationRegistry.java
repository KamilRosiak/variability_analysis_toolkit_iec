package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.UnknownObjectException;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;

public class MutationRegistry {

	private final List<MutationContext> mutCtxs = new ArrayList<>();
	
	/**
	 * 
	 * @param originalScenarioObject
	 * @return
	 * @throws UnknownObjectException
	 */
	public Optional<EObject> searchMutated(EObject originalScenarioObject) throws UnknownObjectException {
		for (MutationContext ctx : mutCtxs) {
			try {
				return ctx.getMutated(originalScenarioObject);				
			} catch (UnknownObjectException e) {
				// original object not contained in context, keep searching
			}
		}
		throw new UnknownObjectException(String.format("Object \"%s\" has not been mutated.", originalScenarioObject));
	}

	public Optional<EObject> searchOriginal(EObject mutatedScenarioObject) throws UnknownObjectException {
		for (MutationContext ctx : mutCtxs) {
			try {
				return ctx.getOriginal(mutatedScenarioObject);				
			} catch (UnknownObjectException e) {
				// mutated object not contained in context, keep searching
			}
		}
		throw new UnknownObjectException(String.format("Object \"%s\" has not resulted from mutation.", mutatedScenarioObject));
	}

	public Optional<MutationContext> getMostRecentMutationContext() {
		if (!mutCtxs.isEmpty()) {
			return Optional.of(mutCtxs.get(mutCtxs.size() - 1));
		}
		return Optional.empty();
	}
	
	/**
	 * Returns all pairs of eobjects which are mutated.
	 * By mutation a change, removal or insertion is meant.
	 * 
	 * @return pairs of mutated eobjects
	 */
	public List<MutationPair> getMutationPairs() {
		List<MutationPair> mutPairs = new ArrayList<>();
		for (MutationContext ctx : mutCtxs) {
			mutPairs.addAll(ctx.getMutationPairs());
		}
		return mutPairs;
	}
	
	public int mutatedEObjects() {
		return mutCtxs.stream().mapToInt(MutationContext::mutatedEObjects).sum();
	}
	
	public List<MutationContext> getMutCtxs() {
		return mutCtxs;
	}
}
