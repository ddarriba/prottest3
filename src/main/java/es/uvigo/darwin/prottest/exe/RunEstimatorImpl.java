package es.uvigo.darwin.prottest.exe;


import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

import static es.uvigo.darwin.prottest.util.logging.ProtTestLogger.*;

// TODO: Auto-generated Javadoc
/**
 * Template implementation for every subclass applying the
 * observer pattern into the analyzers. 
 * 
 * @author Francisco Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public abstract class RunEstimatorImpl
        extends ObservableModelUpdater
        implements RunEstimator {

    /** The application options instance. */
    protected ApplicationOptions options;
    /** The inner model to optimize. */
    protected Model model;
    /** Indicates if the inner model is yet optimized. */
    private boolean optimized;
    /** The time taken for the optimization. */
    protected String time;
    /** The number of rate categories. */
    protected int numberOfCategories;
    /** The independent process. */
    protected Process proc = null;
    /** The number of threads for parallel execution **/
    protected int numberOfThreads = 1;

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.exe.RunEstimator#getModel()
     */
    public Model getModel() {
        return model;
    }

    /**
     * Gets the thread pool size.
     * 
     * @return the thread pool size
     */
    protected int getPoolSize() {
        return 1;
    }

    /**
     * Gets the time taken to compute.
     * 
     * @return the time
     */
    public String getTime() {
        if (optimized) {
            return time;
        } else {
            throw new ProtTestInternalException("The model is not optimized");
        }
    }

    /**
     * Instantiates a new generic optimizer.
     * 
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     * @param numberOfThreads the number of threads to use in the optimization
     */
    public RunEstimatorImpl(ApplicationOptions options, Model model) {
        this(options, model, 1);
    }

    /**
     * Instantiates a new generic optimizer.
     * 
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     */
    public RunEstimatorImpl(ApplicationOptions options, Model model, int numberOfThreads) {
        this.options = options;
        this.model = model;
        this.optimized = model.isComputed();
        this.numberOfThreads = numberOfThreads;
    }
    
    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.exe.RunEstimator#optimizeModel(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
     */
    public boolean optimizeModel() {

        // notify task computation
        notifyObservers(getModel(),null);
        
        boolean result = true;
        try {
            if (!optimized) {
                result = runEstimator();
            }
        } catch (ModelOptimizationException ex) {
            
            severeln(ex.getMessage(), RunEstimator.class);
            
        } finally {
            // notify results
            notifyObservers(getModel(), options);
            deleteTemporaryFiles();
        }

        optimized = result;

        return result;

    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.exe.RunEstimator#report()
     */
    public void report() {

        if (optimized) {
            printReport();
        } else {
            throw new ProtTestInternalException("The model is not optimized");
        }

    }

    /**
     * Runs the estimator.
     * 
     * @return true, if successful
     */
    public abstract boolean runEstimator()
            throws ModelOptimizationException;

    /**
     * Prints a report of the execution.
     * 
     * @param out the output writer to write in
     * @param pfine if debug mode is on
     */
    public abstract void printReport();

    /**
     * Deletes temporary files of the execution.
     * 
     * @return true, if successful
     */
    protected abstract boolean deleteTemporaryFiles();

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        if (!this.optimizeModel()) {
            throw new ProtTestInternalException("Optimization error");
        }
    }

    protected void print(String message) {
        info(message, RunEstimator.class);
    }

    protected void println(String message) {
        infoln(message, RunEstimator.class);
    }

    protected void error(String message) {
        severe(message, RunEstimator.class);
    }

    protected void errorln(String message) {
        severeln(message, RunEstimator.class);
    }
    
    protected void pfine(String message) {
        fine(message, RunEstimator.class);
    }

    protected void pfineln(String message) {
        fineln(message, RunEstimator.class);
    }
    
    protected void pfiner(String message) {
        finer(message, RunEstimator.class);
    }

    protected void pfinerln(String message) {
        finerln(message, RunEstimator.class);
    }
}
