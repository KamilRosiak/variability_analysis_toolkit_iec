package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Identifier;

/**
 * A filter that rejects mutations on meta data.
 * 
 * @author Oliver Urbaniak
 *
 */
public class AttributeFilter implements BiPredicate<EObject, EAttribute> {

	private FilterEntry[] exclusions = { entry(Identifier.class, "id"), entry(Statement.class, "absStartLine"),
			entry(Statement.class, "absEndLine"), entry(Statement.class, "relStartLine"),
			entry(Statement.class, "relEndLine"), entry(Statement.class, "startColumnPos"),
			entry(Statement.class, "endColumnPos"), entry(Statement.class, "statementType"),
			entry(StructuredText.class, "label"), entry(SequentialFunctionChart.class, "label"), 
			entry(Transition.class, "local_ID"), entry(Step.class, "local_ID"),
			entry(AbstractAction.class, "localId") };

	/**
	 * Tests if the EObject's attribute is allowed to be mutated.
	 */
	@Override
	public boolean test(EObject scenarioObject, EAttribute attr) {
		return Stream.of(exclusions).noneMatch(exclusion -> exclusion.clazz.isAssignableFrom(scenarioObject.getClass())
				&& exclusion.attrName.equals(attr.getName()));
	}

	public static FilterEntry entry(Class<?> clazz, String attrName) {
		return new FilterEntry(clazz, attrName);
	}

	private static class FilterEntry {
		private Class<?> clazz;
		private String attrName;

		public FilterEntry(Class<?> clazz, String attrName) {
			this.clazz = clazz;
			this.attrName = attrName;
		}
	}
}
