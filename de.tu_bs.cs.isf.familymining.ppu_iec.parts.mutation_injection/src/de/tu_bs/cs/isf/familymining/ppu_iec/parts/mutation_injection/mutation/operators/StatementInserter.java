package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.BOOL;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.DERIVED;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.DINT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.INT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.LINT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.LREAL;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.REAL;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.SINT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.STRING;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.UINT;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType.WSTRING;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ExpressionType.BINARY;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ExpressionType.LITERAL;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ExpressionType.UNARY;
import static de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ExpressionType.VARIABLE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Assignment;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.FunctionCallStatement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StatementType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredTextFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryExpression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryOperator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ExpressionType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.FunctionCallExpression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Literal;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.StructuredTextExpressionFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.UnaryExpression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.UnaryOperator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.VariableExpression;

public class StatementInserter extends StatementMutation {
	private final StructuredTextFactory stFactory = StructuredTextFactory.eINSTANCE;
	private final StructuredTextExpressionFactory steFactory = StructuredTextExpressionFactory.eINSTANCE;
	
	private final float REC_CHANCE = 0.3f;

	private int maxMutations;

	private Randomization randomly;

	@PostConstruct
	public void postConstruct(
			@Preference(nodePath = MUTATION_PREF, value = MutationParameters.STMT_INS_MAX_MUTATIONS) int maxMutations) {
		this.maxMutations = maxMutations;
		this.randomly = new Randomization();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MutationContext apply(MutationContext ctx) {
		int mutationCount = 0;

		List<EObject> randomized = ctx.getCtxObjects().stream().filter(statementContainers())
				.collect(Collectors.toList());
		Collections.shuffle(randomized);

		Iterator<EObject> it = randomized.iterator();
		while (it.hasNext() && mutationCount < maxMutations) {
			EObject stmtContainer = it.next();
			List<EReference> stmtRefs = stmtContainer.eClass().getEAllContainments().stream()
					.filter(this::isStatementListRef).collect(Collectors.toList());

			for (EReference stmtRef : stmtRefs) {
				EList<Statement> stmts = (EList<Statement>) stmtContainer.eGet(stmtRef, true);
				Assignment assignment = generateAssignment();
				if (stmtContainer instanceof StructuredText) {
					StructuredText st = (StructuredText) stmtContainer;
					assignment.setStructuredText(st);
				} else if (stmtContainer instanceof Statement) {
					Statement stmt = (Statement) stmtContainer;
					assignment.setStructuredText(stmt.getStructuredText());
				}
				stmts.add(assignment);
				
				// log change
				ctx.logInsertion(assignment);
				ctx.setChangedTreeStructure(true);
			}
		}

		return ctx;
	}

	private Assignment generateAssignment() {
		Assignment assignment = stFactory.createAssignment();
		assignment.setStatementType(StatementType.ASSIGNMENT);
		attachMetaData(assignment);

		ElementaryDataType dataType = randomly.pickFrom(INT, REAL, STRING, BOOL);
		assignment.setLeft(generateVariableExpression(dataType));
		assignment.setRight(generateExpression(assignment.getLeft().getDataType()));

		return assignment;
	}

	private FunctionCallStatement generateFunctionCallStatement(Statement reference) {
		FunctionCallStatement func = stFactory.createFunctionCallStatement();
		func.setStatementType(StatementType.FUNCTION_CALL);
		attachMetaData(func);

		ElementaryDataType dataType = randomly.pickFrom(INT, REAL, STRING, BOOL);
		func.setInvokingVariable(generateVariableExpression(DERIVED));
		func.setFunctionCall(generateFunctionCallExpression(dataType));

		return func;
	}

	private void attachMetaData(Statement stmt) {
		stmt.setId(RandomStringUtils.randomAlphanumeric(10));
		stmt.setAbsStartLine(0);
		stmt.setAbsEndLine(0);
		stmt.setRelStartLine(0);
		stmt.setRelEndLine(0);
		stmt.setStartColumnPos(0);
		stmt.setEndColumnPos(0);
	}
	
	private void attachMetaData(Expression expr) {
		expr.setId(RandomStringUtils.randomAlphanumeric(10));
	}

	/**
	 * Generates Expression conforming to provided data type.
	 * 
	 * Accepts
	 * <ul>
	 * <li>INT including variants
	 * <li>REAL including variants
	 * <li>STRING including variants
	 * <li>BOOL
	 * </ul>
	 * 
	 * @param returnType
	 * @return
	 */
	private Expression generateExpression(ElementaryDataType returnType) {
		if (!isInt(returnType) && !isReal(returnType) && !isString(returnType) && returnType != BOOL) {
			throw new IllegalArgumentException("Wrong data type.");
		}

		// return type determines the compatible set of expression types
		ExpressionType exprType;
		if (isString(returnType)) {
			exprType = randomly.pickFrom(LITERAL, VARIABLE);
		} else {
			if (randomly.nextFloat() < REC_CHANCE) {
				exprType = randomly.pickFrom(UNARY, BINARY);
			} else {
				exprType = randomly.pickFrom(LITERAL, VARIABLE);
			}
		}

		switch (exprType) {
		case UNARY:
			return generateUnaryExpression(returnType);
		case BINARY:
			return generateBinaryExpression(returnType);
		case LITERAL:
			return generateLiteral(returnType);
		case VARIABLE:
			return generateVariableExpression(returnType);
		default:
			break;
		}
		return null;
	}

	private Expression generateUnaryExpression(ElementaryDataType returnType) {
		UnaryExpression unaryExpression = steFactory.createUnaryExpression();
		unaryExpression.setExpressionType(UNARY);
		attachMetaData(unaryExpression);

		if (isInt(returnType) || isReal(returnType)) {
			unaryExpression.setOperator(
					randomly.pickFrom(UnaryOperator.POSITIVE, UnaryOperator.NEGATIVE, UnaryOperator.PARENTHESIS));
		} else if (isString(returnType)) {
			return generateExpression(returnType); // no unary op on strings (except "()"), so defer back to the
													// expression generator
		} else if (returnType == BOOL) {
			unaryExpression.setOperator(UnaryOperator.NOT);
		} else {
			return null;
		}

		unaryExpression.setSubexpression(generateExpression(returnType));
		return unaryExpression;
	}

	private Expression generateBinaryExpression(ElementaryDataType returnType) {
		BinaryExpression binaryExpression = steFactory.createBinaryExpression();
		binaryExpression.setExpressionType(BINARY);
		attachMetaData(binaryExpression);

		if (isInt(returnType) || isReal(returnType)) {
			binaryExpression.setOperator(randomly.pickFrom(BinaryOperator.SUBTRACT, BinaryOperator.ADD,
					BinaryOperator.DIV, BinaryOperator.MULT, BinaryOperator.EXP));
		} else if (isString(returnType)) {
			return generateExpression(returnType); // no unary op on strings (except "()"), so defer back to the
													// expression generator
		} else if (returnType == BOOL) {
			binaryExpression.setOperator(randomly.pickFrom(BinaryOperator.AND, BinaryOperator.OR, BinaryOperator.XOR,
					BinaryOperator.EQUAL, BinaryOperator.GREATER_EQUAL, BinaryOperator.LESSER_EQUAL,
					BinaryOperator.GREATER_THAN, BinaryOperator.LESSER_THAN, BinaryOperator.NOT_EQUAL));
		} else {
			return null;
		}

		binaryExpression.setLeft(generateExpression(returnType));
		binaryExpression.setRight(generateExpression(returnType));
		return binaryExpression;
	}

	/**
	 * Generates parameterless function.
	 * 
	 * @param dataType
	 * @return
	 */
	private FunctionCallExpression generateFunctionCallExpression(ElementaryDataType dataType) {
		FunctionCallExpression funcCallExpr = steFactory.createFunctionCallExpression();
		funcCallExpr.setExpressionType(ExpressionType.FUNCTION_CALL);
		attachMetaData(funcCallExpr);

		funcCallExpr.setSymbol(RandomStringUtils.randomAlphabetic(5));
		funcCallExpr.setDataType(dataType);

		return funcCallExpr;
	}

	private Expression generateLiteral(ElementaryDataType returnType) {
		Literal lit = steFactory.createLiteral();
		lit.setExpressionType(ExpressionType.LITERAL);
		attachMetaData(lit);
		
		lit.setDataType(returnType);
		if (isInt(returnType)) {
			lit.setSymbol(RandomStringUtils.randomNumeric(3));
		} else if (isReal(returnType)) {
			lit.setSymbol(RandomStringUtils.randomNumeric(3) + ".0");
		} else if (isString(returnType)) {
			lit.setSymbol(RandomStringUtils.randomAlphabetic(5));
		} else if (returnType == BOOL) {
			lit.setSymbol(randomly.pickFrom("TRUE", "FALSE"));
		} else {
			return null;
		}
		return lit;
	}

	private VariableExpression generateVariableExpression(ElementaryDataType returnType) {
		VariableExpression varExpr = steFactory.createVariableExpression();
		varExpr.setExpressionType(VARIABLE);
		attachMetaData(varExpr);

		varExpr.setDataType(returnType);
		varExpr.setSymbol(RandomStringUtils.randomAlphanumeric(5));
		return varExpr;
	}

	private boolean isString(ElementaryDataType dataType) {
		return Arrays.asList(STRING, WSTRING).contains(dataType);
	}

	private boolean isReal(ElementaryDataType dataType) {
		return Arrays.asList(REAL, LREAL).contains(dataType);
	}

	private boolean isInt(ElementaryDataType dataType) {
		return Arrays.asList(INT, DINT, SINT, UINT, LINT).contains(dataType);
	}
}
