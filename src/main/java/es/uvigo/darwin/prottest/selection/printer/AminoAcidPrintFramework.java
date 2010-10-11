package es.uvigo.darwin.prottest.selection.printer;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;
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
		println("  alpha       (+G):  " + ProtTestFormattedOutput.space(3, ' ') + getDisplayValue(ic.getAlphaImportance(), PARAMETER_G, ic.isExistGammaModels()));
		println("  p-inv       (+I):  " + ProtTestFormattedOutput.space(3, ' ') + getDisplayValue(ic.getInvImportance(), PARAMETER_I, ic.isExistInvModels()));
		println("  alpha+p-inv (+I+G):" + ProtTestFormattedOutput.space(3, ' ') + getDisplayValue(ic.getAlphaInvImportance(), PARAMETER_IG, ic.isExistGammaInvModels()));
		println("  freqs       (+F):  " + ProtTestFormattedOutput.space(3, ' ') + getDisplayValue(ic.getFImportance(), PARAMETER_F, ic.isExistFModels()));
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
		println("  alpha (+G):        " + ProtTestFormattedOutput.space(3,  ' ') + getDisplayValue(ic.getOverallAlpha(), PARAMETER_G, ic.isExistGammaModels()));
		println("  p-inv (+I):        " + ProtTestFormattedOutput.space(3,  ' ') + getDisplayValue(ic.getOverallInv(), PARAMETER_I, ic.isExistInvModels()));
		println("  alpha (+I+G):      " + ProtTestFormattedOutput.space(3,  ' ') + getDisplayValue(ic.getOverallAlphaInv(), PARAMETER_IG, ic.isExistGammaInvModels()));
		println("  p-inv (+I+G):      " + ProtTestFormattedOutput.space(3,  ' ') + getDisplayValue(ic.getOverallInvAlpha(), PARAMETER_IG, ic.isExistGammaInvModels()));
	}

}
