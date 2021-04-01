package de.tu_bs.isf.familymining.ppu_iec.export.factories.sfc;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.ComplexAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.isf.familymining.ppu_iec.export.components.selection.FMSelection;
import de.tu_bs.isf.familymining.ppu_iec.export.factories.ScalingFactory;
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
	private ScalingFactory scalingFactory;

	/**
	 * Converting {@code ComplexAction}s requires the full selection.
	 */
	@Inject
	@Optional
	private FMSelection selection;
	
	/**
	 * @return {@code <sfc>..</sfc>}.
	 */
	public Sfc createSfcInfo() {
		Sfc sfc = new Sfc();
		sfc.setScaling(scalingFactory.createSfcScaling());
		return sfc;
	}

	public Body.SFC createSfc(SequentialFunctionChart sfc) {
		SfcExporter sfcExporter = new SfcExporter(selection);
		return sfcExporter.createSfc(sfc);
	}
}
