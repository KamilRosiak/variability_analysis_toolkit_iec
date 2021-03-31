package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.CaseBlock;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ConditionalBlock;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredTextPackage;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.UnboundedLoop;

public abstract class StatementMutation implements Mutation {

	protected boolean isStatementListRef(EReference containmentRef) {
		return StructuredTextPackage.Literals.STATEMENT == containmentRef.getEType()
				&& containmentRef.getUpperBound() == -1;
	}

	protected Predicate<EObject> statementContainers() {
		return (eobject) -> Stream
				.of(StructuredText.class, ConditionalBlock.class, CaseBlock.class, ForLoop.class, UnboundedLoop.class)
				.anyMatch(clazz -> clazz.isAssignableFrom(eobject.getClass()));
	}

}
