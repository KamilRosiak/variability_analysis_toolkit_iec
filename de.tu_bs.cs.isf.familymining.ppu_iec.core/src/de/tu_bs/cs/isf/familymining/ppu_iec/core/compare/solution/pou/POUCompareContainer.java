package de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractOption;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;

/**
 * This class represents the POUCompareContainer which will be added to data structure 
 * when the engine compares some POUS.
 * @author {Kamil Rosiak}
 */
public class POUCompareContainer extends IECAbstractContainer<POU> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5778073642506920516L;

	private POUActionOption pouActionOption;
	private POUImplementationOption pouImplOption;
	private POUVariableOption pouVarOption;
	
	public POUCompareContainer(POU first, POU second, MetricContainer metric) {
		super(first, second, metric);
	}

	public POUActionOption getPouActionOption() {
		return pouActionOption;
	}

	public void setPouActionOption(POUActionOption pouActionOption) {
		getOptions().add(pouActionOption);
		this.pouActionOption = pouActionOption;
	}

	public POUImplementationOption getPouImplOption() {
		return pouImplOption;
	}

	public void setPouImplOption(POUImplementationOption pouImplOption) {
		getOptions().add(pouImplOption);
		this.pouImplOption = pouImplOption;
	}

	public POUVariableOption getPouVarOption() {
		return pouVarOption;
	}

	public void setPouVarOption(POUVariableOption pouVarOption) {
		getOptions().add(pouVarOption);
		this.pouVarOption = pouVarOption;
	}
	
	public String toString() {
		String firstID = super.getFirst() == null ? "null" : super.getFirst().getIdentifier();
		String secondID = super.getSecond() == null ? "null" : super.getSecond().getIdentifier();
		
		String result = "";
		
		result += "First:\t"+firstID+"\n";
		result += "Second:\t"+secondID+"\n";
		result += "-----------------------------------\n";
		result += "Similarity: "+super.getSimilarity();
		return result;
	}

	@Override
	public void updateSimilarity() {
		float pouContainerSimilarity = 0.0f;
		for(AbstractOption option : getOptions()) {
			option.updateSimilarity();
			pouContainerSimilarity += getSimilarity(option.getSimilarity(), option.getOptionAttr(), getMetric().isWeighted());
			
		}

		//adds all results weighted or not weighted.
		for(ResultElement<?> pouResult : getResults()) {			
			pouContainerSimilarity += getSimilarity(pouResult.getSimilarity(), pouResult.getAttribute(), getMetric().isWeighted());
		}
		
		//if not weighted we have to dived the similarity with the count of the results else its normed by 100% trough the weights.
		if(!getMetric().isWeighted() && getResults().size() + getOptions().size() > 0) {
			pouContainerSimilarity = pouContainerSimilarity / (getResults().size() + getOptions().size());
		}
		setSimilarity(pouContainerSimilarity);
	}

	@Override
	public void reset() {
		getOptions().clear();
		getResults().clear();
	}

	@Override
	public String getLeftLabel() {
		if(getFirst() != null) {
			return getFirst().getIdentifier();
		} else {
			return null;
		}	
	}

	@Override
	public String getRightLabel() {
		if(getSecond() != null) {
			return getSecond().getIdentifier();
		} else {
			return null;
		}
	}

	@Override
	public Boolean checkCompared() {
		if(pouVarOption != null || pouActionOption != null || pouImplOption != null || isCompared() || !getResults().isEmpty()) {
			return true;
		} else {
			return false;
		}	
	}
}
