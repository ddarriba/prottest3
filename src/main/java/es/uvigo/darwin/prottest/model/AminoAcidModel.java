package es.uvigo.darwin.prottest.model;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;

/**
 * Substitution model form amino-acid sequences
 */
public class AminoAcidModel extends Model {

	/** The serialVersionUID. */
	private static final long serialVersionUID = -8634618735564880783L;
	
	/**
	 * Instantiates a new amino acid substitution model.
	 * 
	 * @param matrix the matrix name
	 * @param distribution the distribution value
	 * @param plusF consider observed frequencies
	 */
	public AminoAcidModel(String matrix, int distribution, boolean plusF, 
			Alignment alignment, Tree tree, int ncat) {
		super(matrix, distribution, plusF, alignment, tree, ncat);
		
		if (isPlusF())
	    	frequenciesDistribution = ApplicationGlobals.FREQ_DISTRIBUTION_EMPIRICAL;
	    else
	    	frequenciesDistribution = ApplicationGlobals.FREQ_DISTRIBUTION_OTHER;
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.Model#getNumberOfModelParameters()
	 */
	public int getNumberOfModelParameters() {
		int numModelParameters;
		numModelParameters = getNumBranches() + getDistributionParameters();
		if(isPlusF()) 
			numModelParameters = numModelParameters + 19;
		return numModelParameters;
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.Model#getModelName()
	 */
	@Override
	public String getModelName()  {
		
		String matrixName = 
			ProtTestFactory.getInstance().
				getApplicationGlobals().
				getModelName(getMatrix(), frequenciesDistribution);
		StringBuffer modelName = new StringBuffer(matrixName);
		if(getDistribution() == Model.DISTRIBUTION_INVARIABLE)
			modelName.append("+I");
		else if(getDistribution() == Model.DISTRIBUTION_GAMMA) 
			modelName.append("+G");
		else if(getDistribution() == Model.DISTRIBUTION_GAMMA_INV)
			modelName.append("+I+G");

		if(isPlusF())
			modelName.append("+F");

		return modelName.toString();
	}

}
