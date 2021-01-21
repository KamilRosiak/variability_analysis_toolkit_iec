package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.data;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval.MutationEngine;

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
		private int totalMutations;
		private int compareContainersFound;
		private int mutationsFound;

		private float recall;

		private float precision;

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

		public int getTotalMutations() {
			return totalMutations;
		}

		public void setTotalMutations(int totalMutations) {
			this.totalMutations = totalMutations;
		}

		public int getCompareContainersFound() {
			return compareContainersFound;
		}

		public void setCompareContainersFound(int compareContainersFound) {
			this.compareContainersFound = compareContainersFound;
		}

		public int getMutationsFound() {
			return mutationsFound;
		}

		public void setMutationsFound(int mutationsFound) {
			this.mutationsFound = mutationsFound;
		}

		public float getRecall() {
			return recall;
		}

		public void setRecall(float recall) {
			this.recall = recall;
		}

		public float getPrecision() {
			return precision;
		}

		public void setPrecision(float precision) {
			this.precision = precision;
		}
	}
}
