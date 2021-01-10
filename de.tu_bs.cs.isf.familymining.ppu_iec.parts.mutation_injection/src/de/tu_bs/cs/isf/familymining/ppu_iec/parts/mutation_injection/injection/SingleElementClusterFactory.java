package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;

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

public class SingleElementClusterFactory implements ScenarioObjectClusterFactory {

	@Override
	public List<EObject> createFromConfiguration(Configuration config) {
		return createSingleObjectContext(config);
	}

	@Override
	public List<EObject> createFromPOU(POU pou) {
		return createSingleObjectContext(pou);
	}

	@Override
	public List<EObject> createFromAction(Action action) {
		return createSingleObjectContext(action);
	}

	@Override
	public List<EObject> createFromDeclaration(Declaration declaration) {
		return createSingleObjectContext(declaration);
	}

	@Override
	public List<EObject> createFromST(StructuredText st) {
		return createSingleObjectContext(st);
	}

	@Override
	public List<EObject> createFromSTStatement(Statement statement) {
		return createSingleObjectContext(statement);
	}

	@Override
	public List<EObject> createFromSTExpression(Expression expression) {
		return createSingleObjectContext(expression);
	}

	@Override
	public List<EObject> createFromSFC(SequentialFunctionChart sfc) {
		return createSingleObjectContext(sfc);
	}

	@Override
	public List<EObject> createFromSFCStep(Step step) {
		return createSingleObjectContext(step);
	}

	@Override
	public List<EObject> createFromSFCAction(AbstractAction action) {
		return createSingleObjectContext(action);
	}

	@Override
	public List<EObject> createFromSFCTransition(Transition transition) {
		return createSingleObjectContext(transition);
	}
	
	private List<EObject> createSingleObjectContext(EObject eobject) {
		return Lists.newArrayList(eobject);
	}

}
