package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NAME_MAX_MUTATIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;

public class NameChanger implements Mutation {

	private int maxSymbolsMutations;

	@PostConstruct
	public void postConstruct(
			@Preference(nodePath = MUTATION_PREF, value = NAME_MAX_MUTATIONS) int maxSymbolsMutations) {
		this.maxSymbolsMutations = maxSymbolsMutations;
	}

	@Override
	public MutationContext apply(MutationContext ctx) {
		int symbolMutationCount = 0;
		Set<String> exclusionList = new TreeSet<>();

		List<EObject> randomized = new ArrayList<>(ctx.getCtxObjects());
		Collections.shuffle(randomized);

		Iterator<EObject> it = randomized.iterator();
		while (it.hasNext() && symbolMutationCount < maxSymbolsMutations) {
			EObject candidate = it.next();
			List<EAttribute> stringAttrs = scanForStringAttributes(candidate, exclusionList);
			if (!stringAttrs.isEmpty()) {
				EAttribute attr = stringAttrs.get(0);
				String oldValue = (String) candidate.eGet(attr);

				// log change
				ctx.logChange(candidate);

				String newValue = generateName(oldValue);
				candidate.eSet(attr, newValue);

				exclusionList.add(newValue);
				//replaceOccurrences(oldValue, newValue, ctx);
				

				symbolMutationCount++;
			}
		}
		return ctx;
	}

	/**
	 * Replaces occurrences of <i>oldValue</i> with <i>newValue</i> in the context
	 * 
	 * @param oldValue
	 * @param newValue
	 * @param ctx
	 */
	private void replaceOccurrences(String oldValue, String newValue, MutationContext ctx) {
		for (EObject ctxObject : ctx.getCtxObjects()) {
			for (EAttribute attr : scanForStringAttributes(ctxObject, Collections.emptySet())) {
				String value = (String) ctxObject.eGet(attr);
				if (value.equals(oldValue)) {
					ctxObject.eSet(attr, newValue);
				}
			}
		}
	}

	/**
	 * Generates a new string different from the argument.
	 * 
	 * @param name filter for generated name
	 * @return
	 */
	private String generateName(String name) {
		String generatedName = "";
		do {
			generatedName = RandomStringUtils.randomAlphanumeric(name.length() != 0 ? name.length() : 1);
		} while (generatedName.equalsIgnoreCase(name));
		return generatedName;
	}

	/**
	 * 
	 * @param eobject    source object for attribute scanning
	 * @param exclusions set of names excluded from scan
	 * @return
	 */
	private List<EAttribute> scanForStringAttributes(EObject eobject, Set<String> exclusions) {
		List<EAttribute> attributes = new ArrayList<>();
		for (EAttribute attr : eobject.eClass().getEAllAttributes()) {
			Object o = eobject.eGet(attr);
			if (o instanceof String && !exclusions.contains(o)) {
				attributes.add(attr);
			}
		}
		return attributes;
	}
}
