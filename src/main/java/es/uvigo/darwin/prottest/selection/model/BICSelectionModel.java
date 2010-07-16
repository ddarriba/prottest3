package es.uvigo.darwin.prottest.selection.model;

import es.uvigo.darwin.prottest.model.Model;

/**
 * Model wrapper including information about Bayesian Information Criterion
 */
public class BICSelectionModel extends SelectionModel {

	/**
	 * Instantiates a new BIC selection model.
	 * 
	 * @param model the underlying model
	 * @param sampleSize the sample size
	 */
	public BICSelectionModel(Model model, double sampleSize) {
		super(model, sampleSize);
		this.value = -2 * model.getLk() + model.getNumberOfModelParameters() * Math.log (sampleSize);
	}
}
