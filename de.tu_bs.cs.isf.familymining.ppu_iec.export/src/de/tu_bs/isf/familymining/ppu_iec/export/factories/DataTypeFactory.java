package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tu_bs.cs.isf.familymining.ppu_iec.code_gen.st.ExpressionToStringExporter;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ArrayVariable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Struct;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;

/**
 * {@code <dataType>..</dataType>}
 * 
 * @author Oliver Urbaniak
 *
 */
@Creatable
@Singleton
public class DataTypeFactory {

	/**
	 * {@code <data>..</data>}
	 */
	@Inject
	DataFactory dataFactory;
	
	private Document document;
	
	public Element createDataType(Struct struct) {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}	
		
		Element dataTypeElement = document.createElement("dataType");
		dataTypeElement.setAttribute("name", struct.getName());
		
		Element baseTypeElement = document.createElement("baseType");
		dataTypeElement.appendChild(baseTypeElement);
		
		Element structElement = createStruct(struct);
		baseTypeElement.appendChild(structElement);
		
		return dataTypeElement;
	}
	
	/**
	 * @param struct
	 * @return {@code <struct>..</struct>}
	 */
	private Element createStruct(Struct struct) {
		Element structElement = document.createElement("struct");
		for (Variable var : struct.getVariables()) {
			Element variableElement  = createVariableElement(var);
			structElement.appendChild(variableElement);
		}
		
		
		return structElement;
	}

	private Element createVariableElement(Variable var) {
		Element varElement = document.createElement("variable");
		varElement.setAttribute("name", var.getName());
		
		Element typeElement = document.createElement("type");
		varElement.appendChild(typeElement);
		
		if (var instanceof ArrayVariable) {
			ArrayVariable array = (ArrayVariable) var;
			
			Element arrayElement = document.createElement("array");
			typeElement.appendChild(arrayElement);
			
			Element dimensionElement = document.createElement("dimension");
			arrayElement.appendChild(dimensionElement);
			
			ExpressionToStringExporter exprExporter = new ExpressionToStringExporter();
			String lowerBoundString = exprExporter.apply(array.getLowerBound());
			String upperBoundString = exprExporter.apply(array.getUpperBound());
			dimensionElement.setAttribute("lower", lowerBoundString);
			dimensionElement.setAttribute("upper", upperBoundString);
			
			Element baseTypeElement = document.createElement("baseType");
			arrayElement.appendChild(baseTypeElement);
			
			Element concreteTypeElement = document.createElement(array.getTypeName());
			baseTypeElement.appendChild(concreteTypeElement);
		} else {
			Element concreteTypeElement = document.createElement(var.getTypeName());
			typeElement.appendChild(concreteTypeElement);
		}
		
		return varElement;
	}
}
