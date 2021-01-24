package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators.StatementGenerator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;

public class StatementInserter extends StatementMutation {
	
	@Inject
	private StatementGenerator statementGenerator; 
	
	private int maxMutations;

	@PostConstruct
	public void postConstruct(
			@Preference(nodePath = MUTATION_PREF, value = MutationParameters.STMT_INS_MAX_MUTATIONS) int maxMutations) {
		this.maxMutations = maxMutations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MutationContext apply(MutationContext ctx) {
		int mutationCount = 0;

		List<EObject> randomizedStatementContainers = ctx.getCtxObjects().stream().filter(statementContainers())
				.collect(Collectors.toList());
		Collections.shuffle(randomizedStatementContainers);

		Iterator<EObject> it = randomizedStatementContainers.iterator();
		while (it.hasNext() && mutationCount < maxMutations) {
			EObject stmtContainer = it.next();
			List<EReference> stmtRefs = stmtContainer.eClass().getEAllContainments().stream()
					.filter(this::isStatementListRef).collect(Collectors.toList());

			for (EReference stmtRef : stmtRefs) {
				EList<Statement> stmts = (EList<Statement>) stmtContainer.eGet(stmtRef, true);
				Statement generatedStatement = statementGenerator.generateStatement();

				// log change
				ctx.logInsertion(generatedStatement);
				ctx.setChangedTreeStructure(true);

				// do the insertion
				stmts.add(generatedStatement);
			}
		}

		return ctx;
	}
}
