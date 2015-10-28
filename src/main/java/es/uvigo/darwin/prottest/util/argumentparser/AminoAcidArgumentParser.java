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
package es.uvigo.darwin.prottest.util.argumentparser;

import es.uvigo.darwin.prottest.global.ApplicationGlobals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import java.util.Properties;

/**
 * The Class AminoAcidArgumentParser.
 */
public class AminoAcidArgumentParser extends ProtTestArgumentParser {

	/** Amino-Acid model matrices to include in the analysis. */
	public static final List<String> PARAM_MATRICES;

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
        
        /* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser#getMatrices()
	 */
        protected Properties checkArgs(String[] args)
            throws IllegalArgumentException {
            
            // set all-matrices as default
            boolean existMatrix = false;
            int index = 0;
            String modArgs[] = new String[args.length + 1];
            for (String arg : args) {
                if (PARAM_MATRICES.contains(arg.substring(1))) {
                    existMatrix = true;
                }
                modArgs[index] = arg;
                index++;
            }
            if (!existMatrix) {
                modArgs[index] = "-" + PARAM_ALL_MATRICES;
            } else {
                modArgs = args;
            }
            
            return super.checkArgs(modArgs);
        }
}
