package es.uvigo.darwin.prottest.selection.model;

import es.uvigo.darwin.prottest.model.Model;

/**
 * Model wrapper including information about 
 * Likelihood Score Selection
 */
public class LNLSelectionModel extends SelectionModel {

	/**
	 * Instantiates a new LnL selection model.
	 * 
	 * @param model the underlying model
	 */
	public LNLSelectionModel(Model model) {
		super(model, 0.0);
		this.value = model.getLk(); 
	}
}
