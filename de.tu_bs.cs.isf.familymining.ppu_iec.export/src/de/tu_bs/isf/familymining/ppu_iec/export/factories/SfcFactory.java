package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Project.ContentHeader.CoordinateInfo.Sfc;

/**
 * {@code <sfc>..</sfc>} factory.
 */
@Creatable
@Singleton
public class SfcFactory {

	/**
	 * {@code <scaling>..</scaling>} factory.
	 */
	@Inject
	public ScalingFactory scalingFactory;

	/**
	 * @return {@code <sfc>..</sfc>}.
	 */
	public Sfc createSfcInfo() {
		Sfc sfc = new Sfc();
		sfc.setScaling(scalingFactory.createSfcScaling());
		return sfc;
	}

	public Body.SFC createSfc(SequentialFunctionChart sfc) {
		Body.SFC sfcBody = new Body.SFC();
		
		// TODO: implement sfc parsing
		
		return sfcBody;
	}

}
