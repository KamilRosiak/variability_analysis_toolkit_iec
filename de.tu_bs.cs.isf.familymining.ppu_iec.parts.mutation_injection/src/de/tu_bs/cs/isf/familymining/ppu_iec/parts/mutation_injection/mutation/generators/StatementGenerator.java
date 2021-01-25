package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators;

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

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Assignment;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Case;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.CaseBlock;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ConditionalBlock;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.FunctionCallStatement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.If;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StatementType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredTextFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.UnboundedLoop;
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

@Creatable
public class StatementGenerator {
	
	private final StructuredTextFactory stFactory = StructuredTextFactory.eINSTANCE;
	private final StructuredTextExpressionFactory steFactory = StructuredTextExpressionFactory.eINSTANCE;

	private final float SUB_STATEMENT_CHANCE = 0.15f;

	private Randomization randomly;

	public StatementGenerator() {
		this.randomly = new Randomization();
	}
	
	public Statement generateStatement() {
		StatementType type = randomly.pickFrom(StatementType.ASSIGNMENT, StatementType.FUNCTION_CALL, StatementType.IF,
				StatementType.CASE, StatementType.WHILE, StatementType.REPEAT, StatementType.FOR_LOOP);

		switch (type) {
		case ASSIGNMENT:
			return generateAssignment();
		case FUNCTION_CALL:
			return generateFunctionCallStatement();
		case IF:
			return generateIfStatement();
		case CASE:
			return generateCaseStatment();
		case WHILE:
			return generateWhileLoop();
		case REPEAT:
			return generateRepeatLoop();
		case FOR_LOOP:
			return generateForLoop();
		default:
			throw new IllegalArgumentException();
		}
	}

	public Statement generateSimpleStatement() {
		StatementType type = randomly.pickFrom(StatementType.ASSIGNMENT, StatementType.FUNCTION_CALL);

		switch (type) {
		case ASSIGNMENT:
			return generateAssignment();
		case FUNCTION_CALL:
			return generateFunctionCallStatement();
		default:
			throw new IllegalArgumentException();
		}
	}

	public Statement generateNestedStatement() {
		StatementType type = randomly.pickFrom(StatementType.IF, StatementType.CASE, StatementType.WHILE,
				StatementType.REPEAT, StatementType.FOR_LOOP);

		switch (type) {
		case IF:
			return generateIfStatement();
		case CASE:
			return generateCaseStatment();
		case WHILE:
			return generateWhileLoop();
		case REPEAT:
			return generateRepeatLoop();
		case FOR_LOOP:
			return generateForLoop();
		default:
			throw new IllegalArgumentException();
		}
	}

	public Assignment generateAssignment() {
		Assignment assignment = stFactory.createAssignment();
		assignment.setStatementType(StatementType.ASSIGNMENT);
		attachMetaData(assignment);

		ElementaryDataType dataType = randomly.pickFrom(INT, REAL, STRING, BOOL);
		assignment.setLeft(generateVariableExpression(dataType));
		assignment.setRight(generateExpression(assignment.getLeft().getDataType()));

		return assignment;
	}

	public FunctionCallStatement generateFunctionCallStatement() {
		FunctionCallStatement func = stFactory.createFunctionCallStatement();
		func.setStatementType(StatementType.FUNCTION_CALL);
		attachMetaData(func);

		ElementaryDataType dataType = randomly.pickFrom(INT, REAL, STRING, BOOL);
		func.setInvokingVariable(generateVariableExpression(DERIVED));
		func.setFunctionCall(generateFunctionCallExpression(dataType));

		return func;
	}
	
	public If generateIfStatement() {
		If ifStatement = stFactory.createIf();
		ifStatement.setStatementType(StatementType.IF);
		attachMetaData(ifStatement);
		
		do {
			ConditionalBlock conditionalBlock = stFactory.createConditionalBlock();
			conditionalBlock.setStatementType(StatementType.CONDITIONAL_BLOCK);
			attachMetaData(conditionalBlock);
			
			conditionalBlock.setCondition(generateExpression(BOOL));
			ifStatement.getConditionalBlocks().add(conditionalBlock);	
		} while (randomly.nextFloat() < SUB_STATEMENT_CHANCE);
		
		return ifStatement;
	}

	public Case generateCaseStatment() {
		Case caseStatement = stFactory.createCase();
		caseStatement.setStatementType(StatementType.CASE);
		attachMetaData(caseStatement);
		
		do {
			CaseBlock caseBlock = stFactory.createCaseBlock();
			caseBlock.setStatementType(StatementType.CASE_BLOCK);
			attachMetaData(caseBlock);
			
			caseBlock.getCaseExpressions().add(generateExpression(INT));
			caseStatement.getCases().add(caseBlock);	
		} while (randomly.nextFloat() < SUB_STATEMENT_CHANCE);
		
		return caseStatement;
	}

	public UnboundedLoop generateWhileLoop() {
		UnboundedLoop whileStatement = stFactory.createUnboundedLoop();
		whileStatement.setStatementType(StatementType.WHILE);
		attachMetaData(whileStatement);

		whileStatement.setCondition(generateExpression(BOOL));
		
		do {
			Statement simpleStatement = generateSimpleStatement();
			whileStatement.getSubstatements().add(simpleStatement);
		} while (randomly.nextFloat() < SUB_STATEMENT_CHANCE);

		return whileStatement;	
	}

	public Statement generateRepeatLoop() {
		UnboundedLoop repeatStatement = stFactory.createUnboundedLoop();
		repeatStatement.setStatementType(StatementType.REPEAT);
		attachMetaData(repeatStatement);

		repeatStatement.setCondition(generateExpression(BOOL));
		
		do {
			Statement simpleStatement = generateSimpleStatement();
			repeatStatement.getSubstatements().add(simpleStatement);
		} while (randomly.nextFloat() < SUB_STATEMENT_CHANCE);

		return repeatStatement;	
	}

	public ForLoop generateForLoop() {
		ForLoop forLoop = stFactory.createForLoop();
		forLoop.setStatementType(StatementType.FOR_LOOP);
		attachMetaData(forLoop);

		forLoop.setCounter(generateVariableExpression(ElementaryDataType.INT));
		forLoop.setInitialValue(randomly.nextInt(5));
		forLoop.setIncrement(randomly.nextInt(3));
		forLoop.setUpperBound(forLoop.getInitialValue() + randomly.nextInt(20) * forLoop.getIncrement());

		do {
			Statement simpleStatement = generateSimpleStatement();
			forLoop.getSubstatements().add(simpleStatement);
		} while (randomly.nextFloat() < SUB_STATEMENT_CHANCE);

		return forLoop;
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
	public Expression generateExpression(ElementaryDataType returnType) {
		if (!isInt(returnType) && !isReal(returnType) && !isString(returnType) && returnType != BOOL) {
			throw new IllegalArgumentException("Wrong data type.");
		}

		// return type determines the compatible set of expression types
		ExpressionType exprType;
		if (isString(returnType)) {
			exprType = randomly.pickFrom(LITERAL, VARIABLE);
		} else {
			if (randomly.nextFloat() < SUB_STATEMENT_CHANCE) {
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

	public Expression generateUnaryExpression(ElementaryDataType returnType) {
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

	public Expression generateBinaryExpression(ElementaryDataType returnType) {
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
	public FunctionCallExpression generateFunctionCallExpression(ElementaryDataType dataType) {
		FunctionCallExpression funcCallExpr = steFactory.createFunctionCallExpression();
		funcCallExpr.setExpressionType(ExpressionType.FUNCTION_CALL);
		attachMetaData(funcCallExpr);

		funcCallExpr.setSymbol(RandomStringUtils.randomAlphabetic(5));
		funcCallExpr.setDataType(dataType);

		return funcCallExpr;
	}

	public Expression generateLiteral(ElementaryDataType returnType) {
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

	public VariableExpression generateVariableExpression(ElementaryDataType returnType) {
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
