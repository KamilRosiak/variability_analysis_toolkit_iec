package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Declaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;

/**
 * Creates a cluster of scenario objects from a given key scenario object. 
 * The interface holds a cluster factory method for  key scenario object.
 * The returned cluster should have at least one element for a valid cluster, 
 * no element for a rejected key scenario object, or null if an error occurred.
 * 
 * @author Oliver Urbaniak
 */
public interface ScenarioObjectClusterFactory {

	public final static String MUTATION_PREF = "de.tu_bs.cs.isf.familymining.ppu_iec_parts.mutation_injection";
	
	List<EObject> createFromConfiguration(Configuration config);
	
	List<EObject> createFromPOU(POU pou);
	
	List<EObject> createFromVariable(Variable variable);
	
	List<EObject> createFromAction(Action action);
	
	List<EObject> createFromDeclaration(Declaration declaration);
	
	List<EObject> createFromST(StructuredText st);
	
	List<EObject> createFromSTStatement(Statement statement);
	
	List<EObject> createFromSTExpression(Expression expression);
	
	List<EObject> createFromSFC(SequentialFunctionChart sfc);
	
	List<EObject> createFromSFCStep(Step step);
	
	List<EObject> createFromSFCAction(AbstractAction action);
	
	List<EObject> createFromSFCTransition(Transition transition);
}
