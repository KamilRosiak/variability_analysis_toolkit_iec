package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

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

/**
 * 
 * 
 * @author Oliver Urbaniak
 *
 */
public interface MutationContextFactory {

	MutationContext createFromConfiguration(Configuration config);
	
	MutationContext createFromPOU(POU pou);
	
	MutationContext createFromAction(Action action);
	
	MutationContext createFromDeclaration(Declaration declaration);
	
	MutationContext createFromST(StructuredText st);
	
	MutationContext createFromSTStatement(Statement statement);
	
	MutationContext createFromSTExpression(Expression expression);
	
	MutationContext createFromSFC(SequentialFunctionChart sfc);
	
	MutationContext createFromSFCStep(Step step);
	
	MutationContext createFromSFCAction(AbstractAction action);
	
	MutationContext createFromSFCTransition(Transition transition);
}
