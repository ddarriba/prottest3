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
 * Console execution parameters
 * 
 * @author diego
 */
public interface ProtTestConsoleParameters {

    /** Data type parameter token. */
    public static final String PARAM_DATA_TYPE = "d";		// data type
    /** Nucleotide data type parameter value. */
    public static final String DATA_TYPE_NUCLEOTIDE = "nt";
    /** Amino-acid data type parameter value. */
    public static final String DATA_TYPE_AMINOACID = "aa";	// default
    /** Number of threads parameter token. */
    public static final String PARAM_NUM_THREADS = "threads";
    /** Alignment file parameter token. */
    public static final String PARAM_ALIGNMENT_FILE = "i";	// alignment file (required)
    /** Output file parameter token. */
    public static final String PARAM_OUTPUT_FILE = "o";		// output file
    /** Tree file parameter token. */
    public static final String PARAM_TREE_FILE = "t";		// tree file
    /** Sort by parameter token. */
    public static final String PARAM_DO_AIC = "AIC";            // sort models by AIC
    public static final String PARAM_DO_BIC = "BIC";            // sort models by BIC
    public static final String PARAM_DO_AICC = "AICC";          // sort models by AICc
    public static final String PARAM_DO_DT = "DT";              // sort models by DT
    /** Display all framework comparison parameter token. */
    public static final String PARAM_ALL_FRAMEWORK_COMPARISON = "all";	// display 7-framework comparison table
    /** Optimization strategy selection parameter token. */
    public static final String PARAM_TREE_SEARCH_OP = "s";	// tree search operation
    /** Optimization strategy selection parameter token. */
    public static final String PARAM_OPTIMIZATION_STRATEGY = "S";	// optimization strategy
    /** Display Newick tree parameter token. */
    public static final String PARAM_DISPLAY_NEWICK_TREE = "t1";	// display newick tree
    /** Display ASCII tree parameter token. */
    public static final String PARAM_DISPLAY_ASCII_TREE = "t2";		// display ASCII tree
    /** Display Consensus tree parameter token. */
    public static final String PARAM_DISPLAY_CONSENSUS_TREE = "tc";	// display Consensus tree
    /** Include models with observed frequencies parameter token. */
    public static final String PARAM_PLUSF = "F";
    /** Include models with a proportion invariable sites parameter token. */
    public static final String PARAM_PLUSI = "I";
    /** Include models with rate variation among sites and number of categories parameter token. */
    public static final String PARAM_PLUSG = "G";
    /** Include models with a proportion of invariable sites and rate variation parameter token. */
    public static final String PARAM_PLUSIG = "IG";
    /** User defined number of categories parameter token. */
    public static final String PARAM_NCAT = "ncat";
    /** Include models with all distributions (like -I -G). */
    public static final String PARAM_ALL_DISTRIBUTIONS = "all-distributions";
    /** Enable or Disable output logging files. */
    public static final String PARAM_LOGGING = "log";
    /** Verbose mode parameter token. */
    public static final String PARAM_VERBOSE = "verbose";
}
