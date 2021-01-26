package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data;

import java.util.ArrayList;
import java.util.List;

public class EvaluationResult {

	private String name;

	private String directory;

	private int totalRuns;

	private List<RunResult> result = new ArrayList<>();

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

	public List<RunResult> getResult() {
		return result;
	}

	public void setResult(List<RunResult> result) {
		this.result = result;
	}

	public static class RunResult {
		private int run;
		private String name;
		
		// debug
		private int numberMutations;
		private int numberChangesFound;
		
		// mutants that are killed by the compare engine
		private float truePositives;
		// in mutants but not in changes
		private float falseNegatives;
		// changed artifacts in changes not in mutants
		private float falsePositives;

		public float getFalsePositives() {
			return falsePositives;
		}

		public void setFalsePositives(float falsePositives) {
			this.falsePositives = falsePositives;
		}

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

		public float getRecall() {
			return truePositives / (truePositives + falseNegatives);
		}

		public float getPrecision() {
			return truePositives / (truePositives + falsePositives);
		}

		public float getTruePositives() {
			return truePositives;
		}

		public void setTruePositives(int truePositives) {
			this.truePositives = truePositives;
		}

		public float getFalseNegatives() {
			return falseNegatives;
		}

		public void setFalseNegatives(int falseNegatives) {
			this.falseNegatives = falseNegatives;
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
			return "RUN "+run+" Changes: "+  getNumberChangesFound() +" Mutants: "+ getNumberMutations() +" (TP: " + getTruePositives() + " FP: " + getFalsePositives() + " FN: " + getFalseNegatives() + " Precision: " + getPrecision() + " Recall: " +getRecall()+")";
		}
	}
}
