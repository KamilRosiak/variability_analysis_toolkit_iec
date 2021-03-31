package de.tu_bs.isf.familymining.ppu_iec.export.factories.sfc;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.ComplexAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SimpleAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.StepQualifier;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.isf.familymining.ppu_iec.export.factories.ScalingFactory;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.InVariable;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.JumpStep;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.Transition.Condition;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Connection;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.ConnectionPointIn;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.ConnectionPointOut;
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
	 * @return {@code <sfc>..</sfc>}.
	 */
	public Sfc createSfcInfo() {
		Sfc sfc = new Sfc();
		sfc.setScaling(scalingFactory.createSfcScaling());
		return sfc;
	}

	public Body.SFC createSfc(SequentialFunctionChart sfc) {
		SfcExporter sfcExporter = new SfcExporter();
		return sfcExporter.createSfc(sfc);
	}
}
