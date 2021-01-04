package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario.ScenarioFacade;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;


public class MutationInjection {

	@Inject
	@Named("STMutator")
	private Mutator mutator;
	
	@Inject
	@Named("single")
	private MutationContextFactory factory;
	
	@Inject
	private ScenarioFacade scenarioFacade;
	
	/**
	 * Loads scenario, applies mutations as defined in the {@link Mutator}, 
	 * and writes the result back to the mutant directory. 
	 * 
	 * @param scenarioName
	 * @param mutantPostfix
	 * @throws IOException
	 * 
	 * @see Mutator
	 * @see MutationContextFactory
	 * @see ScenarioFacade#getMutantDirectoryName()
	 */
	public void generateMutant(String scenarioName, String mutantPostfix) throws IOException {
		Optional<Configuration> scenario = scenarioFacade.loadScenario(scenarioName);
		if (!scenario.isPresent()) {
			throw new IllegalArgumentException("The scenario name is not known or is not located in the workspace.");
		}
		System.out.println(String.format("Scenario \"%s\" loaded.", scenarioName));
		
		TreeIterator<EObject> it = EcoreUtil.<EObject>getAllProperContents(scenario.get(), true);
		iterate(it);
		
		System.out.println(String.format("Scenario mutatant \"%s\" stored as \"%s\" in directory %s.", scenarioName, scenarioName+mutantPostfix, scenarioFacade.getMutantDirectoryName()));
		scenarioFacade.saveScenario(scenarioName+mutantPostfix, scenario.get());
	}
	
	private void iterate(TreeIterator<EObject> it) {
		
		while (it.hasNext()) {
			EObject eobject = it.next();				
			
			if (eobject instanceof POU) {
				MutationContext ctx = factory.createFromPOU((POU) eobject);
				mutator.mutate(ctx);
			} else if (eobject instanceof Action) {
				MutationContext ctx = factory.createFromAction((Action) eobject);
				mutator.mutate(ctx);
			} else if (eobject instanceof Expression) {
				MutationContext ctx = factory.createFromSTExpression((Expression) eobject);
				mutator.mutate(ctx);
			} else if (eobject instanceof StructuredText) {
				MutationContext ctx = factory.createFromST((StructuredText) eobject);
				mutator.mutate(ctx);
				it.prune(); // remove sub tree of structured text since eobjects might have been deleted
				TreeIterator<EObject> stIt = EcoreUtil.getAllProperContents(eobject, true);
				stIt.next();
				iterate(stIt);
			} 
		}
	}
}
