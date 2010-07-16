package es.uvigo.darwin.prottest.util.comparator;

import java.util.Comparator;

import es.uvigo.darwin.prottest.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelWeightComparator.
 */
public abstract class ModelWeightComparator implements Comparator<Model> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Model model1, Model model2) {
		int value = 0;
		if (model1 != null && model2 != null)
			value = getWeight(model2) - getWeight(model1);
		return value;
	}

	/**
	 * Gets the weight.
	 * 
	 * @param model the model
	 * 
	 * @return the weight
	 */
	public abstract int getWeight(Model model);
}
