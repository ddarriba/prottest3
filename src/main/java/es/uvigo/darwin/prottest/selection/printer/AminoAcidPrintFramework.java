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
	 * 
	 * @param informationCriterion the information criterion
	 */
	public AminoAcidPrintFramework(InformationCriterion informationCriterion) {
		super(informationCriterion);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.printer.PrintFramework#printRelativeImportance(java.io.PrintWriter)
	 */
	@Override
	void printRelativeImportance() {
		
		println("***********************************************");
		println("Relative importance of parameters");
		println("***********************************************");
		println("  alpha       (+G):  " + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getAlphaImportance(), 2));//calculateAlphaImportance(false), 2));
		println("  p-inv       (+I):  " + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getInvImportance(), 2));
		println("  alpha+p-inv (+I+G):" + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getAlphaInvImportance(), 2));
		println("  freqs       (+F):  " + ProtTestFormattedOutput.space(3, ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getFImportance(), 2));
		println("");
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.selection.printer.PrintFramework#printModelAveragedEstimation(java.io.PrintWriter)
	 */
	@Override
	void printModelAveragedEstimation() {
		
		println("***********************************************");
		println("Model-averaged estimate of parameters");
		println("***********************************************");
		println("  alpha (+G):        " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getOverallAlpha(), 2));
		println("  p-inv (+I):        " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getOverallInv(), 2));
		println("  alpha (+I+G):      " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getOverallAlphaInv(), 2));
		println("  p-inv (+I+G):      " + ProtTestFormattedOutput.space(3,  ' ') + ProtTestFormattedOutput.getDecimalString(getInformationCriterion().getOverallInvAlpha(), 2));
	}

}
