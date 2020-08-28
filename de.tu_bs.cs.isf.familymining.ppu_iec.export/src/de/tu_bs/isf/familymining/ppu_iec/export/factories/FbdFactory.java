package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Diagram;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.DiagramType;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Project.ContentHeader.CoordinateInfo.Fbd;

/**
 * {@code <fbd>..</fbd>} factory.
 */
@Creatable
@Singleton
public class FbdFactory {

	/**
	 * {@code <scaling>..</scaling>} factory.
	 */
	@Inject
	public ScalingFactory scalingFactory;

	/**
	 * @return {@code <fbd>..</fbd>}.
	 */
	public Fbd createFbd() {
		Fbd fbd = new Fbd();
		fbd.setScaling(scalingFactory.createFbdScaling());
		return fbd;
	}

	/**
	 * {@code <FBD>..</FBD>}
	 * 
	 * @param fbd
	 */
	public Body.FBD createFbd(Diagram fbd) {
		if (fbd.getType() != DiagramType.FUNCTION_BLOCK_DIAGRAM) {
			return null;
		}
		
		Body.FBD fbdInstance = new Body.FBD();
		
		// TODO: implement fbd parsing
		
		return fbdInstance;
	}

}
