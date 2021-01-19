package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection;

import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_ACTION_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_ACTION_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_CONFIG_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_CONFIG_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_DECL_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_DECL_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_EXPR_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_EXPR_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_POU_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_POU_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_ACTION_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_ACTION_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_STEP_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_STEP_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_TRANS_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_SFC_TRANS_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_STATEMENT_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_STATEMENT_CHANCE_DEFAULT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_ST_CHANCE;
import static de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InjectionParameters.CLUSTER_FACT_ST_CHANCE_DEFAULT;
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

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.CompoundMutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementInserter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementRemover;

/**
 * Fills the eclipse context with necessary configuration parameters for the mutation injection
 * 
 * @author Oliver Urbaniak
 * 
 * @see MutationInjection
 */
public class MutationInjectionConfig {

	@Inject
	public void prepareContext(IEclipseContext context, @Preference(nodePath = MUTATION_PREF) IEclipsePreferences prefs) {
		// mutation operators
		setIntPref(prefs, NAME_MAX_MUTATIONS, NAME_MAX_MUTATIONS_DEFAULT);		
		NameChanger nameChanger = ContextInjectionFactory.make(NameChanger.class, context);
		context.set(NameChanger.class, nameChanger);
		
		setIntPref(prefs, ENUM_MAX_MUTATIONS, ENUM_MAX_MUTATIONS_DEFAULT);
		EnumChanger enumChanger = ContextInjectionFactory.make(EnumChanger.class, context);
		context.set(EnumChanger.class, enumChanger);
		
		setIntPref(prefs, NUMBER_MAX_MUTATIONS, NUMBER_MAX_MUTATIONS_DEFAULT);
		setIntPref(prefs, NUMBER_GENERATED_DIGIT_LENGTH, NUMBER_GENERATED_DIGIT_LENGTH_DEFAULT);
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, context);
		context.set(NumberChanger.class, numberChanger);
		
		setIntPref(prefs, STMT_INS_MAX_MUTATIONS, STMT_INS_MAX_MUTATIONS_DEFAULT);
		StatementInserter stmtInserter = ContextInjectionFactory.make(StatementInserter.class, context);
		context.set(StatementInserter.class, stmtInserter);
		
		setIntPref(prefs, STMT_REM_MAX_MUTATIONS, STMT_REM_MAX_MUTATIONS_DEFAULT);
		StatementRemover stmtRemover = ContextInjectionFactory.make(StatementRemover.class, context);
		context.set(StatementRemover.class, stmtRemover);

		// cluster factory
		setFloatPref(prefs, CLUSTER_FACT_CONFIG_CHANCE, CLUSTER_FACT_CONFIG_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_POU_CHANCE, CLUSTER_FACT_POU_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_ACTION_CHANCE,CLUSTER_FACT_ACTION_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_DECL_CHANCE, CLUSTER_FACT_DECL_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_ST_CHANCE,  CLUSTER_FACT_ST_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_STATEMENT_CHANCE, CLUSTER_FACT_STATEMENT_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_EXPR_CHANCE, CLUSTER_FACT_EXPR_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_SFC_CHANCE, CLUSTER_FACT_SFC_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_SFC_STEP_CHANCE, CLUSTER_FACT_SFC_STEP_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_SFC_ACTION_CHANCE, CLUSTER_FACT_SFC_ACTION_CHANCE_DEFAULT);
		setFloatPref(prefs, CLUSTER_FACT_SFC_TRANS_CHANCE, CLUSTER_FACT_SFC_TRANS_CHANCE_DEFAULT);
		RandomizedClusterFactory randomizedClusterFactory = ContextInjectionFactory.make(RandomizedClusterFactory.class, context);
		context.set(ScenarioObjectClusterFactory.class, randomizedClusterFactory);
		
		// mutator		
		CompoundMutator compoundMutator = ContextInjectionFactory.make(CompoundMutator.class, context);
		context.set(Mutator.class, compoundMutator);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	private void setIntPref(IEclipsePreferences prefs, String key, int defaultValue) {
		prefs.putInt(key, prefs.getInt(key, defaultValue));
	}
	
	private void setFloatPref(IEclipsePreferences prefs, String key, float defaultValue) {
		prefs.putFloat(key, prefs.getFloat(key, defaultValue));
	}
}
