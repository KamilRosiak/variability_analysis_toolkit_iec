package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation.MUTATION_PREF;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.ConfigurationCompareUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationResult;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.UnknownObjectException;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjectionConfig;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationRegistry;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

@Creatable
public class MutationEngine {
	private static final int RUNS = 10;
	private ServiceContainer services;
	private MutationInjection mutationInjection;

	@Inject
	public MutationEngine(ServiceContainer services, IEclipseContext context,
			@Preference(nodePath = MUTATION_PREF) IEclipsePreferences prefs) {
		this.setServices(services);
		initMutationInjection(context, prefs);
	}

	public void startMutation(Configuration seed) {
		mutationCycle(seed, 0);
	}

	private void mutationCycle(Configuration seed, int run) {
		// generate and rename mutant 
		MutationResult mutationResult = mutationInjection.generateMutant(seed);
		Configuration mutant = mutationResult.getMutated();
		mutant.getResources().get(0).setName(name(seed));

		// find changes
		ConfigurationResultRoot result = ConfigurationCompareUtil.compare(seed, mutant);
		List<AbstractContainer> changeList = ConfigurationCompareUtil.findChanges(result);
				
		//search for accordances between mutants and found changes
		List<MutationPair> totalMutants = mutationResult.getMutationRegistry().getMutationPairs();
		System.out.println("Total Mutants: " + totalMutants);		
		
		int foundMutants = searchForMutants(changeList, totalMutants);
		System.out.println("Found Mutants: " + foundMutants);

		// next iteration with the mutant as seed
		if (run < RUNS) {
			mutationCycle(mutant, run++);
		}
	}
	
	
	/**
	 * This method iterates over both list and removes pairs if elements are matching
	 */
	private int searchForMutants(List<AbstractContainer> changeList, List<MutationPair> totalMutants) {
		Iterator<AbstractContainer> changeIterator = changeList.iterator();
		Iterator<MutationPair> mutantsIterator = totalMutants.iterator();
		int foundMutants = 0;
		while (changeIterator.hasNext()) {
			AbstractContainer currentContainer = changeIterator.next();
			while (mutantsIterator.hasNext()) {
				MutationPair mutantPair = mutantsIterator.next();
				// Mutant was found can can be remove from both iterators
				if (mutantPair.getOrigin().equals(currentContainer.getFirst())
						&& mutantPair.getMutant().equals(currentContainer.getSecond())) {
					mutantsIterator.remove();
					changeIterator.remove();
					foundMutants++;
					// TODO: Create an evaluation of all runs with detailed information
				}
			}
		}
		return foundMutants;
	}
	
	/**
	 * This method initializes the mutationInjection
	 */
	@Inject
	public void initMutationInjection(IEclipseContext context,
			@Preference(nodePath = MUTATION_PREF) IEclipsePreferences prefs) {
		ContextInjectionFactory.inject(new MutationInjectionConfig(), context);
		mutationInjection = ContextInjectionFactory.make(MutationInjection.class, context);
	}

	private String name(Configuration scenario) {
		return scenario.getResources().get(0).getName();
	}
	
	public ServiceContainer getServices() {
		return services;
	}

	public void setServices(ServiceContainer services) {
		this.services = services;
	}

	public MutationInjection getMutationInjection() {
		return mutationInjection;
	}

	public void setMutationInjection(MutationInjection mutationInjection) {
		this.mutationInjection = mutationInjection;
	}
}