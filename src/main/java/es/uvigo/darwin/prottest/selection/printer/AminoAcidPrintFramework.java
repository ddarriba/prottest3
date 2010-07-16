package es.uvigo.darwin.prottest.selection.printer;


import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

// TODO: Auto-generated Javadoc
/**
 * The Class AminoAcidPrintFramework.
 */
public class AminoAcidPrintFramework extends PrintFramework {

	/**
	 * Instantiates a new amino acid print framework.
	 */
	public AminoAcidPrintFramework() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.printer.PrintFramework#printRelativeImportance(java.io.PrintWriter)
	 */
	@Override
	void printRelativeImportance(InformationCriterion ic) {
		
		println("***********************************************");
		println("Relative importance of parameters");
		println("***********************************************");
		println("  alpha       (+G):  " + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(ic.getAlphaImportance(), 2));//calculateAlphaImportance(false), 2));
		println("  p-inv       (+I):  " + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(ic.getInvImportance(), 2));
		println("  alpha+p-inv (+I+G):" + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(ic.getAlphaInvImportance(), 2));
		println("  freqs       (+F):  " + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(ic.getFImportance(), 2));
		println("");
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.printer.PrintFramework#printModelAveragedEstimation(java.io.PrintWriter)
	 */
	@Override
	void printModelAveragedEstimation(InformationCriterion ic) {
		
		println("***********************************************");
		println("Model-averaged estimate of parameters");
		println("***********************************************");
		println("  alpha (+G):        " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(ic.getOverallAlpha(), 2));
		println("  p-inv (+I):        " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(ic.getOverallInv(), 2));
		println("  alpha (+I+G):      " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(ic.getOverallAlphaInv(), 2));
		println("  p-inv (+I+G):      " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(ic.getOverallInvAlpha(), 2));
	}

}
