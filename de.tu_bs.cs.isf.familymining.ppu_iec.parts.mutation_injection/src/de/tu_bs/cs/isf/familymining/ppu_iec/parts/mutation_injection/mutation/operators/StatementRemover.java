package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;

public class StatementRemover extends StatementMutation {
	private int maxMutations;

	private Randomization randomly;

	@PostConstruct
	public void postConstruct(
			@Preference(nodePath = MUTATION_PREF, value = MutationParameters.STMT_INS_MAX_MUTATIONS) int maxMutations) {
		this.maxMutations = maxMutations;
		this.randomly = new Randomization();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MutationContext apply(MutationContext ctx) {
		int mutationCount = 0;

		List<EObject> randomized = ctx.getCtxObjects().stream().filter(statementContainers())
				.collect(Collectors.toList());
		Collections.shuffle(randomized);

		Iterator<EObject> it = randomized.iterator();
		while (it.hasNext() && mutationCount < maxMutations) {
			EObject stmtContainer = it.next();
			List<EReference> stmtRefs = stmtContainer.eClass().getEAllContainments().stream()
					.filter(this::isStatementListRef).collect(Collectors.toList());

			for (EReference stmtRef : stmtRefs) {
				EList<Statement> stmts = (EList<Statement>) stmtContainer.eGet(stmtRef, true);
				if (!stmts.isEmpty()) {
					// log change
					int randIndex = randomly.nextInt(stmts.size());
					Statement toBeRemoved = stmts.get(randIndex);
					ctx.logRemoval(toBeRemoved);

					stmts.remove(randIndex);		
					
					ctx.setChangedTreeStructure(true);
				}
			}
		}

		return ctx;
	}
}
