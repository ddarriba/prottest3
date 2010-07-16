/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.darwin.prottest.exe;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import pal.alignment.Alignment;

/**
 * Allows the execution of many model optimizations in a thread pool
 * 
 * @author Diego Darriba
 * @since 3.0
 */
public class ParallelModelEstimator extends ObservableModelUpdater
        implements ModelUpdaterObserver {

    /** The runtime **/
    private Runtime runtime = Runtime.getRuntime();
    /** The size of parallel tasks **/
    private int maxNumberOfTasks;
    /** The list of model estimators **/
    private List<RunEstimator> estimatorList;
    /** The pool of threads **/
    private ExecutorService threadPool;
    /** Collection of callable objects (tasks) **/
    private Collection<Callable<Object>> c = new ArrayList<Callable<Object>>();

    /**
     * Instantiates a new ParallelModelEstimator with no models to optimize. 
     * The size of the thread pool is equal to the number of available cores in the machine.
     * 
     * @param alignment the common alignment of the models
     */
    public ParallelModelEstimator(Alignment alignment) {

        this(-1, alignment);

    }

    /**
     * Instantiates a new ParallelModelEstimator with a set of models to optimize
     * The size of the thread pool is equal to the number of available cores in the machine.
     * 
     * @param modelCollection the collection of models to optimize in the pool
     */
    public ParallelModelEstimator(ModelCollection modelCollection) {

        this(-1, modelCollection);

    }

    /**
     * Instantiates a new ParallelModelEstimator with no models to optimize and
     * a fixed size of the thread pool
     *
     * @param availableThreads the size of the thread pool 
     * @param alignment the common alignment of the models
     */
    public ParallelModelEstimator(int availableThreads, Alignment alignment) {

        if (availableThreads < 0) {
            availableThreads = runtime.availableProcessors();
        }
        this.maxNumberOfTasks = availableThreads;
        this.estimatorList = new ArrayList<RunEstimator>(maxNumberOfTasks);
        this.threadPool = Executors.newFixedThreadPool(maxNumberOfTasks);
    }

    /**
     * Instantiates a new ParallelModelEstimator with a set of models to optimize and
     * a fixed size of the thread pool
     *
     * @param availableThreads the size of the thread pool
     * @param modelCollection the collection of models to optimize in the pool
     */
    public ParallelModelEstimator(int availableThreads, ModelCollection modelCollection) {

        if (availableThreads < 0) {
            availableThreads = runtime.availableProcessors();
        }
        this.estimatorList = new ArrayList<RunEstimator>(maxNumberOfTasks);

    }

    /**
     * Executes the model optimization
     * 
     * @param estimator the model estimator to execute
     * 
     * @return if succesfully added the task
     */
    public boolean execute(RunEstimator estimator) {
        estimator.addObserver(this);

        boolean added = estimatorList.add(estimator);
        c.add(Executors.callable(estimator));
        Collection<Future<Object>> futures = null;

        threadPool.execute(estimator);

        return added;
    }

    /* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.observer.ModelUpdaterObserver#update(es.uvigo.darwin.prottest.observer.ObservableModelUpdater, es.uvigo.darwin.prottest.model.Model, es.uvigo.darwin.prottest, es.uvigo.darwin.prottest.global.options.ApplicationOptions)
	 */
    public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {
        notifyObservers(model, options);
    }

    /**
     * Checks if exist more tasks in the task queue
     * 
     * @return true, if exist more tasks to execute
     */
    public boolean hasMoreTasks() {
        for (RunEstimator estimator : estimatorList) {
            if (!estimator.getModel().isComputed()) {
                return true;
            }
        }
        return false;
    }
}
