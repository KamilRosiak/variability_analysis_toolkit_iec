package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test;


import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InequalTreeException;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryExpression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryOperator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.VariableExpression;

public class MutationContextTest extends ScenarioTest {

	@Test
	public void testOverlap_singleElement_true() {
		BinaryExprTree original = createExpressionTree();
		BinaryExprTree mutated = createExpressionTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mc1 = new MutationContext(mapping);
		mc1.getCtxObjects().add(mutated.rightLeft);
		
		MutationContext mc2 = new MutationContext(mapping);
		mc2.getCtxObjects().add(mutated.rightLeft);
		
		Assertions.assertThat(mc1.sharesElementsWith(mc2)).isTrue();
	}

	@Test
	public void testOverlap_singleElement_false() {
		BinaryExprTree original = createExpressionTree();
		BinaryExprTree mutated = createExpressionTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mc1 = new MutationContext(mapping);
		mc1.getCtxObjects().add(mutated.rightRight);
		
		MutationContext mc2 = new MutationContext(mapping);
		mc2.getCtxObjects().add(mutated.rightLeft);
		
		Assertions.assertThat(mc1.sharesElementsWith(mc2)).isFalse();
	}
	
	@Test
	public void testOverlap_singleElement_otherTree_false() {
		BinaryExprTree original = createExpressionTree();
		BinaryExprTree mutated = createExpressionTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mc1 = new MutationContext(mapping);
		mc1.getCtxObjects().add(original.rightRight);
		
		MutationContext mc2 = new MutationContext(mapping);
		mc2.getCtxObjects().add(mutated.rightLeft);
		
		assertThat(mc1.sharesElementsWith(mc2)).isFalse();
	}
	
	@Test 
	public void testLogRemoval_logInsertion_logRemovalRoot() {
		StTree original = createStTree();
		StTree mutated = createStTree();		
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		ForLoop inserted = createForLoop(5, 2, 20);
		
		MutationContext mutCtx = new MutationContext(mapping);
		mutCtx.logInsertion(inserted);
		mutated.root.getStatements().add(inserted);
		mutCtx.logRemoval(mutated.root);
		

		assertThat(mutCtx.getMutationPairs()).hasSize(1);
		assertThat(mutCtx.getMutationPairs()).allMatch(mp -> mp.hasOrigin() && !mp.hasMutant());
		assertThat(mutCtx.getCtxObjects()).isEmpty();
	}
	
	@Test 
	public void testLogRemoval__logChangeChild_logRemovalRoot() {
		StTree original = createStTree();
		StTree mutated = createStTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mutCtx = new MutationContext(mapping);
		
		mutCtx.logChange(mutated.forLoop1);
		mutCtx.logRemoval(mutated.root);
		
		assertThat(mutCtx.getMutationPairs()).hasSize(1);
		assertThat(mutCtx.getMutationPairs()).allMatch(mp -> mp.hasOrigin() && !mp.hasMutant());
		assertThat(mutCtx.getCtxObjects()).isEmpty();
	}
	
	private StTree createStTree() {
		StructuredText st = createSt("testLogRemoval");
		
		ForLoop forLoop1 = createForLoop(0, 1, 10);
		ForLoop forLoop2 = createForLoop(10, -1, 0);

		st.getStatements().add(forLoop1);
		st.getStatements().add(forLoop2);
		
		StTree stTree = new StTree();
		stTree.root = st;
		stTree.forLoop1 = forLoop1;
		stTree.forLoop2 = forLoop2;
		
		return stTree;
	}
	
	private static class StTree {
		public StructuredText root;
		public ForLoop forLoop1;
		public ForLoop forLoop2;
	} 
	
	
	//      (+)
	//     /  \
	// (var)  (+)
	//       /   \
	//    (var)  (var)
	private BinaryExprTree createExpressionTree() {
		BinaryExprTree tree = new BinaryExprTree();
		tree.rightRight = createVariable(); 
		tree.rightLeft = createVariable();
		tree.right = createBinaryExpr(tree.rightLeft, tree.rightRight, BinaryOperator.ADD);
		tree.left = createVariable();
		tree.root = createBinaryExpr(tree.left, tree.right, BinaryOperator.ADD);
		return tree;
	}

	private VariableExpression createVariable() {
		VariableExpression var = createVariableExpression("test", ElementaryDataType.INT);
		return var;
	}
	
	private static class BinaryExprTree {
		public BinaryExpression root;
		public VariableExpression left;
		public BinaryExpression right;
		public VariableExpression rightLeft;
		public VariableExpression rightRight;
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
