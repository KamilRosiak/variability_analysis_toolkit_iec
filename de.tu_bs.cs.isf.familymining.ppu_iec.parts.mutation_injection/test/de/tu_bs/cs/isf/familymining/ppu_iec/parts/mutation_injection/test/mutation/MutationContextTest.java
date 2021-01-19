package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.mutation;


import org.assertj.core.api.Assertions;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.InequalTreeException;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test.ScenarioTest;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryExpression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryOperator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.VariableExpression;

public class MutationContextTest extends ScenarioTest {

	@Test
	public void testOverlap_singleElement_true() {
		BinaryExprTree original = createTestTree();
		BinaryExprTree mutated = createTestTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mc1 = new MutationContext(mapping);
		mc1.getCtxObjects().add(mutated.rightLeft);
		
		MutationContext mc2 = new MutationContext(mapping);
		mc2.getCtxObjects().add(mutated.rightLeft);
		
		Assertions.assertThat(mc1.sharesElementsWith(mc2)).isTrue();
	}

	@Test
	public void testOverlap_singleElement_false() {
		BinaryExprTree original = createTestTree();
		BinaryExprTree mutated = createTestTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mc1 = new MutationContext(mapping);
		mc1.getCtxObjects().add(mutated.rightRight);
		
		MutationContext mc2 = new MutationContext(mapping);
		mc2.getCtxObjects().add(mutated.rightLeft);
		
		Assertions.assertThat(mc1.sharesElementsWith(mc2)).isFalse();
	}
	
	@Test
	public void testOverlap_singleElement_otherTree_false() {
		BinaryExprTree original = createTestTree();
		BinaryExprTree mutated = createTestTree();
		BiMap<EObject, EObject> mapping = constructOriginalToMutatedTreeMapping(original.root, mutated.root);
		
		MutationContext mc1 = new MutationContext(mapping);
		mc1.getCtxObjects().add(original.rightRight);
		
		MutationContext mc2 = new MutationContext(mapping);
		mc2.getCtxObjects().add(mutated.rightLeft);
		
		Assertions.assertThat(mc1.sharesElementsWith(mc2)).isFalse();
	}
	
	//      (+)
	//     /  \
	// (var)  (+)
	//       /   \
	//    (var)  (var)
	private BinaryExprTree createTestTree() {
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
