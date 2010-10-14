/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
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
