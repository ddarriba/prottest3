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
package es.uvigo.darwin.prottest.facade.strategy;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.checkpoint.status.ProtTestStatus;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;

/**
 * This class provides the strategy template for distribute the models amongst processors,
 * the concrete classes should both distribute models and compute likelihood values.
 */
public abstract class DistributionStrategy extends ObservableModelUpdater implements ModelUpdaterObserver {
 
	/** The application options instance. */
	protected ApplicationOptions options;
	
	/** The model set to compute. */
	protected ModelCollection modelSet;
	
	/** The number of models to compute. */
	protected int numberOfModels;
	
	/** The number of models per processor. It will be necessary
	 * by the root process to do the non-uniform gathering */
	protected int[] itemsPerProc;
	
	/** The array of displacements after the distribution.
	 * It will be necessary by the root process to do the non-uniform gathering */
	protected int[] displs;
	
	/** The ProtTest factory instance. */
	protected ProtTestFactory factory = ProtTestFactory.getInstance();
	
	/** MPJ Rank of the processor. */
	protected int mpjMe;
	
	/** MPJ Size of the communicator. */
	protected int mpjSize;
	
	/** The absolute start time of the main computation in milliseconds. It should be filled by
	 * the classes which extends this one 
	 * @see System#currentTimeMillis()
	 * */
	protected long startTime;
	
	/** The absolute end time of the main computation in milliseconds. It should be filled by
	 * the classes which extends this one 
	 * @see System#currentTimeMillis()
	 * */
	protected long endTime;
	
	private CheckPointManager cpManager;
	
	/**
	 * Instantiates a new distribution strategy with Checkpointing support.
	 * 
	 * @param mpjMe the rank of the current process in MPJ
	 * @param mpjSize the size of the MPJ communicator
	 * @param options the application options
	 * @param cpManager the checkpoint manager, it can be null if checkpointing is not supported
	 */
	public DistributionStrategy(int mpjMe, int mpjSize, ApplicationOptions options,
			CheckPointManager cpManager) {
		this.options = options;
		this.mpjMe = mpjMe;
		this.mpjSize = mpjSize;
		this.cpManager = cpManager;
	}
	
	/**
	 * Method called by the root process in order to distribute the models
	 * amongst processors.
	 * 
	 * @param modelSet the models to distribute
	 * @param comparator implementation of the heuristic algorithm for sort and distribute models
	 * 
	 * @return the array of models after computing likelihood
	 */
	public abstract Model[] distribute(ModelCollection modelSet, ModelWeightComparator comparator);
	
	/**
	 * Method called by non-root processors in order to request and compute
	 * their assigned models.
	 */
	public abstract void request();
	
	/**
	 * Gets the absolute start time of computing in milliseconds.
	 * 
	 * @return the start time in milliseconds
	 */
	public long getStartTime() { return startTime; }
	
	/**
	 * Gets the absolute end time of computing in milliseconds.
	 * 
	 * @return the end time in milliseconds
	 */
	public long getEndTime() { return endTime; }
	
	public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {
		
		notifyObservers(model, options);
		
	}
	
	protected void setCheckpoint(ModelCollection modelSet) {
		
		if (mpjMe > 0)
			throw new ProtTestInternalException("Only root process can set checkpoints");

		if (cpManager != null) {
			ProtTestStatus newStatus = new ProtTestStatus(modelSet.toArray(new Model[0]), options);
			cpManager.setStatus(newStatus);
		}
	}
	
	protected void computationDone() {
		if (mpjMe > 0)
			throw new ProtTestInternalException("Only root process can set checkpoints");
		
		cpManager.done();
	}
}
