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
    private static final int TAG_EXIST_MORE_MODELS = 2;
    /** MPJ Tag for sending a new model. */
    private static final int TAG_SEND_MODEL = 3;
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
    public int[] getItemsPerProc() {
        return itemsPerProc;
    }

    /**
     * Gets the array of displacements. The root process needs this attribute
     * in order to do the non-uniform gathering.
     * 
     * @return the array of displacements
     */
    public int[] getDispls() {
        return displs;
    }

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
        Status requestStatus = null;
        displs = new int[mpjSize];
        int[] freePEs = new int[1];
        boolean sended = true;

        Request modelRequest = null;
        while (!modelsToSend.isEmpty()) {
            // check root processor
            //
            // This strategy is an easy way to avoid the problem of thread-safety
            // in MPJ-Express. It works correctly, but it also causes to introduce
            // coupling between this class and ImprovedDynamicDistributionStrategy,
            // having to define two public attributes: rootModelRequest and rootModel.
            //
            if (caller.rootModelRequest && caller.availablePEs > 0) {
//				if (caller.rootModel != null) {
//					caller.setCheckpoint(modelSet);
//				}
                Model rootModel = getNextModel(caller.availablePEs);
                if (rootModel != null) {
                    caller.rootModel = rootModel;
                    caller.rootModelRequest = false;
                    itemsPerProc[mpjMe]++;
                }
            } else {
                // getModel request
                if (sended) {
                    modelRequest = MPI.COMM_WORLD.Irecv(freePEs, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG_SEND_REQUEST);
                    // wait for request
                    sended = false;
                }
                requestStatus = modelRequest.Test();
                if (requestStatus != null) {

                    Request notifySend = MPI.COMM_WORLD.Isend(new boolean[]{true}, 0, 1, MPI.BOOLEAN, requestStatus.source, TAG_EXIST_MORE_MODELS);

                    // prepare model
                    Model[] modelToSend = new Model[1];

                    notifySend.Wait();

                    modelToSend[0] = getNextModel(freePEs[0]);
                    Request modelSend = MPI.COMM_WORLD.Isend(modelToSend, 0, 1, MPI.OBJECT, requestStatus.source, TAG_SEND_MODEL);

                    if (modelToSend[0] != null) {
                        // update structures
                        itemsPerProc[requestStatus.source]++;
                    }

                    // wait for send
                    modelSend.Wait();
                    sended = true;

                    requestStatus = null;

                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new ProtTestInternalException("Thread interrupted");
                }

            }
        }
        
        displs[0] = 0;
        for (int i = 1; i < mpjSize; i++) {
            displs[i] = displs[i - 1] + itemsPerProc[i - 1];
        }

        // finalize
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
        
        for (int i = 1; i < mpjSize; i++) {
            // getModel request
            modelRequest = MPI.COMM_WORLD.Irecv(freePEs, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG_SEND_REQUEST);
            // wait for request
            requestStatus = modelRequest.Wait();
            // send null model
            Request notifySend = MPI.COMM_WORLD.Isend(new boolean[]{false}, 0, 1, MPI.BOOLEAN, requestStatus.source, TAG_EXIST_MORE_MODELS);
            notifySend.Wait();

        }
        
    }

    private Model getNextModel(int numPEs) {
        Model nextModel = null;
        for (Model model : modelsToSend) {
            if (getPEs(model, maxAvailableThreads) <= numPEs) {
                nextModel = model;
                break;
            }
        }
        modelsToSend.remove(nextModel);
        return nextModel;
    }

    public static int getPEs(Model model, int maxAvailableThreads) {
        int numberOfThreads;
        if (model.isGamma()) {
            numberOfThreads = 4;
        } else if (model.isInv()) {
            numberOfThreads = 2;
        } else {
            numberOfThreads = 1;
        }
        return Math.min(maxAvailableThreads, numberOfThreads);
    }
}
