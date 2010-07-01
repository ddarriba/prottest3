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
    public static final String PARAM_SORT_BY = "sort";          // sort field
    /** Display all framework comparison parameter token. */
    public static final String PARAM_ALL_FRAMEWORK_COMPARISON = "all";	// display 7-framework comparison table
    /** Optimization strategy selection parameter token. */
    public static final String PARAM_OPTIMIZATION_STRATEGY = "S";	// optimization strategy
    /** Sample size mode parameter token. */
    public static final String PARAM_SAMPLE_SIZE_MODE = "sample";	// sample size for AICc and BIC corrections
    /** Specific sample size parameter token. */
    public static final String PARAM_SPECIFIC_SAMPLE_SIZE = "size";	// specific sample size
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
    /** Verbose mode parameter token. */
    public static final String PARAM_VERBOSE = "verbose";
}
