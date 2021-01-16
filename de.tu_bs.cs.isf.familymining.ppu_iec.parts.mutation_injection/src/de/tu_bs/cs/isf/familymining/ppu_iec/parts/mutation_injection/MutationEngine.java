package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation.MUTATION_PREF;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.ENUM_MAX_MUTATIONS;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.ENUM_MAX_MUTATIONS_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NAME_MAX_MUTATIONS;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NAME_MAX_MUTATIONS_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NUMBER_GENERATED_DIGIT_LENGTH;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NUMBER_GENERATED_DIGIT_LENGTH_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NUMBER_MAX_MUTATIONS;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.NUMBER_MAX_MUTATIONS_DEFAULT;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util.ConfigurationCompareUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type2Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
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
		// TODO: Oli the Configuration must be a copy of the seed
		Configuration mutant = null;
		// Configuration mutant = null;// =
		// mutationInjection.generateMutant(seed.getIdentifier(), "1");

		ConfigurationResultRoot result = ConfigurationCompareUtil.compare(seed, mutant);
		// find changes
		List<AbstractContainer> changeList = ConfigurationCompareUtil.findChanges(result);
		
		// TODO: Oli get all EObject pairs which are changed during a mutation
		List<MutationPair> containedMutants = null;	
		
		//search for accordances between mutants and found changes
		System.out.println("Mutants in Model: " + containedMutants.size());
		int foundMutants = searchForMutants(changeList, containedMutants);
		System.out.println("Found Mutants: " + foundMutants);

		// next iteration with the mutant as seed
		if (run < RUNS) {
			mutationCycle(mutant, run++);
		}
	}
	
	
	/**
	 * This method iterates over both list and removes pairs if elements are matching
	 */
	private int searchForMutants(List<AbstractContainer> changeList, List<MutationPair> containedMutants ) {
		Iterator<AbstractContainer> changeIterator = changeList.iterator();
		Iterator<MutationPair> mutantsIterator = containedMutants.iterator();
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
		// mutation operators
		prefs.putInt(NAME_MAX_MUTATIONS, prefs.getInt(NAME_MAX_MUTATIONS, NAME_MAX_MUTATIONS_DEFAULT));
		NameChanger nameChanger = ContextInjectionFactory.make(NameChanger.class, context);
		context.set(NameChanger.class, nameChanger);

		prefs.putInt(ENUM_MAX_MUTATIONS, prefs.getInt(ENUM_MAX_MUTATIONS, ENUM_MAX_MUTATIONS_DEFAULT));
		EnumChanger enumChanger = ContextInjectionFactory.make(EnumChanger.class, context);
		context.set(EnumChanger.class, enumChanger);

		prefs.putInt(NUMBER_MAX_MUTATIONS, prefs.getInt(NUMBER_MAX_MUTATIONS, NUMBER_MAX_MUTATIONS_DEFAULT));
		prefs.putInt(NUMBER_GENERATED_DIGIT_LENGTH,
				prefs.getInt(NUMBER_GENERATED_DIGIT_LENGTH, NUMBER_GENERATED_DIGIT_LENGTH_DEFAULT));
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, context);
		context.set(NumberChanger.class, numberChanger);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		// mutators and supporting factories
		Type2Mutator type2Mutator = ContextInjectionFactory.make(Type2Mutator.class, context);
		context.set("type2Mutator", type2Mutator);
		//TODO: Oli kp wie das jetzt gemacht wird 
		//context.set("single", new SingleContextFactory());

		setMutationInjection(ContextInjectionFactory.make(MutationInjection.class, context));
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
