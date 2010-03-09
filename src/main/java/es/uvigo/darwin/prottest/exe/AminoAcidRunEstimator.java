package es.uvigo.darwin.prottest.exe;

import java.io.PrintWriter;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;

/**
 * A generic optimizer for amino-acid models
 * 
 * @author Francisco Abascal
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

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.exe.RunEstimator#report(java.io.PrintWriter)
	 */
	public void printReport (PrintWriter out, boolean verbose) {
		model.printReport(out, verbose);
		if (time != null)
			out.println("     (" + time + ")");
		out.println();
	}

}
