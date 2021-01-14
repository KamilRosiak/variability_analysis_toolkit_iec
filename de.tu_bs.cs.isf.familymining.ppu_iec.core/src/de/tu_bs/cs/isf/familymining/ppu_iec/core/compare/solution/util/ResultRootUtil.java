package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.util;

import java.util.List;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractOption;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;

public class ResultRootUtil {
	private static final float MANDATORY_VALUE = 1.0f;

	
	/**
	 * This method returns all changed artifacts of a ConfigurationResultRoot as
	 * List of AbstractContainer.
	 * 
	 * @return
	 */
	public static List<AbstractContainer> findChanges(List<AbstractContainer> containers, List<AbstractContainer> results) {
		for (AbstractContainer container : containers) {
			//adding optional items to results
			if(container.getFirst() == null || container.getSecond() == null) {
				results.add(container);
			}
			//adding changed items to results
			if (checkAttributeSimilarity(container.getResults())) {
				results.add(container);
			}

			for(Object option : container.getOptions()) {
				results.addAll(findChanges(((AbstractOption)option).getAllContainer(), results));
			}
		}

		return results;
	}
	
	/**
	 * This method checks if the similarity of a list of results has an element with
	 * a similarity that is lower than 1, which indicates that this element was
	 * changed.
	 */
	private static boolean checkAttributeSimilarity(List<ResultElement> list) {
		return list.stream().anyMatch(e -> e.getSimilarity() < MANDATORY_VALUE ? true : false);
	}

}
