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
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.STMT_INS_MAX_MUTATIONS;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.STMT_INS_MAX_MUTATIONS_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.STMT_REM_MAX_MUTATIONS;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.MutationParameters.STMT_REM_MAX_MUTATIONS_DEFAULT;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.SingleElementClusterFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.STMutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type2Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementInserter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementRemover;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

public class MutationInjectionController {

	private MutationInjection mutationInjection;

	@Inject
	public void fillContext(IEclipseContext context, @Preference(nodePath = MUTATION_PREF) IEclipsePreferences prefs) {		
		// mutation operators
		prefs.putInt(NAME_MAX_MUTATIONS, prefs.getInt(NAME_MAX_MUTATIONS, NAME_MAX_MUTATIONS_DEFAULT));		
		NameChanger nameChanger = ContextInjectionFactory.make(NameChanger.class, context);
		context.set(NameChanger.class, nameChanger);
		
		prefs.putInt(ENUM_MAX_MUTATIONS, prefs.getInt(ENUM_MAX_MUTATIONS, ENUM_MAX_MUTATIONS_DEFAULT));
		EnumChanger enumChanger = ContextInjectionFactory.make(EnumChanger.class, context);
		context.set(EnumChanger.class, enumChanger);
		
		prefs.putInt(NUMBER_MAX_MUTATIONS, prefs.getInt(NUMBER_MAX_MUTATIONS, NUMBER_MAX_MUTATIONS_DEFAULT));
		prefs.putInt(NUMBER_GENERATED_DIGIT_LENGTH, prefs.getInt(NUMBER_GENERATED_DIGIT_LENGTH, NUMBER_GENERATED_DIGIT_LENGTH_DEFAULT));
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, context);
		context.set(NumberChanger.class, numberChanger);
		
		prefs.putInt(STMT_INS_MAX_MUTATIONS, prefs.getInt(STMT_INS_MAX_MUTATIONS, STMT_INS_MAX_MUTATIONS_DEFAULT));
		StatementInserter stmtInserter = ContextInjectionFactory.make(StatementInserter.class, context);
		context.set(StatementInserter.class, stmtInserter);
		
		prefs.putInt(STMT_REM_MAX_MUTATIONS, prefs.getInt(STMT_REM_MAX_MUTATIONS, STMT_REM_MAX_MUTATIONS_DEFAULT));
		StatementRemover stmtRemover = ContextInjectionFactory.make(StatementRemover.class, context);
		context.set(StatementRemover.class, stmtRemover);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		// mutators and supporting factories
		Type2Mutator type2Mutator = ContextInjectionFactory.make(Type2Mutator.class, context);
		context.set("type2Mutator", type2Mutator);
		
		STMutator stMutator = ContextInjectionFactory.make(STMutator.class, context);
		context.set("STMutator", stMutator);
		
		context.set("single", new SingleElementClusterFactory());
		
		mutationInjection = ContextInjectionFactory.make(MutationInjection.class, context);
	}

	@PostConstruct
	public void init() {
		demo();
	}

	public void demo() {
		try {
			String scenario24_small = "scenario24_small";
			Configuration config = mutationInjection.loadScenario(scenario24_small);
			
			Configuration mutConfig = mutationInjection.generateMutant(config);
			
			mutationInjection.saveScenario(mutConfig, "-mutated");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 
	 * @param config
	 * @return
	 */
	public Configuration mutate(Configuration config) {
		// copy config
		
		// mutate 
		
		// post processing - somehow store mutation meta data
		
		
		
		// return 
		
		return null;
	}
	
}
