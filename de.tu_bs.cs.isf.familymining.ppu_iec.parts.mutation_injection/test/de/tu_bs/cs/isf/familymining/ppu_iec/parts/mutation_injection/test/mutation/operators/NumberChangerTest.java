package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NumberChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;

public class NumberChangerTest extends ScenarioTest {
	private static final int GEN_DIGIT_LENGTH = 5;
	private static final int MAX_MUTATIONS = 3;

	private NumberChanger numberChanger;

	@BeforeEach
	void setUp() throws Exception {
		numberChanger = new NumberChanger();
		numberChanger.postConstruct(MAX_MUTATIONS, GEN_DIGIT_LENGTH);
	}

	@RepeatedTest(5)
	public void testParameter_maxMutations() {
		MutationContext ctx = new MutationContext();
		ctx.setCtxObjects(forLoops(5));

		MutationContext mutCtx = numberChanger.apply(ctx.clone());
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
