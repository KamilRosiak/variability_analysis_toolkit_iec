package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test;

import java.util.Random;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Location;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableDeclaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableLocationDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableLocationType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChartFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ForLoop;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredTextFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.StructuredTextExpressionFactory;

public abstract class ScenarioTest {

	protected ConfigurationFactory configFactory = ConfigurationFactory.eINSTANCE;
	protected StructuredTextFactory stFactory = StructuredTextFactory.eINSTANCE;
	protected StructuredTextExpressionFactory steFactory = StructuredTextExpressionFactory.eINSTANCE;
	protected SequentialFunctionChartFactory sfcFactory = SequentialFunctionChartFactory.eINSTANCE;
	
	private Random rand = new Random();

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
	
	protected Variable createVariableWithInitialValue(String name, VariableDeclaration scope, ElementaryDataType type,
			VariableLocationDataType locDataType, VariableLocationType locType, Expression expr) {
		Variable var = createVariable(name, scope, type, locDataType, locType);
		var.setInitialValue(expr);
		
		return var;
	}
	
	protected ForLoop createForLoop(int initialValue, int increment, int upperBound) {
		ForLoop forLoop = stFactory.createForLoop();
		forLoop.setAbsStartLine(rand.nextInt(20));
		forLoop.setAbsEndLine(forLoop.getAbsStartLine()+3);
		forLoop.setStartColumnPos(rand.nextInt(20));
		forLoop.setEndColumnPos(forLoop.getStartColumnPos()+5);
		forLoop.setRelStartLine(rand.nextInt(5));
		forLoop.setRelEndLine(forLoop.getRelStartLine()+ 10);
		forLoop.setId("");
		forLoop.setIncrement(increment);
		forLoop.setInitialValue(initialValue);
		forLoop.setUpperBound(upperBound);
		
		return forLoop;
	}
}
