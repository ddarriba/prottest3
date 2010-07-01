package es.uvigo.darwin.prottest.exe;


import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;

/**
 * The Interface RunEstimator. Should be implemented by any class which launches
 * an external likelihood calculator or directly calculates likelihood for any 
 * kind of model.
 * 
 * @author Federico Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public interface RunEstimator extends Runnable {

	/**
	 * Gets the inner optimized model.
	 * 
	 * @return the model
	 */
	public Model getModel();
	
	/**
	 * Optimizes the parameters of the model.
	 * 
	 * @return true, if there is no error in the execution of the optimizer
	 */
	public boolean optimizeModel();
	
	/**
	 * Reports out the result of the optimization into the application loggers: 
         * the optimized parameters of the model.
	 */
	public void report();
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.observer.ObservableModelUpdater#addObserver(es.uvigo.darwin.prottest.observer.ObservableModelUpdater)
	 */
	public void addObserver(ModelUpdaterObserver o);

}
