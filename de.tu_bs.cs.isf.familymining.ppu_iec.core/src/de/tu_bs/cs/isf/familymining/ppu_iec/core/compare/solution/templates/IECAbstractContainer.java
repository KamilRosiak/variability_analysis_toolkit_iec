package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;

/**
 * This class represents a abstract container thats holds two elements, the similarity and all results.
 * @author {Kamil Rosiak}
 *
 * @param <ElementType> Type of elements to compare.
 * @param <ResultType> Type of result elements.
 */
public abstract class IECAbstractContainer<ElementType extends EObject> extends AbstractContainer<ElementType, MetricContainer>  implements ICompareCheck {
	
	private static final long serialVersionUID = 1521277552874459308L;
	
	public IECAbstractContainer(ElementType first, ElementType second, MetricContainer metric) {
		super(first, second, metric);
	}
}
