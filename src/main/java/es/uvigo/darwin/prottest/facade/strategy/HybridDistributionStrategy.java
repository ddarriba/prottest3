package es.uvigo.darwin.prottest.facade.strategy;

import es.uvigo.darwin.prottest.exe.ParallelModelEstimator;

import mpi.MPI;
import mpi.Request;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * The Class ImprovedDynamicDistributionStrategy.
 */
public class HybridDistributionStrategy extends DistributionStrategy {

    /** The Constant TAG_SEND_REQUEST. */
    private static final int TAG_SEND_REQUEST = 1;
    /** The Constant TAG_SEND_MODEL. */
    private static final int TAG_SEND_MODEL = 2;
    private int maxPEs;
    /** The number of available PEs. */
    int availablePEs;
    /** The root model request. */
    boolean rootModelRequest;
    /** The root model. */
    Model rootModel;
    private ParallelModelEstimator pme;
    private Model[] computedModels;
    private MultipleDistributor distributor;

    /**
     * Instantiates a new improved dynamic distribution strategy.
     *
     * @param mpjMe the rank of the current process in MPJ
     * @param mpjSize the size of the MPJ communicator
     * @param options the application options
     * @param cpManager the checkpoint manager, it can be null if checkpointing is not supported
     */
    public HybridDistributionStrategy(int mpjMe, int mpjSize, ApplicationOptions options, CheckPointManager cpManager) {
        this(mpjMe, mpjSize, options, cpManager, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Instantiates a new improved dynamic distribution strategy.
     *
     * @param mpjMe the rank of the current process in MPJ
     * @param mpjSize the size of the MPJ communicator
     * @param options the application options
     * @param cpManager the checkpoint manager, it can be null if checkpointing is not supported
     */
    public HybridDistributionStrategy(int mpjMe, int mpjSize, ApplicationOptions options, CheckPointManager cpManager, int numberOfThreads) {
        super(mpjMe, mpjSize, options, cpManager);
        if (mpjSize == 1) {
            throw new ProtTestInternalException("Dynamic Distribution Strategy" + " requires at least 2 processors");
        }
        itemsPerProc = new int[mpjSize];
        displs = new int[mpjSize];
        modelSet = new SingleModelCollection(options.getAlignment());
        maxPEs = numberOfThreads;
        availablePEs = maxPEs;
        pme = new ParallelModelEstimator(maxPEs, options.getAlignment());
        pme.addObserver(this);
    }
    
    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy#distribute(es.uvigo.darwin.prottest.util.collection.ModelCollection)
     */
    public Model[] distribute(ModelCollection arrayListModel, ModelWeightComparator comparator) {

        distributor = new MultipleDistributor(this, arrayListModel, comparator, mpjMe, mpjSize);
        Thread distributorThread = new Thread(distributor);
        distributorThread.start();
        numberOfModels = arrayListModel.size();
        request();

        computationDone();

        return computedModels;
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy#request()
     */
    public void request() {

        startTime = System.currentTimeMillis();

//        List<RunEstimator> runenvList = new ArrayList<RunEstimator>();

        while (true) {
            // send request to root
            Model[] modelToReceive = null;
            Model model = null;
            if (mpjMe > 0) {
                int[] sendMessage = {availablePEs};
                Request modelRequest = MPI.COMM_WORLD.Isend(sendMessage, 0, 1, MPI.INT, 0, TAG_SEND_REQUEST);
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

            if (model == null) {
                break;
            } else {
                System.out.println("--- [" + mpjMe +"] "+ availablePEs + " PEs --> " 
                        + model.getModelName() + "(" + MultipleDistributor.getPEs(model, maxPEs) + ")");
                availablePEs -= MultipleDistributor.getPEs(model, maxPEs);
                // compute
                modelSet.add(model);
                RunEstimator runenv =
                        factory.createRunEstimator(options, model, MultipleDistributor.getPEs(model, maxPEs));
//                runenv.addObserver(this);
                pme.execute(runenv);

                while (availablePEs <= 0) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new ProtTestInternalException("Thread interrupted");
                    }
                }
//                if (!runenv.optimizeModel()) {
//                    throw new ProtTestInternalException("Optimization error");
//                }
//
//                runenvList.add(runenv);
//                lastComputedModel[0] = runenv.getModel();
            }
        }

        endTime = System.currentTimeMillis();

        while (pme.hasMoreTasks()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new ProtTestInternalException("Thread interrupted");
                    }
                }
        
        if (mpjMe > 0) {
            gather();
        } else {
            computedModels = gather();
        }
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#update(es.uvigo.darwin.prottest.observer.Observable, java.lang.Object)
     */
    @Override
    public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {
        if (options != null && model.isComputed()) {
            availablePEs += MultipleDistributor.getPEs(model, maxPEs);
        }
        super.update(o, model, options);
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
        if (distributor != null) {
            itemsPerProc = distributor.getItemsPerProc();
            displs = distributor.getDispls();
        }

        // gathering optimized models
        MPI.COMM_WORLD.Gatherv(modelSet.toArray(new Model[0]), 0, modelSet.size(), MPI.OBJECT,
                allModels, 0, itemsPerProc, displs, MPI.OBJECT, 0);
//		else
//			allModels = modelSet.toArray(new Model[0]);

        return allModels;
    }
}
