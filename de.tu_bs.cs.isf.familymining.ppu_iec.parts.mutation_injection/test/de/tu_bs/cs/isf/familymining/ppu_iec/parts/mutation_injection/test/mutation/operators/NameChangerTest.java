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
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.NameChanger;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableDeclaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableLocationDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableLocationType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;

public class NameChangerTest extends ScenarioTest {
	private static final int MAX_MUTATIONS = 3;

	private NameChanger nameChanger;

	@BeforeEach
	void setUp() throws Exception {
		nameChanger = new NameChanger();
		nameChanger.postConstruct(MAX_MUTATIONS);
	}

	@RepeatedTest(5)
	public void testParameter_maxMutations() {
		MutationContext ctx = new MutationContext(null, null);
		ctx.getCtxObjects().addAll(variables(5));

		MutationContext mutCtx = nameChanger.apply(ctx.clone());
		assertThat(mutCtx.getCtxObjects()).hasSize(5);

		int totalChangeCount = 0;
		for (int i = 0; i < 5; i++) {
			EObject ctxVar = ctx.getCtxObjects().get(i);
			EObject mutCtxVar = mutCtx.getCtxObjects().get(i);

			long changedEnumCount = mutCtxVar.eClass().getEAllAttributes().stream()
					.filter(attr -> attr.getEAttributeType() == EcorePackage.Literals.ESTRING)
					.filter(attr -> !ctxVar.eGet(attr).equals(mutCtxVar.eGet(attr))).count();
			totalChangeCount += changedEnumCount;
		}
		assertThat(totalChangeCount).isEqualTo(MAX_MUTATIONS);
	}

	private List<EObject> variables(int count) {
		List<EObject> variables = new ArrayList<>();

		Variable varPrototype = createVariable("prototype", VariableDeclaration.CONSTANT, ElementaryDataType.DATE,
				VariableLocationDataType.W, VariableLocationType.Q);
		for (int i = 0; i < count; i++) {
			variables.add(EcoreUtil.copy(varPrototype));
		}

		return variables;
	}
}
