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
package es.uvigo.darwin.prottest.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.model.DTSelectionModel;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;
import es.uvigo.darwin.prottest.tree.TreeDistancesCache;
import es.uvigo.darwin.prottest.tree.TreeEuclideanDistancesCache;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;

/**
 * The Decision Theory Information Criterion.
 *
 * @author Diego Darriba
 */
public class DT extends InformationCriterion
{
	
	private BIC bic;
	private TreeDistancesCache distancesCache;
	
	/**
	 * Instantiates a new Decision Theory Information Criterion.
	 * 
	 * @param models the models
	 * @param confidenceInterval the confidence interval
	 * @param sampleSize the sample size if different of the default
	 */
	public DT (ModelCollection models, double confidenceInterval, double sampleSize) 
		{
		super(models, confidenceInterval, sampleSize);
		Collections.sort(selectionModels);
		}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.InformationCriterion#getSelectionModels(es.uvigo.darwin.prottest.util.collection.ModelIterator)
	 */
	protected List<SelectionModel> getSelectionModels(List<Model> models) {
		ModelCollection modelCollection = new SingleModelCollection(
                        models.toArray(new Model[0]),
                        alignment);
		bic = new BIC(modelCollection, confidenceInterval, sampleSize);
		distancesCache = TreeEuclideanDistancesCache.getInstance();
		
		List<SelectionModel> list = new ArrayList<SelectionModel>();
		for (Model model : models) {
			SelectionModel toAdd = new DTSelectionModel(
					model, 
					sampleSize,
					bic,
					distancesCache); 
			list.add( toAdd );
			hashModels.put(model, toAdd);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.InformationCriterion#getCriterionName()
	 */
	public String getCriterionName() {
		return "DT";
	}
}


