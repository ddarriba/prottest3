package es.uvigo.darwin.prottest.facade.strategy;

import mpi.MPI;
import mpi.Request;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.ParallelModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * This distribution strategy makes an static distribution of the models amongst the
 * processors. This mean the model distribution it is completely and heuristically done 
 * before computing any likelihood value. 
 */
public class StaticDistributionStrategy extends DistributionStrategy {
	
	/**
	 * Instantiates a new static distribution strategy.
	 * 
	 * @param mpjMe the rank of the current process in MPJ
	 * @param mpjSize the size of the MPJ communicator
	 * @param options the application options
	 */
	public StaticDistributionStrategy(int mpjMe, int mpjSize, ApplicationOptions options) {
		super(mpjMe, mpjSize, options, null);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy#distribute(es.uvigo.darwin.prottest.util.collection.ModelCollection)
	 */
	public Model[] distribute(ModelCollection arrayListModel, ModelWeightComparator comparator) {

		itemsPerProc = new int[mpjSize];
		displs = new int [mpjSize];
		numberOfModels = arrayListModel.size();
		
		ParallelModelCollection pmi = new ParallelModelCollection(arrayListModel, mpjSize, comparator);
		Request[] requests = new Request[mpjSize - 1];
		for (int procToSend = 1; procToSend < mpjSize; procToSend++) {
			ModelCollection[] modelsToSend = new SingleModelCollection[1];
			modelsToSend[0] = pmi.getModelCollection(procToSend);
			itemsPerProc[procToSend] = modelsToSend[0].size();
			requests[procToSend - 1] = MPI.COMM_WORLD.Isend(modelsToSend, 0, 1, MPI.OBJECT, procToSend, 2);
		}
		modelSet = pmi.getModelCollection(mpjMe);
		itemsPerProc[0] = modelSet.size();
		displs[0] = 0;
		for (int i = 1; i < mpjSize; i++)
			displs[i] = displs[i-1] + itemsPerProc[i-1];

		Request.Waitall(requests);
		
		startTime = System.currentTimeMillis();
		Model[] models = compute(modelSet);
		endTime = System.currentTimeMillis();
		
		return models;
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy#request()
	 */
	public void request() {
		
		startTime = System.currentTimeMillis();
		
		ModelCollection[] modelsToReceive = new SingleModelCollection[1];
		Request request = MPI.COMM_WORLD.Irecv(modelsToReceive, 0, 1, MPI.OBJECT, 0, 2);
		request.Wait();
		modelSet = modelsToReceive[0];
		
		compute(modelSet);
		
		endTime = System.currentTimeMillis();
	}
	
	/**
	 * Computes the likelihood value of the model set.
	 * 
	 * @param modelSet the model set to compute
	 * 
	 * @return the array of models after computing likelihood
	 */
	private Model[] compute(ModelCollection modelSet) {
		RunEstimator[] runenv = new RunEstimator[modelSet.size()];
		
		int current = 0;
		for (Model model : modelSet) {

			runenv[current] 
		       = factory.createRunEstimator(options, model);
			runenv[current].addObserver(this);
			
			if(!runenv[current].optimizeModel())
				throw new ProtTestInternalException("Optimization error");

			current++;
		}

		return gather();
	}
	
	/**
	 * Gathers the models of all processors into the root one. This method should
	 * be called by every processor after computing likelihood value of whole model set.
	 * 
	 * This method will return an empty array of models for every non-root processor
	 * 
	 * @return the array of gathered models 
	 */
	private Model[] gather() {
		
		Model[] allModels = new Model[numberOfModels];
		
		// gathering optimized models
		if (mpjSize > 1)
			MPI.COMM_WORLD.Gatherv(modelSet.toArray(new Model[0]), 0, modelSet.size(), MPI.OBJECT, 
					allModels, 0, itemsPerProc, displs, MPI.OBJECT, 0);
		else
			allModels = modelSet.toArray(new Model[0]);
		
		return allModels;
	}
}
