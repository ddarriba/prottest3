package es.uvigo.darwin.prottest.selection.model;

import es.uvigo.darwin.prottest.model.Model;

/**
 * Model wrapper including information about Akaike Information Criterion
 */
public class AICSelectionModel extends SelectionModel {

	/**
	 * Instantiates a new AIC selection model.
	 * 
	 * @param model the underlying model
	 * @param sampleSize the sample size
	 */
	public AICSelectionModel(Model model, double sampleSize) {
		super(model, sampleSize);
		this.value = -2 * model.getLk() + 2*model.getNumberOfModelParameters(); 
	}
}
