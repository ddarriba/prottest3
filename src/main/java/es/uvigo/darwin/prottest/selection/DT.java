/** 
 * AIC.java
 *
 * Description:		AIC computation
 * @author			David Posada, University of Vigo, Spain  
 *					dposada@uvigo.es | darwin.uvigo.es
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
		ModelCollection modelCollection = new SingleModelCollection(models.toArray(new Model[0]));
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


