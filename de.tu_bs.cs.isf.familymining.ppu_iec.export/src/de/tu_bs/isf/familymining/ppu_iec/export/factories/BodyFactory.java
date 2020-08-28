package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Diagram;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.DiagramType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.languageelement.LanguageElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body;

/**
 * {@code <body>..</body>} factory.
 */
@Creatable
@Singleton
public class BodyFactory {

	/**
	 * {@code <ST>..</ST>} factory.
	 */
	@Inject
	public StFactory stFactory;
	
	/**
	 * {@code <SFC>..</SFC>} factory.
	 */
	@Inject 
	public SfcFactory sfcFactory;
	
	/**
	 * {@code <LD>..</LD>} factory.
	 */
	@Inject 
	public LdFactory ldFactory;
	
	/**
	 * {@code <FBD>..</FBD>} factory.
	 */
	@Inject 
	public FbdFactory fbdFactory;

	/**
	 * @return {@code <body>..</body>}.
	 */
	public Body createBody() {
		Body body = new Body();
		body.setST(stFactory.createST());
		return body;
	}
	
	/**
	 * @return {@code <body>..</body>}.
	 */
	public Body createBody(LanguageElement langElement) {
		Body body = new Body();
		if (langElement instanceof StructuredText) {
			StructuredText st = (StructuredText) langElement;
			body.setST(stFactory.createST(st));
		} else if (langElement instanceof SequentialFunctionChart) {
			SequentialFunctionChart sfc = (SequentialFunctionChart) langElement;
			body.setSFC(sfcFactory.createSfc(sfc));
		} else if (langElement instanceof Diagram && ((Diagram) langElement).getType() == DiagramType.LADDER_DIAGRAM) {
			Diagram ld = (Diagram) langElement;
			body.setLD(ldFactory.createLd(ld));
		} else if (langElement instanceof Diagram && ((Diagram) langElement).getType() == DiagramType.FUNCTION_BLOCK_DIAGRAM) {
			Diagram fbd = (Diagram) langElement;
			body.setFBD(fbdFactory.createFbd(fbd));
		} else {
			body.setST(stFactory.createST());
			
		}
		return body;
	}
}
