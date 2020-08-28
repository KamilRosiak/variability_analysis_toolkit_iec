package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.w3c.dom.Element;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.isf.familymining.ppu_iec.export.constants.XmlDataTable;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.FormattedText;

/**
 * {@code <ST>..</ST>} factory.
 */
@Creatable
@Singleton
public class StFactory {

	/**
	 * 
	 * 
	 * @return {@code <ST>..</ST>};
	 */
	public FormattedText createST(StructuredText st) {
		// formatted text only accepts an object of type
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element stElement = docBuilder.newDocument().createElement("xhtml");
		stElement.setTextContent(st.getText());
		stElement.setAttribute("xmlns", XmlDataTable.XHTML_NAMESPACE);
		 
		FormattedText stFormattedText = new FormattedText();
		stFormattedText.setAny(stElement);
		return stFormattedText;
	}
	
	/**
	 * Creates empty structured text tag.
	 * 
	 * @return {@code <ST>..</ST>};
	 */
	public FormattedText createST() {
		FormattedText fromattedSt = new FormattedText();
		
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element stElement = docBuilder.newDocument().createElement("xhtml");
		stElement.setAttribute("xmlns", XmlDataTable.XHTML_NAMESPACE);
		
		fromattedSt.setAny(stElement);
		
		return fromattedSt;
	}
}
