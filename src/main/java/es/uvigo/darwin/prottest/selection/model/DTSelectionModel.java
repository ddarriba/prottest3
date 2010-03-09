package es.uvigo.darwin.prottest.selection.model;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.tree.TreeDistancesCache;

/**
 * Model wrapper including information about Decision Theory Information Criterion
 */
public class DTSelectionModel extends SelectionModel {

	/**
	 * Instantiates a new Decision Theory selection model.
	 * 
	 * @param model the underlying model
	 * @param sampleSize the sample size
	 */
	public DTSelectionModel(Model model, double sampleSize, BIC bic, 
			TreeDistancesCache distancesCache) {
		super(model, sampleSize);

		double minBIC = bic.get(bic.getBestModel()).getValue();
		double sum = 0.0;
		for (SelectionModel selectionModel : bic.getSelectionModels()) {
			double distance = distancesCache.getDistance(model.getTree(), selectionModel.getModel().getTree());
			if (distance > 0) {
				double power = Math.log(distance) - selectionModel.getValue() + minBIC;
				if (power > -30) {
					sum += Math.exp(power);
				}
			}
		}

		this.value = sum;
	}
	
}
