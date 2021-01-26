package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractOption;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.CompareEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.match.matcher.SortingMatcher;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.util.IECCompareUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.languageelement.LanguageElement;

@Creatable
public class ScenarioComparator {
	
	private static final float MANDATORY_VALUE = 1.0f;

	/**
	 * This method compares two IEC61131-3 models using the default metric and
	 * returns the ConfigurationResultRoot which contains all detailed information
	 * of the comparison.
	 */
	public ConfigurationResultRoot compare(Configuration first, Configuration second) {
		CompareEngine compareEngine = new CompareEngine(IECCompareUtil.getDefaultMetric());
		ConfigurationResultRoot resultRoot = compareEngine.compareModelList(Arrays.asList(first), Arrays.asList(second),
				IECCompareUtil.getDefaultMetric());
		
		// Update of the similarity before and after the matching
		resultRoot.updateSimilarity();

		SortingMatcher matcher = new SortingMatcher();
		matcher.match(resultRoot);
		resultRoot.updateSimilarity();
		return resultRoot;
	}
	
	public ConfigurationResultRoot compare(Configuration first, Configuration second, MetricContainer metricContainer) {
		CompareEngine compareEngine = new CompareEngine(metricContainer);
		ConfigurationResultRoot resultRoot = compareEngine.compareModelList(Arrays.asList(first), Arrays.asList(second),
				IECCompareUtil.getDefaultMetric());
		// Update of the similarity before and after the matching
		resultRoot.updateSimilarity();

		SortingMatcher matcher = new SortingMatcher();
		matcher.match(resultRoot);
		resultRoot.updateSimilarity();
		return resultRoot;
	}
	
	/**
	 * This method returns all artifacts pairs, which show changes.
	 */
	public List<AbstractContainer> findChanges(ConfigurationResultRoot result) {
		return findChanges(result.getChildren().stream().map(e -> (AbstractContainer) e).collect(Collectors.toList()),
				new ArrayList<AbstractContainer>());
	}

	/**
	 * This method returns all changed artifacts of a list of AbstractContainer as
	 * List of AbstractContainer.
	 */
	public List<AbstractContainer> findChanges(List<AbstractContainer> containers,
			List<AbstractContainer> results) {
		for (AbstractContainer container : containers) {
			if(container.getFirst() != null && container.getFirst() instanceof LanguageElement || container.getSecond() != null && container.getSecond() instanceof LanguageElement ) {
				//filters LanguageElements
			} else if (container.getFirst() == null || container.getSecond() == null) {
				results.add(container);
			} else if (checkAttributeSimilarity(container.getResults())) {
				results.add(container);
			}

			for (Object option : container.getOptions()) {
				if (option != null) {
					findChanges(((AbstractOption) option).getAllContainer(), results);
				}
			}
		}
		return results;
	}

	/**
	 * This method checks if the similarity of a list of results has an element with
	 * a similarity that is lower than 1, which indicates that this element was
	 * changed.
	 */
	private boolean checkAttributeSimilarity(List<ResultElement> list) {
		return list.stream().anyMatch(e -> e.getSimilarity() < MANDATORY_VALUE ? true : false);
	}
}
