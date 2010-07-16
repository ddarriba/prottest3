package es.uvigo.darwin.prottest.util.comparator;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelDistributionHeuristic.
 */
public class ModelDistributionHeuristic extends ModelWeightComparator {

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator#getWeight(es.uvigo.darwin.prottest.model.Model)
	 */
	public int getWeight(Model model) {
		
		int weight;
		switch (model.getDistribution()) {
		case 0:
			weight = 1;
			break;
		case 1:
			weight = 2;
			break;
		case 2:
			weight = 6;
			break;
		case 3:
			weight = 30;
			break;
		default:
			throw new ProtTestInternalException("Invalid model distribution " + model.getDistribution());
		}
		
		return weight;
	}
	
}
