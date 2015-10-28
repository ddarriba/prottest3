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


import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;

/**
 * The Interface RunEstimator. Should be implemented by any class which launches
 * an external likelihood calculator or directly calculates likelihood for any 
 * kind of model.
 * 
 * @author Federico Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public interface RunEstimator extends Runnable {

	/**
	 * Gets the inner optimized model.
	 * 
	 * @return the model
	 */
	public Model getModel();
	
	/**
	 * Optimizes the parameters of the model.
	 * 
	 * @return true, if there is no error in the execution of the optimizer
	 */
	public boolean optimizeModel();
	
	/**
	 * Reports out the result of the optimization into the application loggers: 
         * the optimized parameters of the model.
	 */
	public void report();
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.observer.ObservableModelUpdater#addObserver(es.uvigo.darwin.prottest.observer.ObservableModelUpdater)
	 */
	public void addObserver(ModelUpdaterObserver o);

}
