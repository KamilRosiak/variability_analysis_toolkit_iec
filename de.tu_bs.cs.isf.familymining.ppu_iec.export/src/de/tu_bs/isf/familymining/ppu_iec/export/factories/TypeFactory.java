package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.w3c.dom.Document;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.DataType;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.DataType.Derived;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.DataType.Wstring;

/**
 * {@code <type>..</type>} factory.
 */
@Creatable
@Singleton
public class TypeFactory {

	/**
	 * @param variable Variable.
	 * @return {@code <type>..</type>} with inner type.
	 */
	public DataType createDataType(Variable variable) {

		DataType dataType = new DataType();

		// create dummy xml document required for type setting below
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch (variable.getType()) {
		case BOOL:
			dataType.setBOOL(doc.createElement(variable.getTypeName()));
			break;
		case BYTE:
			dataType.setBYTE(doc.createElement(variable.getTypeName()));
			break;
		case DATE:
			dataType.setDATE(doc.createElement(variable.getTypeName()));
			break;
		case DATE_AND_TIME:
			dataType.setANYDATE(doc.createElement(variable.getTypeName()));
			break;
		case DERIVED:
			Derived derived = new Derived();
			derived.setName(variable.getTypeName());
			dataType.setDerived(derived);
			break;
		case DINT:
			dataType.setDINT(doc.createElement(variable.getTypeName()));
			break;
		case DWORD:
			dataType.setDWORD(doc.createElement(variable.getTypeName()));
			break;
		case INT:
			dataType.setINT(doc.createElement(variable.getTypeName()));
			break;
		case LINT:
			dataType.setLINT(doc.createElement(variable.getTypeName()));
			break;
		case LREAL:
			dataType.setLREAL(doc.createElement(variable.getTypeName()));
			break;
		case LWORD:
			dataType.setLWORD(doc.createElement(variable.getTypeName()));
			break;
		case REAL:
			dataType.setREAL(doc.createElement(variable.getTypeName()));
			break;
		case SINT:
			dataType.setSINT(doc.createElement(variable.getTypeName()));
			break;
		case STRING:
			dataType.setString(new DataType.String());
			break;
		case TIME:
			dataType.setTIME(doc.createElement(variable.getTypeName()));
			break;
		case TIME_OF_DAY:
			dataType.setTIME(doc.createElement(variable.getTypeName()));
			break;
		case TOD:
			dataType.setTOD(doc.createElement(variable.getTypeName()));
			break;
		case UDINT:
			dataType.setUDINT(doc.createElement(variable.getTypeName()));
			break;
		case UINT:
			dataType.setUINT(doc.createElement(variable.getTypeName()));
			break;
		case UNSET:
			dataType.setANY(doc.createElement(variable.getTypeName()));
			break;
		case USINT:
			dataType.setUSINT(doc.createElement(variable.getTypeName()));
			break;
		case WORD:
			dataType.setWORD(doc.createElement(variable.getTypeName()));
			break;
		case WSTRING:
			dataType.setWstring(new Wstring());
			break;
		default:
			dataType.setANY(doc.createElement(variable.getTypeName()));
			break;
		}

		return dataType;
	}

}
