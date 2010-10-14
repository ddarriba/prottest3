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
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;

/**
 * A generic optimizer for amino-acid models
 * 
 * @author Federico Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public abstract class AminoAcidRunEstimator extends RunEstimatorImpl {
	
	/**
	 * Instantiates a new generic optimizer for amino-acid models.
	 * 
	 * @param options the application options instance
	 * @param model the amino-acid model to optimize
	 */
	public AminoAcidRunEstimator(ApplicationOptions options, Model model) {
		super(options, model);
	}
        
        /**
	 * Instantiates a new generic optimizer for amino-acid models.
	 * 
	 * @param options the application options instance
	 * @param model the amino-acid model to optimize
         * @param numberOfThreads the number of threads to use in the optimization
	 */
	public AminoAcidRunEstimator(ApplicationOptions options, Model model, int numberOfThreads) {
		super(options, model, numberOfThreads);
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.exe.RunEstimator#report()
	 */
	public void printReport () {
		model.printReport();
		if (time != null)
			ProtTestLogger.getDefaultLogger().info("     (" + time + ")\n");
                ProtTestLogger.getDefaultLogger().info("\n");
                ProtTestLogger.getDefaultLogger().flush();
	}

}
