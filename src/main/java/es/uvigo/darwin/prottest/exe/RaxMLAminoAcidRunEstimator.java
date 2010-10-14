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
package es.uvigo.darwin.prottest.exe;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException;


/**
 * The Class RAxMLAminoAcidRunEstimator. It optimizes Amino-Acid
 * model parameters using RAxML.
 * 
 * This implementation fits with RAxML 7.0.4.
 */
public class RaxMLAminoAcidRunEstimator extends AminoAcidRunEstimator {

    /**
     * Instantiates a new optimizer for amino-acid models
     * using RaxML.
     *
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     */
    public RaxMLAminoAcidRunEstimator(ApplicationOptions options, Model model) {
        super(options, model);
    }

     /**
     * Instantiates a new optimizer for amino-acid models
     * using RaxML.
     *
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     * @param numberOfThreads the number of threads to use in the optimization
     */
    public RaxMLAminoAcidRunEstimator(ApplicationOptions options, Model model, int numberOfThreads) {
        super(options, model, numberOfThreads);
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.exe.RunEstimator#optimizeModel(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
     */
    @Override
    public boolean runEstimator() throws ModelOptimizationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Delete temporary files.
     *
     * @return true, if successful
     */
    @Override
    protected boolean deleteTemporaryFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
