package es.uvigo.darwin.prottest.exe;


import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;

/**
 * A generic optimizer for amino-acid models
 * 
 * @author Federico Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public abstract class AminoAcidRunEstimator extends RunEstimatorImpl {
	
	/**
	 * Instantiates a new generic optimizer for amino-acid models.
	 * 
	 * @param options the application options instance
	 * @param model the amino-acid model to optimize
	 */
	public AminoAcidRunEstimator(ApplicationOptions options, Model model) {
		super(options, model);
	}
        
        /**
	 * Instantiates a new generic optimizer for amino-acid models.
	 * 
	 * @param options the application options instance
	 * @param model the amino-acid model to optimize
         * @param numberOfThreads the number of threads to use in the optimization
	 */
	public AminoAcidRunEstimator(ApplicationOptions options, Model model, int numberOfThreads) {
		super(options, model, numberOfThreads);
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.exe.RunEstimator#report()
	 */
	public void printReport () {
		model.printReport();
		if (time != null)
			ProtTestLogger.getDefaultLogger().info("     (" + time + ")\n");
                ProtTestLogger.getDefaultLogger().info("\n");
                ProtTestLogger.getDefaultLogger().flush();
	}

}
