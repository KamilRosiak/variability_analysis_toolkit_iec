package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.StatementInserter;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;

public class StatementInserterTest extends ScenarioTest {
	private static final int MAX_MUTATIONS = 3;
	
	private static final int ST_INSTANCE_SIZE = 3;
	private static final int STMT_SIZE = 5;

	private StatementInserter stmtInserter;

	@BeforeEach
	void setUp() throws Exception {
		stmtInserter = new StatementInserter();
		stmtInserter.postConstruct(MAX_MUTATIONS);
	}

	@Test
	public void testParameter_maxMutations() {
		MutationContext ctx = new MutationContext(null, null);
		ctx.getCtxObjects().addAll(structuredText(ST_INSTANCE_SIZE, STMT_SIZE));

		MutationContext mutCtx = stmtInserter.apply(ctx.clone());

		int totalChangeCount = 0;
		for (int i = 0; i < ST_INSTANCE_SIZE; i++) {
			List<Statement> ctxVar = ((StructuredText) ctx.getCtxObjects().get(i)).getStatements();
			List<Statement> mutCtxVar =  ((StructuredText) mutCtx.getCtxObjects().get(i)).getStatements();
			
			int sizeDiff = mutCtxVar.size() - ctxVar.size();
			assertThat(sizeDiff).isPositive();
			totalChangeCount += sizeDiff;
		}
		
		assertThat(totalChangeCount).isEqualTo(3);
	}

	private List<EObject> structuredText(int instances, int stmtCount) {
		StructuredText stPrototype = stFactory.createStructuredText();
		stPrototype.setId(RandomStringUtils.randomAlphabetic(10));
		stPrototype.setLabel("Prototype");
		stPrototype.setText("");
		ForLoop forLoopPrototype = createForLoop(0, 1, 10);
		for (int j = 0; j < stmtCount; j++) {
			stPrototype.getStatements().add(EcoreUtil.copy(forLoopPrototype));
		}
	
		List<EObject> stList = new ArrayList<>();
		for (int i = 0; i < instances; i++) {
			stList.add(EcoreUtil.copy(stPrototype));
		}
		stList.add(stPrototype);
		return stList;
	}
}
