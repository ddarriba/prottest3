/*
Copyright (C) 2009  David Posada

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
import es.uvigo.darwin.prottest.selection.model.AICSelectionModel;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;

/**
 * The Akaike Information Criterion
 *
 * Description:		AIC computation
 * @author			David Posada, University of Vigo, Spain
 *					dposada@uvigo.es | darwin.uvigo.es
 */
public class AIC extends InformationCriterion
{
	
//	/**
//	 * Instantiates a new Akaike Information Criterion.
//	 * 
//	 * @param models the models
//	 * @param confidenceInterval the confidence interval
//	 */
//	public AIC (ModelCollection models, double confidenceInterval) 
//		{
//		super(models, confidenceInterval);
//		
//		Collections.sort(selectionModels);
//		}
	
	/**
	 * Instantiates a new Akaike Information Criterion.
	 * 
	 * @param models the models
	 * @param confidenceInterval the confidence interval
	 * @param sampleSize the sample size if different of the default
	 */
	public AIC (ModelCollection models, double confidenceInterval, double sampleSize) 
		{
		super(models, confidenceInterval, sampleSize);
		
		Collections.sort(selectionModels);
		}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.InformationCriterion#getSelectionModels(es.uvigo.darwin.prottest.util.collection.ModelIterator)
	 */
	protected List<SelectionModel> getSelectionModels(List<Model> models) {
		List<SelectionModel> list = new ArrayList<SelectionModel>();
		for (Model model : models) {
			SelectionModel toAdd = new AICSelectionModel(
					model, 
					sampleSize); 
			list.add( toAdd );
			hashModels.put(model, toAdd);
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.InformationCriterion#getCriterionName()
	 */
	public String getCriterionName() {
		return "AIC";
	}
}


