package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.BinaryClassification;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.CloneType;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.HitContainer;

public class EvaluationResult {
	private String name;
	private String directory;
	private int totalRuns;
	private List<RunResult> resultFirstMetric = new ArrayList<>();
	private List<RunResult> resultSecondMetric = new ArrayList<>();
	private BinaryClassification binClasFirstMetric = new BinaryClassification();
	private BinaryClassification binClasSecondMetric = new BinaryClassification();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public void setTotalRuns(int totalRuns) {
		this.totalRuns = totalRuns;
	}

	public List<RunResult> getResultFirstMetric() {
		return resultFirstMetric;
	}

	public void setResultFirstMetric(List<RunResult> resultFirstMetric) {
		this.resultFirstMetric = resultFirstMetric;
	}

	public List<RunResult> getResultSecondMetric() {
		return resultSecondMetric;
	}

	public void setResultSecondMetric(List<RunResult> resultSecondMetric) {
		this.resultSecondMetric = resultSecondMetric;
	}

	/**
	 * This method updates the classification for based on a list of run results.
	 */
	private void updateClassification(BinaryClassification binClassification, List<RunResult> results) {
		for (RunResult result : results) {
			binClassification.setTruePositives(
					binClassification.getTruePositives() + result.getClassisfication().getTruePositives());
			binClassification.setTrueNegatives(
					binClassification.getTrueNegatives() + result.getClassisfication().getTrueNegatives());
			binClassification.setFalseNegatives(
					binClassification.getFalseNegatives() + result.getClassisfication().getFalseNegatives());
			binClassification.setFalsePositives(
					binClassification.getFalsePositives() + result.getClassisfication().getFalsePositives());
			binClassification.setNumberChanges(binClassification.getNumberChanges() + result.getNumberMutations());
			binClassification.setNumberDetected(binClassification.getNumberDetected() + result.getNumberChangesFound());
		}
	}

	public BinaryClassification getBinClasFirstMetric() {
		return binClasFirstMetric;
	}

	public void setBinClasFirstMetric(BinaryClassification binClasFirstMetric) {
		this.binClasFirstMetric = binClasFirstMetric;
	}

	public BinaryClassification getBinClasSecondMetric() {
		return binClasSecondMetric;
	}

	public void setBinClasSecondMetric(BinaryClassification binClasSecondMetric) {
		this.binClasSecondMetric = binClasSecondMetric;
	}

	@Override
	public String toString() {
		updateClassification(binClasFirstMetric, getResultFirstMetric());
		updateClassification(binClasSecondMetric, getResultSecondMetric());

		String msg = " Runs: " + totalRuns + " toalMutants: " + binClasFirstMetric.getNumberChanges() + "\n";
		msg += "First Metric Result: \n" + binClasFirstMetric + "\n";
		msg += "Second Metric Result: \n" + binClasSecondMetric + "\n";
		return msg;
	}

	public static class RunResult {
		private CloneType cloneType = CloneType.UNDEFINED;
		private int run;
		private String name;
		// debug
		private int numberMutations;
		private int numberChangesFound;
		private BinaryClassification classisfication = new BinaryClassification();
		private List<HitContainer> foundChanges = new ArrayList<HitContainer>();

		public int getRun() {
			return run;
		}

		public void setRun(int run) {
			this.run = run;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getNumberMutations() {
			return numberMutations;
		}

		public void setNumberMutations(int numberMutations) {
			this.numberMutations = numberMutations;
		}

		public int getNumberChangesFound() {
			return numberChangesFound;
		}

		public void setNumberChangesFound(int numberChangesFound) {
			this.numberChangesFound = numberChangesFound;
		}

		@Override
		public String toString() {
			return getName() + " Changes: " + getNumberChangesFound() + " Mutants: " + getNumberMutations() + " (TP: "
					+ getClassisfication().getTruePositives() + " FP: " + getClassisfication().getFalsePositives()
					+ " FN: " + getClassisfication().getFalseNegatives() + " Precision: "
					+ getClassisfication().getPrecision() + " Recall: " + getClassisfication().getRecall() + ")";
		}

		public BinaryClassification getClassisfication() {
			return classisfication;
		}

		public void setClassisfication(BinaryClassification classisfication) {
			this.classisfication = classisfication;
		}

		public CloneType getCloneType() {
			return cloneType;
		}

		public void setCloneType(CloneType cloneType) {
			this.cloneType = cloneType;
		}

		public List<HitContainer> getFoundChanges() {
			return foundChanges;
		}

		public void setFoundChanges(List<HitContainer> foundChanges) {
			this.foundChanges = foundChanges;
		}
		
		public void addFoundChange(HitContainer container) {
			this.foundChanges.add(container);
		}
	}
}
