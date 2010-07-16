package es.uvigo.darwin.prottest.global;

import java.util.Arrays;
import java.util.List;

/**
 * The global application settings and parameter names for
 * amino-acid alignment sequences using RAxML.
 */
public class RaxmlAminoAcidApplicationGlobals extends
		AminoAcidApplicationGlobals {

	/** The Amino-Acid substitution matrices supported by RAxML. */
	public static String[] ALL_MATRICES	= { 
		 "JTT",
		 "DCMut",
		 "MtREV",
		 "MtMam",
		 "Dayhoff",
		 "WAG",
		 "RtREV",
		 "CpREV",
		 "Blosum62",
		 "VT",
	};
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.global.ApplicationGlobals#getSupportedMatrices()
	 */
	public List<String> getSupportedMatrices() {
		return Arrays.asList(ALL_MATRICES);
	}
}
