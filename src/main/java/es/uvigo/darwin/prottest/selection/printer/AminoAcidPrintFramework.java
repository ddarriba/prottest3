/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
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
