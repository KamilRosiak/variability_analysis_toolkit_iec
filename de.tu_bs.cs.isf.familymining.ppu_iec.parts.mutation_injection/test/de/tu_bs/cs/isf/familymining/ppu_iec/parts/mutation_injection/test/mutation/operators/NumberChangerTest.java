package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.mockito.Mockito;

import com.google.common.collect.HashBiMap;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.AttributeFilter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;

public class NumberChangerTest extends ScenarioTest {
	private static final int GEN_DIGIT_LENGTH = 5;
	private static final int MAX_MUTATIONS = 3;

	private NumberChanger numberChanger;

	@BeforeEach
	void setUp() throws Exception {
		IEclipseContext eclipseCtx = EclipseContextFactory.create();

		AttributeFilter attrFilter = Mockito.mock(AttributeFilter.class);
		Mockito.doReturn(true).when(attrFilter).test(Mockito.any(), Mockito.any());
		eclipseCtx.set(AttributeFilter.class, attrFilter);

		numberChanger = ContextInjectionFactory.make(NumberChanger.class, eclipseCtx);
		numberChanger.postConstruct(MAX_MUTATIONS, GEN_DIGIT_LENGTH);
		
	}

	@RepeatedTest(5)
	public void testParameter_maxMutations() {
		MutationContext ctx = new MutationContext(HashBiMap.create());
		ctx.getCtxObjects().addAll(forLoops(5));

		MutationContext clonedCtx = new MutationContext(HashBiMap.create());
		Collection<EObject> ctxObjectsCopy = EcoreUtil.copyAll(ctx.getCtxObjects());
		clonedCtx.getCtxObjects().addAll(ctxObjectsCopy);

		MutationContext mutCtx = numberChanger.apply(clonedCtx);
		assertThat(mutCtx.getCtxObjects()).hasSize(5);

		int totalChangeCount = 0;
		for (int i = 0; i < 5; i++) {
			EObject ctxFor = ctx.getCtxObjects().get(i);
			EObject mutCtxFor = mutCtx.getCtxObjects().get(i);

			long changedEnumCount = mutCtxFor.eClass().getEAllAttributes().stream()
					.filter(attr -> attr.getEAttributeType() == EcorePackage.Literals.EINT)
					.filter(attr -> !ctxFor.eGet(attr).equals(mutCtxFor.eGet(attr))).count();
			totalChangeCount += changedEnumCount;
		}
		assertThat(totalChangeCount).isEqualTo(MAX_MUTATIONS);
	}

	private List<EObject> forLoops(int count) {
		List<EObject> forLoops = new ArrayList<>();

		ForLoop forLoopPrototype = createForLoop(0, 1, 10);
		for (int i = 0; i < count; i++) {
			forLoops.add(EcoreUtil.copy(forLoopPrototype));
		}

		return forLoops;
	}
}
