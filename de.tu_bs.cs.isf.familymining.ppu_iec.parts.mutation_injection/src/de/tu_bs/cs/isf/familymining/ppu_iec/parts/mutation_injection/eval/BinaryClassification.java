package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

/**
 * A class for the binary classification.
 * 
 * @author Kamil Rosiak
 *
 */
public class BinaryClassification {
	private int numberChanges;
	private int numberDetected;
	private float truePositives;
	private float falsePositives;
	private float falseNegatives;
	private float trueNegatives;

	/**
	 * Precision and Recall constructor
	 */
	public BinaryClassification(int truePositives, int falsePositive, int falseNegatives) {
		setTruePositives(truePositives);
		setFalsePositives(falsePositive);
		setFalseNegatives(falseNegatives);
	}

	/**
	 * Default Constructor
	 */
	public BinaryClassification() {

	}

	public float getTrueNegatives() {
		return trueNegatives;
	}

	public void setTrueNegatives(float trueNegatives) {
		this.trueNegatives = trueNegatives;
	}

	public float getFalseNegatives() {
		return falseNegatives;
	}

	public void setFalseNegatives(float falseNegatives) {
		this.falseNegatives = falseNegatives;
	}

	public float getFalsePositives() {
		return falsePositives;
	}

	public void setFalsePositives(float falsePositives) {
		this.falsePositives = falsePositives;
	}

	public float getTruePositives() {
		return truePositives;
	}

	public void setTruePositives(float truePositives) {
		this.truePositives = truePositives;
	}

	public float getRecall() {
		return truePositives != 0 ? truePositives / (truePositives + falseNegatives) : 0;
	}

	public float getPrecision() {
		return truePositives != 0 ? truePositives / (truePositives + falsePositives) : 0;
	}

	public int getNumberChanges() {
		return numberChanges;
	}

	public void setNumberChanges(int numberChanges) {
		this.numberChanges = numberChanges;
	}

	public int getNumberDetected() {
		return numberDetected;
	}

	public void setNumberDetected(int numberDetected) {
		this.numberDetected = numberDetected;
	}

	@Override
	public String toString() {
		return "Precision: " + getPrecision() + " Recall: " + getRecall() + " TP: " + getTruePositives() + " FP: "
				+ getFalsePositives() + " FN: " + getFalseNegatives() + " TN: " + getTrueNegatives();
	}
}
