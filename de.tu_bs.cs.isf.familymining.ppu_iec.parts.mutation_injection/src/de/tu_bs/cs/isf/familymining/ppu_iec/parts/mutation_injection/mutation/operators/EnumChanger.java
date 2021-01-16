package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.ENUM_MAX_MUTATIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;

public class EnumChanger implements Mutation {

	private int maxSymbolsMutations;

	private Randomization randomly;

	@PostConstruct
	public void postConstruct(
			@Preference(nodePath = MUTATION_PREF, value = ENUM_MAX_MUTATIONS) int maxSymbolsMutations) {
		this.maxSymbolsMutations = maxSymbolsMutations;
		this.randomly = new Randomization();
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
			List<EAttribute> stringAttrs = scanForEnumAttributes(candidate, exclusionList);
			if (!stringAttrs.isEmpty()) {
				EAttribute attr = stringAttrs.get(0);
				Enumerator oldValue = (Enumerator) candidate.eGet(attr);

				List<? extends Enumerator> valueCandidates = Stream.of(oldValue.getClass().getEnumConstants())
						.filter(enumLiteral -> !enumLiteral.equals(oldValue)).collect(Collectors.toList());

				if (!valueCandidates.isEmpty()) {
					Enumerator newValue = randomly.pickFrom(valueCandidates);

					candidate.eSet(attr, newValue);
					exclusionList.add(newValue.getLiteral());

					// log change
					ctx.logChange(candidate);
					
					symbolMutationCount++;
				}
			}
		}
		return ctx;
	}

	/**
	 * 
	 * @param eobject       source object for attribute scanning
	 * @param exclusionList set of names excluded from scan
	 * @return
	 */
	private List<EAttribute> scanForEnumAttributes(EObject eobject, Set<String> exclusionList) {
		List<EAttribute> attributes = new ArrayList<>();
		for (EAttribute attr : eobject.eClass().getEAllAttributes()) {
			Object value = eobject.eGet(attr);
			if (attr.getEAttributeType() instanceof EEnum && value instanceof Enumerator) {
				Enumerator enumerator = (Enumerator) value;
				if (!exclusionList.contains(enumerator.getLiteral())) {
					attributes.add(attr);
				}
			}
		}
		return attributes;
	}
}
