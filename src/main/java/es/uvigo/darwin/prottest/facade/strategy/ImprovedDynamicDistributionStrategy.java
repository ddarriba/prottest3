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

import java.util.ArrayList;
import java.util.List;

import mpi.MPI;
import mpi.Request;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * This class improves the dynamic behaviour adding a distributor
 * thread.
 */
public class ImprovedDynamicDistributionStrategy extends DistributionStrategy {

	/** The Constant TAG_SEND_REQUEST. */
	private static final int TAG_SEND_REQUEST = 1;
	
	/** The Constant TAG_SEND_MODEL. */
	private static final int TAG_SEND_MODEL = 2;
	
	/** The root model request. */
	boolean rootModelRequest;
	
	/** The root model. */
	Model rootModel;
	
	/**
	 * Instantiates a new improved dynamic distribution strategy.
	 * 
	 * @param mpjMe the rank of the current process in MPJ
	 * @param mpjSize the size of the MPJ communicator
	 * @param options the application options
	 * @param cpManager the checkpoint manager, it can be null if checkpointing is not supported
	 */
	public ImprovedDynamicDistributionStrategy(int mpjMe, int mpjSize, ApplicationOptions options, CheckPointManager cpManager) {
		super(mpjMe, mpjSize, options, cpManager);
		if (mpjSize == 1) {
			throw new ProtTestInternalException("Dynamic Distribution Strategy" +
					" requires at least 2 processors");
		}
		itemsPerProc = new int[mpjSize];
		displs = new int[mpjSize];
		modelSet = new SingleModelCollection(options.getAlignment());
	}
		
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy#distribute(es.uvigo.darwin.prottest.util.collection.ModelCollection)
	 */
	public Model[] distribute(ModelCollection arrayListModel, ModelWeightComparator comparator) {

		Distributor distributor = new Distributor(this, arrayListModel, comparator, mpjMe, mpjSize);
		Thread distributorThread = new Thread(distributor);
		distributorThread.start();
		numberOfModels = arrayListModel.size();
		request();
		itemsPerProc = distributor.getItemsPerProc();
		displs = distributor.getDispls();
		
		computationDone();
		return arrayListModel.toArray(new Model[0]);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy#request()
	 */
	public void request() {
		
		startTime = System.currentTimeMillis();
		
		List<RunEstimator> runenvList = new ArrayList<RunEstimator>();
		
		Model[] lastComputedModel = new Model[1];
		while (true) {
			// send request to root
			Model[] modelToReceive = null;
			Model model = null;
			if (mpjMe > 0) {
				Request modelRequest = MPI.COMM_WORLD.Isend(lastComputedModel, 0, 1, MPI.OBJECT, 0, TAG_SEND_REQUEST);
				// prepare reception
				modelToReceive = new Model[1];
				// wait for request
				modelRequest.Wait();
				// receive model
				Request modelReceive = MPI.COMM_WORLD.Irecv(modelToReceive, 0, 1, MPI.OBJECT, 0, TAG_SEND_MODEL);
				modelReceive.Wait();
				model = modelToReceive[0];
			} else {
				// This strategy is an easy way to avoid the problem of thread-safety
				// in MPJ-Express. It works correctly, but it also causes to introduce
				// coupling amongst this class and Distributor, having to define two 
				// public attributes: rootModelRequest and rootModel.
				rootModelRequest = true;
				while (rootModelRequest) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						throw new ProtTestInternalException("Thread interrupted");
					}
				}
				model = rootModel;
			}
			if (model == null)
				break;
			else {
				// compute
				modelSet.add(model);
				RunEstimator runenv =
					factory.createRunEstimator(options, model);
				runenv.addObserver(this);
				
				if(!runenv.optimizeModel())
					throw new ProtTestInternalException("Optimization error");

				runenvList.add(runenv);
				lastComputedModel[0] = runenv.getModel();
			}
		}
		
		endTime = System.currentTimeMillis();
	}

}
