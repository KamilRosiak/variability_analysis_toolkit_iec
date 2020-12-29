package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Declaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;

public class SingleContextFactory implements MutationContextFactory {

	@Override
	public MutationContext createFromConfiguration(Configuration config) {
		return createSingleObjectContext(config);
	}

	@Override
	public MutationContext createFromPOU(POU pou) {
		return createSingleObjectContext(pou);
	}

	@Override
	public MutationContext createFromAction(Action action) {
		return createSingleObjectContext(action);
	}

	@Override
	public MutationContext createFromDeclaration(Declaration declaration) {
		return createSingleObjectContext(declaration);
	}

	@Override
	public MutationContext createFromST(StructuredText st) {
		return createSingleObjectContext(st);
	}

	@Override
	public MutationContext createFromSTStatement(Statement statement) {
		return createSingleObjectContext(statement);
	}

	@Override
	public MutationContext createFromSTExpression(Expression expression) {
		return createSingleObjectContext(expression);
	}

	@Override
	public MutationContext createFromSFC(SequentialFunctionChart sfc) {
		return createSingleObjectContext(sfc);
	}

	@Override
	public MutationContext createFromSFCStep(Step step) {
		return createSingleObjectContext(step);
	}

	@Override
	public MutationContext createFromSFCAction(AbstractAction action) {
		return createSingleObjectContext(action);
	}

	@Override
	public MutationContext createFromSFCTransition(Transition transition) {
		return createSingleObjectContext(transition);
	}
	
	private MutationContext createSingleObjectContext(EObject eobject) {
		List<EObject> eobjects = new ArrayList<>();
		eobjects.add(eobject);
		
		MutationContext mutCtx = new MutationContext();
		mutCtx.setCtxObjects(eobjects);
		
		return mutCtx;
	}

}
