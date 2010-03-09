package es.uvigo.darwin.prottest.exe;

import java.io.PrintWriter;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.util.exception.OSNotSupportedException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

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
	
	protected Process proc = null;

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
		if (optimized)
			return time;
		else
			throw new ProtTestInternalException("The model is not optimized");
	}
	
	/**
	 * Instantiates a new generic optimizer.
	 * 
	 * @param options the application options instance
	 * @param model the amino-acid model to optimize
	 */
	public RunEstimatorImpl(ApplicationOptions options, Model model) {
		this.options = options;
		this.model = model;
		this.optimized = model.isComputed();
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.exe.RunEstimator#optimizeModel(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
	 */
	public boolean optimizeModel() 
		throws OSNotSupportedException {
		
		boolean result = true;
		try {
			if (!optimized) {
				result = runEstimator();
			}
		} finally {
			notifyObservers(getModel(), options);
			deleteTemporaryFiles();
		}
		
		optimized = result;
		
		return result;
		
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.exe.RunEstimator#report(java.io.PrintWriter, boolean)
	 */
	public void report(PrintWriter out, boolean verbose) {
		
		if (optimized) {
			printReport(out, verbose);
		} else {
			throw new ProtTestInternalException("The model is not optimized");
		}
		
	}
	
	/**
	 * Runs the estimator.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean runEstimator();
	
	/**
	 * Prints a report of the execution.
	 * 
	 * @param out the output writer to write in
	 * @param verbose if debug mode is on
	 */
	public abstract void printReport(PrintWriter out, boolean verbose);
	
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

		if (!this.optimizeModel())
			throw new ProtTestInternalException("Optimization error");
	}
}
