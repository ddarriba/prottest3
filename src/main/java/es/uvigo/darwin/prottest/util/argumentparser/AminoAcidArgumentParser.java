package es.uvigo.darwin.prottest.util.argumentparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;

/**
 * The Class AminoAcidArgumentParser.
 */
public class AminoAcidArgumentParser extends ProtTestArgumentParser {

	/** Amino-Acid model matrices to include in the analysis. */
	public static List<String> PARAM_MATRICES;

	/** Include models with all matrices (like -JTT -LG -etc,). */
	public static final String PARAM_ALL_MATRICES = "all-matrices";
	
	static {
		ApplicationGlobals apGlobals = ProtTestFactory.getInstance().getApplicationGlobals();
		PARAM_MATRICES = apGlobals.getSupportedMatrices();
		Map<String, String> allMatrices = new HashMap<String, String>(PARAM_MATRICES.size());
		for (String matrix : PARAM_MATRICES) {
			valuesRequired.put(matrix, false);
			allMatrices.put(matrix, "T");
		}
		valuesRequired.put(PARAM_ALL_MATRICES, false);
		specialArguments.put(PARAM_ALL_MATRICES, allMatrices);
	}
	
	/**
	 * Instantiates a new amino-acid argument parser.
	 * 
	 * @param args the command line arguments
	 * @param options the application options
	 * 
	 * @throws IllegalArgumentException when exists some error in the command line argument
	 */
	public AminoAcidArgumentParser(String[] args, ApplicationOptions options)
			throws IllegalArgumentException {
		super(args, options);
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser#getMatrices()
	 */
	public List<String> getMatrices() {
		return PARAM_MATRICES;
	}
}
