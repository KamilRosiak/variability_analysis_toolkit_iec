package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Location;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableDeclaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableLocationDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableLocationType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChartFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredTextFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryExpression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.BinaryOperator;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ExpressionType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.StructuredTextExpressionFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.VariableExpression;

public abstract class ScenarioTest {

	protected ConfigurationFactory configFactory = ConfigurationFactory.eINSTANCE;
	protected StructuredTextFactory stFactory = StructuredTextFactory.eINSTANCE;
	protected StructuredTextExpressionFactory steFactory = StructuredTextExpressionFactory.eINSTANCE;
	protected SequentialFunctionChartFactory sfcFactory = SequentialFunctionChartFactory.eINSTANCE;
	
	protected Randomization randomly = new Randomization();
	
	protected BinaryExpression createBinaryExpr(Expression left, Expression right, BinaryOperator op) {
		BinaryExpression binary = steFactory.createBinaryExpression();
		binary.setLeft(left);
		binary.setRight(right);
		binary.setOperator(op);
		binary.setId("");
		return binary;
	}
	
	protected Variable createVariable(String name, VariableDeclaration scope, ElementaryDataType type,
			VariableLocationDataType locDataType, VariableLocationType locType) {
		Variable var = configFactory.createVariable();
		var.setName(name);
		var.setScope(scope);
		var.setType(type);
		var.setTypeName("");
		Location loc = configFactory.createLocation();
		loc.setDataType(locDataType);
		loc.setType(locType);
		var.setLocation(loc);
		var.setArray(false);
		
		return var;
	}
	
	protected VariableExpression createVariableExpression(String symbol, ElementaryDataType type) {
		VariableExpression varExpr = steFactory.createVariableExpression();
		varExpr.setId("");
		varExpr.setSymbol(symbol);
		varExpr.setExpressionType(ExpressionType.VARIABLE);
		varExpr.setDataType(type);
		return varExpr;
	}
	
	protected Variable createVariableWithInitialValue(String name, VariableDeclaration scope, ElementaryDataType type,
			VariableLocationDataType locDataType, VariableLocationType locType, Expression expr) {
		Variable var = createVariable(name, scope, type, locDataType, locType);
		var.setInitialValue(expr);
		
		return var;
	}
	
	protected StructuredText createSt(String label) {
		StructuredText st = stFactory.createStructuredText();
		st.setId(RandomStringUtils.random(5));
		st.setLabel(label);
		st.setText("text");
		
		return st;	
	}
	
	protected ForLoop createForLoop(int initialValue, int increment, int upperBound) {
		ForLoop forLoop = stFactory.createForLoop();
		forLoop.setAbsStartLine(randomly.nextInt(20));
		forLoop.setAbsEndLine(forLoop.getAbsStartLine()+3);
		forLoop.setStartColumnPos(randomly.nextInt(20));
		forLoop.setEndColumnPos(forLoop.getStartColumnPos()+5);
		forLoop.setRelStartLine(randomly.nextInt(5));
		forLoop.setRelEndLine(forLoop.getRelStartLine()+ 10);
		forLoop.setId("");
		forLoop.setIncrement(increment);
		forLoop.setInitialValue(initialValue);
		forLoop.setUpperBound(upperBound);
		
		return forLoop;
	}
}
