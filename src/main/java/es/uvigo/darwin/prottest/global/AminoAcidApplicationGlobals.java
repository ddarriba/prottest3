package es.uvigo.darwin.prottest.global;

import java.util.Arrays;
import java.util.List;

/**
 * The global application settings and parameter names for
 * amino-acid alignment sequences.
 */
public class AminoAcidApplicationGlobals extends ApplicationGlobals {

	/** The Amino-Acid supported matrices. */
	public static String[] ALL_MATRICES	= { 
		 "JTT",
		 "LG",
		 "DCMut",
		 "MtREV",
		 "MtMam",
		 "MtArt",
		 "Dayhoff",
		 "WAG",
		 "RtREV",
		 "CpREV",
		 "Blosum62",
		 "VT",
		 "HIVb",
		 "HIVw",
                 "FLU"
	};
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.global.ApplicationGlobals#getSupportedMatrices()
	 */
	public List<String> getSupportedMatrices() {
		return Arrays.asList(ALL_MATRICES);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.global.ApplicationGlobals#getModelName(java.lang.String, int)
	 */
	public String getModelName(String matrix, int frequenciesDistribution) {
		return matrix;
	}
}
