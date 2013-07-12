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
package es.uvigo.darwin.prottest.global;

/**
 * The global constants of ProtTest-HPC
 *
 * @author diego
 */
public interface ProtTestConstants {

    /** The exit status for signals caused by the non existence of properties file. */
    static final int EXIT_NO_PROPERTIES = 101;
    // third-party applications
    /** The Constant ANALYZER_PHYML. It is used to check the analyzer in the properties file */
    static final String ANALYZER_PHYML = "phyml";
    /** The Constant ANALYZER_RAXML. It is used to check the analyzer in the properties file */
    static final String ANALYZER_RAXML = "raxml";
    /** The number of states for amino-acids. */
    static final int AMINOACID_NUM_STATES = 20;
    
    /** The empirical frequencies parameter. */
    static final String PARAMETER_F = "+F";
    /** The invariant sites parameter. */
    static final String PARAMETER_I = "+I";
    /** The gamma distribution parameter. */
    static final String PARAMETER_G = "+G";
    /** The invariant sites and gamma distribution parameter. */
    static final String PARAMETER_IG = "+I+G";

    // tree search operation
    /** NNI moves */
    static final String TREE_SEARCH_NNI = "NNI";
    /** SPR moves */
    static final String TREE_SEARCH_SPR = "SPR";
    /** Best of NNI and SPR moves */
    static final String TREE_SEARCH_BEST = "BEST";

    /** Fixed BIONJ topology for every model. */
    static final int OPTIMIZE_FIXED_BIONJ = 0;
    /** BIONJ topology for each model. */
    static final int OPTIMIZE_BIONJ = 1;
    /** Maximum likelighood topology for each model. */
    static final int OPTIMIZE_ML = 2;
    /** User defined topology. */
    static final int OPTIMIZE_USER = 3;
    /** Set of descriptions of the optimization strategies. */
    public static final String[] OPTIMIZE_NAMES = {
        "Fixed BIONJ JTT",
        "BIONJ Tree",
        "Maximum Likelihood tree",
        "User defined topology"
    };
    /** Set of values of the optimization strategies. */
    static final int[] OPTIMIZE_VALUES = {
        OPTIMIZE_FIXED_BIONJ,
        OPTIMIZE_BIONJ,
        OPTIMIZE_ML,
        OPTIMIZE_USER
    };
    static final String[] CRITERION_NAMES = {
        "AKAIKE INFORMATION CRITERION",
        "BAYESIAN INFORMATION CRITERION",
        "CORRECTED AKAIKE INFORMATION CRITERION",
        "DECISION THEORY CRITERION",
        "LOG-LIKELIHOOD"
    };
    // default settings
    /** The default setting of property "Display Newick tree". */
    static final boolean DEFAULT_DISPLAY_NEWICK_TREE = false;
    /** The default setting of property "Number of threads". */
    static final int DEFAULT_THREADS = 1;
    /** The default setting of property "Display ASCII tree". */
    static final boolean DEFAULT_DISPLAY_ASCII_TREE = false;
    /** The default setting of property "Display Consensus tree". */
    static final boolean DEFAULT_DISPLAY_CONSENSUS_TREE = false;
    /** The default setting of property "Debug" (Verbose mode). */
    static final boolean DEFAULT_DEBUG = false;
    /** The default setting of property "Display a 7-framework comparison". */
    static final boolean DEFAULT_COMPARE_ALL = false;
    /** The default setting of property "Compute all matrices". */
    static final boolean DEFAULT_MATRICES = false;
    /** The default setting of property "Compute all distributions". */
    static final boolean DEFAULT_DISTRIBUTIONS = false;
    /** The default setting of property "Number of categories". */
    static final int DEFAULT_NCAT = 4;
    /** The default setting of property "Optimization strategy mode". */
    static final int DEFAULT_STRATEGY_MODE = OPTIMIZE_FIXED_BIONJ;
    
    // Tree properties
    /** Tree property for write branch length averaging */
    static final String PROP_BRANCH_LENGTH_AVERAGING = "branch_length_averaging";

    static final int IMPORTANCE_PRECISSION = 3;
    static final int CRITERION_PRECISSION = 5;
}
