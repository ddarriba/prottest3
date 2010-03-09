package es.uvigo.darwin.prottest.selection.model;

import es.uvigo.darwin.prottest.model.Model;

/**
 * Model wrapper including information about corrected Akaike Information Criterion
 */
public class AICcSelectionModel extends SelectionModel {

	/**
	 * Instantiates a new AICc selection model.
	 * 
	 * @param model the underlying model
	 * @param sampleSize the sample size
	 */
	public AICcSelectionModel(Model model, double sampleSize) {
		super(model, sampleSize);
		double k	= model.getNumberOfModelParameters();
		double n	= sampleSize;
		this.value = (-2 * model.getLk() + 2*k + 2*k*(k+1)/(n-k-1));
	}
}
