package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InequalTreeException;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators.StatementGenerator;
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
	private StatementGenerator stmtGen = new StatementGenerator();

	@BeforeEach
	void setUp() throws Exception {
		IEclipseContext eclipseCtx = EclipseContextFactory.create();
		stmtInserter = ContextInjectionFactory.make(StatementInserter.class, eclipseCtx);
		stmtInserter.postConstruct(MAX_MUTATIONS);
	}

	@Test
	public void testParameter_maxMutations() {
		MutationContext ctx = new MutationContext(HashBiMap.create());
		ctx.getCtxObjects().addAll(structuredText(ST_INSTANCE_SIZE, STMT_SIZE));

		MutationContext clonedCtx = new MutationContext(HashBiMap.create());
		clonedCtx.getCtxObjects().addAll(structuredText(ST_INSTANCE_SIZE, STMT_SIZE));

		stmtInserter.apply(clonedCtx);

		int totalChangeCount = 0;
		for (int i = 0; i < ST_INSTANCE_SIZE; i++) {
			List<Statement> ctxVar = ((StructuredText) ctx.getCtxObjects().get(i)).getStatements();
			List<Statement> mutCtxVar =  ((StructuredText) clonedCtx.getCtxObjects().get(i)).getStatements();
			
			int sizeDiff = mutCtxVar.size() - ctxVar.size();
			assertThat(sizeDiff).isGreaterThanOrEqualTo(0);
			totalChangeCount += sizeDiff;
		}
		
		assertThat(totalChangeCount).isEqualTo(3);
	}

	@RepeatedTest(50)
	public void testMultipleMutations() {
		List<Statement> stmts = statements(STMT_SIZE);
		List<Statement> stmtsCopy = (List<Statement>) EcoreUtil.copyAll(stmts);
		
		BiMap<EObject, EObject> mapping = HashBiMap.create();
		for (int i = 0; i < stmts.size(); i++) {
			mapping.putAll(constructOriginalToMutatedTreeMapping(stmts.get(i), stmtsCopy.get(i)));
		}
		
		MutationContext ctx = new MutationContext(mapping);
		ctx.getCtxObjects().addAll(stmtsCopy);
		
		int mutations = 3;
		stmtInserter.postConstruct(mutations);
		stmtInserter.apply(ctx);
		
		for (MutationPair mp : ctx.getMutationPairs()) {
			System.out.println("mutation pair: ("+mp.getOrigin()+", "+mp.getMutant()+")");			
		}
		assertThat(ctx.getMutationPairs()).allMatch(pair -> pair.hasMutant());
		assertThat(ctx.getMutationPairs()).hasSizeLessThanOrEqualTo(mutations);
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
	
	private List<Statement> statements(int stmtCount) {
		List<Statement> stmts = new ArrayList<>();
		for (int j = 0; j < stmtCount; j++) {
			stmts.add(stmtGen.generateStatement());
		}
	
		return stmts;
	}
	
	private BiMap<EObject, EObject> constructOriginalToMutatedTreeMapping(EObject original, EObject mutated) {
		BiMap<EObject, EObject> mapping = HashBiMap.create();
		
		mapping.put(original, mutated);
		
		TreeIterator<EObject> origIt = EcoreUtil.getAllProperContents(original, true);
		TreeIterator<EObject> mutatedIt = EcoreUtil.getAllProperContents(mutated, true);
		while (origIt.hasNext() && mutatedIt.hasNext()) {
			EObject origObject = origIt.next();
			EObject mutatedObject = mutatedIt.next();
			mapping.put(origObject, mutatedObject);
		}
		
		if (origIt.hasNext() || mutatedIt.hasNext()) {
			throw new InequalTreeException("Trees do not have the same structure.");
		}
		
		return mapping;
	}
}
