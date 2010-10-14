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
package es.uvigo.darwin.prottest.model;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;

/**
 * Substitution model form amino-acid sequences
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
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
	    	frequenciesDistribution = FREQ_DISTRIBUTION_EMPIRICAL;
	    else
	    	frequenciesDistribution = FREQ_DISTRIBUTION_OTHER;
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
