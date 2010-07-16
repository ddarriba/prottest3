package es.uvigo.darwin.prottest.selection.model;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.InformationCriterion;

/**
 * This class provides a Model wrapper including information about a concrete
 * model selection criterion, like AIC, BIC or corrected AIC.
 * 
 * @see Model
 * @see InformationCriterion
 */
public abstract class SelectionModel implements Comparable<SelectionModel> {

	/** The contained model. */
	private Model model;
	
	/** The sample size. */
	protected double sampleSize;
	
	/** The model selection absolute value. */
	protected double value;
	
	/** The model selection relative weight value. */
	private double weightValue;
	
	/** The model selection delta value. */
	private double deltaValue;
	
	/** The cumulative weight value, in relationship to
	 * the order of the models sorted by the model selection
	 * criterion. */
	private double cumulativeWeightValue;
	
	/** Model lays into the confidence interval */
	private boolean inConfidenceInterval;
	
	/**
	 * Gets the underlying model.
	 * 
	 * @return the underlyingmodel
	 */
	public Model getModel() { return model; }

	/**
	 * Gets the model selection criterion value.
	 * 
	 * @return the model selection criterion value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Gets the model selection relative weight value.
	 * 
	 * @return the weight value
	 */
	public double getWeightValue() {
		return weightValue;
	}

	/**
	 * Sets the model selection relative weight value.
	 * 
	 * @param weightValue the new weight value
	 */
	public void setWeightValue(double weightValue) {
		this.weightValue = weightValue;
	}

	/**
	 * Gets the model selection criterion delta value.
	 * 
	 * @return the delta value
	 */
	public double getDeltaValue() {
		return deltaValue;
	}

	/**
	 * Sets the model selection criterion delta value.
	 * 
	 * @param deltaValue the new delta value
	 */
	public void setDeltaValue(double deltaValue) {
		this.deltaValue = deltaValue;
	}

	/**
	 * Gets the cumulative weight value.
	 * 
	 * @return the cumulative weight value
	 */
	public double getCumulativeWeightValue() {
		return cumulativeWeightValue;
	}

	/**
	 * Sets the cumulative weight value.
	 * 
	 * @param cumulateWeightValue the new cumulative weight value
	 */
	public void setCumulativeWeightValue(double cumulateWeightValue) {
		this.cumulativeWeightValue = cumulateWeightValue;
	}
	
	/**
	 * Checks if model lays into the confidence interval.
	 * 
	 * @return true, if is in confidence interval
	 */
	public boolean isInConfidenceInterval() {
		return inConfidenceInterval;
	}

	/**
	 * Sets the in confidence interval.
	 * 
	 * @param inConfidenceInterval the new in confidence interval
	 */
	public void setInConfidenceInterval(boolean inConfidenceInterval) {
		this.inConfidenceInterval = inConfidenceInterval;
	}

	/**
	 * Instantiates a new selection model.
	 * 
	 * @param model the underlying model
	 * @param sampleSize the sample size
	 */
	public SelectionModel(Model model, double sampleSize) {
		this.model = model;
		this.sampleSize = sampleSize;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SelectionModel other) {
		if (this.value > other.value)
			return 1;
		else if (this.value < other.value)
			return -1;
		else
			return 0;
	}
}
