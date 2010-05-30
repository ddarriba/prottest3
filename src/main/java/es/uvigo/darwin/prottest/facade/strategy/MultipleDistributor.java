package es.uvigo.darwin.prottest.facade.strategy;

import java.util.Collections;

import mpi.MPI;
import mpi.Request;
import mpi.Status;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * This class depends on MPJ-Express. Its behavior is inherently coupled to 
 * ImprovedDynamicDistributionStrategy's, providing the root process with
 * the possibility of distribute the models and also computing likelihood
 * values asynchronously.
 * 
 * @see ImprovedDynamicDistributionStrategy
 */
public class MultipleDistributor implements Runnable {

	/** MPJ Tag for requesting a new model. */
	private static final int TAG_SEND_REQUEST = 1;
	
	/** MPJ Tag for sending a new model. */
	private static final int TAG_SEND_MODEL = 2;

	/** MPJ Rank of the processor. */
	private int mpjMe;
	
	/** MPJ Size of the communicator. */
	private int mpjSize;

	/** The improved dynamic distribution strategy who calls this instance. */
	private HybridDistributionStrategy caller;
	
	/** The collection of all models to distribute. */
	private ModelCollection modelsToSend;
	
	/** The heuristic algorithm to compare models. */
	private ModelWeightComparator comparator;
	
	/** The number of models per processor. It will be necessary
	 * by the root process to do the non-uniform gathering */
	private int[] itemsPerProc;
	
	/** The array of displacements after the distribution.
	 * It will be necessary by the root process to do the non-uniform gathering */
	private int[] displs;

        //For now, we use a static number of available threads
        //TODO: Make this dynamically filled for each node
        private int maxAvailableThreads = 8;

/**
 * Gets the array of items per processor.
 * 
 * @return the array of items per processor
 */
public int[] getItemsPerProc() { return itemsPerProc; }
	
	/**
	 * Gets the array of displacements. The root process needs this attribute
	 * in order to do the non-uniform gathering.
	 * 
	 * @return the array of displacements
	 */
	public int[] getDispls() { return displs; }
	
	/**
	 * Instantiates a new distributor. The root process needs this attribute
	 * in order to do the non-uniform gathering.
	 * 
	 * @param caller the ImprovedDynamicDistributionStrategy instance which calls this constructor
	 * @param modelCollection the models to distribute amongst processors
	 * @param mpjMe the rank of the current process in MPJ
	 * @param mpjSize the size of the MPJ communicator
	 */
	public MultipleDistributor(HybridDistributionStrategy caller,
			ModelCollection modelCollection, ModelWeightComparator comparator,
			int mpjMe, int mpjSize) {
		this.comparator = comparator;
                this.modelsToSend = modelCollection.clone();
		Collections.sort(this.modelsToSend, comparator);
		this.mpjMe = mpjMe;
		this.mpjSize = mpjSize;
		this.caller = caller;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		distribute();
			
	}
	
	/**
	 * Distributes the whole model collection amongst the processors in
	 * the communicator. This method should be synchronized with the
	 * request method in the Improved Dynamic Distribution Strategy.
	 * 
	 * @see es.uvigo.darwin.prottest.facade.strategy.ImprovedDynamicDistributionStrategy#request()
	 * 
	 * @param modelSet the model collection to distribute amongst processors
	 * @param comparator implementation of the heuristic algorithm for sort and distribute models
	 */
	private void distribute() {

		itemsPerProc = new int[mpjSize];
		displs = new int[mpjSize];
		
		while (!modelsToSend.isEmpty()) {
			// check root processor
			//
			// This strategy is an easy way to avoid the problem of thread-safety
			// in MPJ-Express. It works correctly, but it also causes to introduce
			// coupling between this class and ImprovedDynamicDistributionStrategy,
			// having to define two public attributes: rootModelRequest and rootModel.
			//
                        if (caller.rootModelRequest && caller.rootModelFreePEs > 0) {
//				if (caller.rootModel != null) {
//					caller.setCheckpoint(modelSet);
//				}
				caller.rootModel = getNextModel(caller.rootModelFreePEs);
				caller.rootModelFreePEs -= getPEs(caller.rootModel);
                                caller.rootModelRequest = false;
				itemsPerProc[mpjMe]++;
			} else {
				Integer[] freePEs = new Integer[1];
				// getModel request
				Request modelRequest = MPI.COMM_WORLD.Irecv(freePEs, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG_SEND_REQUEST);
				// prepare model
				Model[] modelToSend = new Model[1];
				modelToSend[0] = getNextModel(freePEs[0]);
				// wait for request
				Status requestStatus = modelRequest.Wait();
//				if (computedModel[0] != null) {
//					// set checkpoint
//					int index = modelSet.indexOf(computedModel[0]);
//					modelSet.set(index, computedModel[0]);
//					caller.setCheckpoint(modelSet);
//				}
				// send model
				Request modelSend = MPI.COMM_WORLD.Isend(modelToSend, 0, 1, MPI.OBJECT, requestStatus.source, TAG_SEND_MODEL);
				// update structures
				itemsPerProc[requestStatus.source]++;
				// wait for send
				modelSend.Wait();
			}
		}
		displs[0] = 0;
		for (int i = 1; i < mpjSize; i++)
			displs[i] = displs[i-1] + itemsPerProc[i-1];

		// finalize
		for (int i = 1; i < mpjSize; i++) {
			Model[] computedModel = new Model[1];
			// getModel request
			Request modelRequest = MPI.COMM_WORLD.Irecv(computedModel, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, TAG_SEND_REQUEST);
			Model[] modelToSend = { null };
			// wait for request
			Status requestStatus = modelRequest.Wait();
			if (computedModel[0] != null) {
				// set checkpoint
				int index = modelSet.indexOf(computedModel[0]);
				modelSet.set(index, computedModel[0]);
				caller.setCheckpoint(modelSet);
			}
			// send null model
			Request modelSend = MPI.COMM_WORLD.Isend(modelToSend, 0, 1, MPI.OBJECT, requestStatus.source, TAG_SEND_MODEL);

			modelSend.Wait();
		}
		// check root
		while (!caller.rootModelRequest) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new ProtTestInternalException("Thread interrupted");
			}
		}
		caller.rootModel = null;
		caller.rootModelRequest = false;
	}

        private Model getNextModel(int numPEs) {
            Model nextModel = null;
            for (Model model : modelsToSend) {
                if (getPEs(model) <= numPEs) {
                    nextModel = model;
                    break;
                }
            }
            modelsToSend.remove(nextModel);
            return nextModel;
        }

        private int getPEs(Model model) {
            int numberOfThreads;
            if (model.isGamma())
                numberOfThreads = 4;
            else if (model.isInv())
                numberOfThreads = 2;
            else
                numberOfThreads = 1;
            return Math.min(maxAvailableThreads, numberOfThreads);
        }
}