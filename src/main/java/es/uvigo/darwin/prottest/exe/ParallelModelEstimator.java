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
 *
 * @author diego
 */
public class ParallelModelEstimator extends ObservableModelUpdater
    implements ModelUpdaterObserver {

    
    
    /** The runtime **/
    private Runtime runtime = Runtime.getRuntime();
    /** The size of parallel tasks **/
    private int maxNumberOfTasks;
    private List<RunEstimator> estimatorList;
    private ExecutorService threadPool;
    private Collection<Callable<Object>> c = new ArrayList<Callable<Object>>();
    
    public ParallelModelEstimator(Alignment alignment) {

        this(-1, alignment);

    }

    public ParallelModelEstimator(ModelCollection modelCollection) {

        this(-1, modelCollection);

    }

    public ParallelModelEstimator(int availableThreads, Alignment alignment) {

        if (availableThreads < 0) {
            availableThreads = runtime.availableProcessors();
        }
        this.maxNumberOfTasks = availableThreads;
        this.estimatorList = new ArrayList<RunEstimator>(maxNumberOfTasks);
        this.threadPool = Executors.newFixedThreadPool(maxNumberOfTasks);
    }
    
    public ParallelModelEstimator(int availableThreads, ModelCollection modelCollection) {

        if (availableThreads < 0) {
            availableThreads = runtime.availableProcessors();
        }
        this.estimatorList = new ArrayList<RunEstimator>(maxNumberOfTasks);

    }

    /**
     * Executes the model optimization
     * 
     * @param model the model
     */
    public boolean execute(RunEstimator estimator) {
        estimator.addObserver(this);
        
//        Thread t = new Thread(estimator);
//        t.start();
        boolean added = estimatorList.add(estimator);
        c.add(Executors.callable(estimator));
        Collection<Future<Object>> futures = null;
//        try {
            threadPool.execute(estimator);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return added;
    }

    public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {
        notifyObservers(model, options);
    }
 
    public boolean hasMoreTasks() {
        for (RunEstimator estimator : estimatorList) {
            if (!estimator.getModel().isComputed())
                return true;
        }
        return false;
    }
}
