package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Arrays;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.HashBiMap;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.AttributeFilter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.EnumChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.StepQualifier;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;

public class AttributeFilterTest extends ScenarioTest {

	private static final int GENERATED_DIGIT_LENGTH = 6;
	private static final int MAX_MUTATIONS = 10;
	
	@RepeatedTest(20)
	public void testStatementAttributeFilter() {
		ForLoop forLoop = createForLoop(0, 1, 10);
		ForLoop cloneLoop = EcoreUtil.copy(forLoop);
		
		IEclipseContext eclipseCtx = EclipseContextFactory.create();
		eclipseCtx.set(AttributeFilter.class, new AttributeFilter());
		
		EnumChanger enumChanger = ContextInjectionFactory.make(EnumChanger.class, eclipseCtx);
		enumChanger.postConstruct(MAX_MUTATIONS);
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, eclipseCtx);
		numberChanger.postConstruct(MAX_MUTATIONS, GENERATED_DIGIT_LENGTH);
		
		MutationContext mutCtx = spy(new MutationContext(HashBiMap.create()));
		doReturn(Arrays.asList(forLoop)).when(mutCtx).getCtxObjects();
		doNothing().when(mutCtx).logChange(Mockito.any());
		doNothing().when(mutCtx).logRemoval(Mockito.any());
		doNothing().when(mutCtx).logInsertion(Mockito.any(), Mockito.any());
		
		enumChanger.apply(mutCtx);
		numberChanger.apply(mutCtx);
		
		assertThat(forLoop.getId()).isEqualTo(cloneLoop.getId());
		assertThat(forLoop.getAbsStartLine()).isEqualTo(cloneLoop.getAbsStartLine());
		assertThat(forLoop.getAbsEndLine()).isEqualTo(cloneLoop.getAbsEndLine());
		assertThat(forLoop.getRelStartLine()).isEqualTo(cloneLoop.getRelStartLine());
		assertThat(forLoop.getRelEndLine()).isEqualTo(cloneLoop.getRelEndLine());
		assertThat(forLoop.getStartColumnPos()).isEqualTo(cloneLoop.getStartColumnPos());
		assertThat(forLoop.getEndColumnPos()).isEqualTo(cloneLoop.getEndColumnPos());
		assertThat(forLoop.getStatementType()).isEqualTo(cloneLoop.getStatementType());		
	}
	
	@Test
	public void testStepFilter() {
		Step step = createStep(0, "step", StepQualifier.D);
		Step cloneStep = EcoreUtil.copy(step);
		
		IEclipseContext eclipseCtx = EclipseContextFactory.create();
		eclipseCtx.set(AttributeFilter.class, new AttributeFilter());
		
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, eclipseCtx);
		numberChanger.postConstruct(MAX_MUTATIONS, GENERATED_DIGIT_LENGTH);
		
		MutationContext mutCtx = spy(new MutationContext(HashBiMap.create()));
		doReturn(Arrays.asList(step)).when(mutCtx).getCtxObjects();
		doNothing().when(mutCtx).logChange(Mockito.any());
		doNothing().when(mutCtx).logRemoval(Mockito.any());
		doNothing().when(mutCtx).logInsertion(Mockito.any(), Mockito.any());
		
		numberChanger.apply(mutCtx);
		
		assertThat(step.getLocal_ID()).isEqualTo(cloneStep.getLocal_ID());
	}
	
	@RepeatedTest(20)
	public void testTransitionFilter() {
		Transition transition = createTransition(0, "condition", false);
		Transition cloneTransition = EcoreUtil.copy(transition);
		
		IEclipseContext eclipseCtx = EclipseContextFactory.create();
		eclipseCtx.set(AttributeFilter.class, new AttributeFilter());
		
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, eclipseCtx);
		numberChanger.postConstruct(MAX_MUTATIONS, GENERATED_DIGIT_LENGTH);
		
		MutationContext mutCtx = spy(new MutationContext(HashBiMap.create()));
		doReturn(Arrays.asList(transition)).when(mutCtx).getCtxObjects();
		doNothing().when(mutCtx).logChange(Mockito.any());
		doNothing().when(mutCtx).logRemoval(Mockito.any());
		doNothing().when(mutCtx).logInsertion(Mockito.any(), Mockito.any());
		
		numberChanger.apply(mutCtx);
		
		assertThat(transition.getLocal_ID()).isEqualTo(cloneTransition.getLocal_ID());
	}
	
	@RepeatedTest(20)
	public void testSFCActionFilter() {
		AbstractAction simpleAction = createSimpleAction(0, StepQualifier.N, null);
		AbstractAction cloneSimpleAction = EcoreUtil.copy(simpleAction);
		
		IEclipseContext eclipseCtx = EclipseContextFactory.create();
		eclipseCtx.set(AttributeFilter.class, new AttributeFilter());
		
		NumberChanger numberChanger = ContextInjectionFactory.make(NumberChanger.class, eclipseCtx);
		numberChanger.postConstruct(MAX_MUTATIONS, GENERATED_DIGIT_LENGTH);
		
		MutationContext mutCtx = spy(new MutationContext(HashBiMap.create()));
		doReturn(Arrays.asList(simpleAction)).when(mutCtx).getCtxObjects();
		doNothing().when(mutCtx).logChange(Mockito.any());
		doNothing().when(mutCtx).logRemoval(Mockito.any());
		doNothing().when(mutCtx).logInsertion(Mockito.any(), Mockito.any());
		
		numberChanger.apply(mutCtx);
		
		assertThat(simpleAction.getLocalId()).isEqualTo(cloneSimpleAction.getLocalId());
	}
}
