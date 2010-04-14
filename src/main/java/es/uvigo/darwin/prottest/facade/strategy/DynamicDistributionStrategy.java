package es.uvigo.darwin.prottest.facade.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mpi.MPI;
import mpi.Request;
import mpi.Status;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * The Class DynamicDistributionStrategy.
 */
public class DynamicDistributionStrategy extends DistributionStrategy {

	/** MPJ Tag for requesting a new model. */
	private static final int TAG_SEND_REQUEST = 1;
	
	/** MPJ Tag for sending a new model. */
	private static final int TAG_SEND_MODEL = 2;
	
	/**
	 * Instantiates a new dynamic distribution strategy.
	 * 
	 * @param mpjMe the rank of the current process in MPJ
	 * @param mpjSize the size of the MPJ communicator
	 * @param options the application options
	 * @param cpManager the checkpoint manager, it can be null if checkpointing is not supported
	 */
	public DynamicDistributionStrategy(int mpjMe, int mpjSize, ApplicationOptions options, CheckPointManager cpManager) {
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

		numberOfModels = arrayListModel.size();
		Collections.sort(arrayListModel, comparator);
		for (Model model : arrayListModel) {
			Model[] computedModel = new Model[1];
			// getModel request
			Request modelRequest = MPI.COMM_WORLD.Irecv(computedModel, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, TAG_SEND_REQUEST);
			// prepare model
			Model[] modelToSend = new Model[1];
			modelToSend[0] = model;
			// wait for request
			Status requestStatus = modelRequest.Wait();
			if (computedModel[0] != null) {
				// set checkpoint
				int index = arrayListModel.indexOf(computedModel[0]);
				arrayListModel.set(index, computedModel[0]);
				setCheckpoint(arrayListModel);
			}
			// send model
			Request modelSend = MPI.COMM_WORLD.Isend(modelToSend, 0, 1, MPI.OBJECT, requestStatus.source, TAG_SEND_MODEL);
			// update structures
			itemsPerProc[requestStatus.source]++;
			// wait for send
			modelSend.Wait();
		}
		itemsPerProc[0] = modelSet.size();
		displs[0] = 0;
		for (int i = 1; i < mpjSize; i++)
			displs[i] = displs[i-1] + itemsPerProc[i-1];

		// finalize
		for (int i = 1; i < mpjSize; i++) {
			Model[] computedModel = new Model[1];
			Request modelRequest = MPI.COMM_WORLD.Irecv(computedModel, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, TAG_SEND_REQUEST);
			Model[] modelToSend = { null };
			// wait for request
			Status requestStatus = modelRequest.Wait();
			if (computedModel[0] != null) {
				// set checkpoint
				int index = arrayListModel.indexOf(computedModel[0]);
				arrayListModel.set(index, computedModel[0]);
				setCheckpoint(arrayListModel);
			}
			// send null model
			Request modelSend = MPI.COMM_WORLD.Isend(modelToSend, 0, 1, MPI.OBJECT, requestStatus.source, TAG_SEND_MODEL);

			modelSend.Wait();
		}
		
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
			Request modelRequest = MPI.COMM_WORLD.Isend(lastComputedModel, 0, 1, MPI.OBJECT, 0, TAG_SEND_REQUEST);
			// prepare reception
			Model[] modelToReceive = new Model[1];
			// wait for request
			modelRequest.Wait();
			// receive model
			Request modelReceive = MPI.COMM_WORLD.Irecv(modelToReceive, 0, 1, MPI.OBJECT, 0, TAG_SEND_MODEL);
			modelReceive.Wait();
			Model model = modelToReceive[0];
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
