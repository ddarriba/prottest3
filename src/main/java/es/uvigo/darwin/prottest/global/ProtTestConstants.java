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
    

    // size modes
    /** Shannon sample size mode. */
    static final int SIZEMODE_SHANNON = 0;
    /** Shannon NxL sample size mode. */
    static final int SIZEMODE_SHANNON_NxL = 1;
    /** Alignment length sample size mode. */
    static final int SIZEMODE_ALIGNMENT = 2;
    /** Alignment length variable sample size mode. */
    static final int SIZEMODE_ALIGNMENT_VAR = 3;
    /** NxL sample size mode. */
    static final int SIZEMODE_NxL = 4;
    /** User defined sample size mode. */
    static final int SIZEMODE_USERSIZE = 5;
    
    /** Set of descriptions of size modes. */
    public static final String[] SIZE_MODE_NAMES = {
        "Shannon-entropy Sum",
        "Average (0-1)Shannon-entropy x NxL",
        "Total number of characters (aligment length)",
        "Number of variable characters",
        "Alignment length x num taxa (NxL)",
        "Specified by the user"
    };
    /** Set of values of size modes. */
    public static final int[] SIZE_MODE_VALUES = {
        SIZEMODE_SHANNON,
        SIZEMODE_SHANNON_NxL,
        SIZEMODE_ALIGNMENT,
        SIZEMODE_ALIGNMENT_VAR,
        SIZEMODE_NxL,
        SIZEMODE_USERSIZE
    };
    // optimization strategies
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
    // Information criterion
    /** The value of AIC sorting. */
    static final char SORTBY_AIC = 'A';
    /** The value of BIC sorting. */
    static final char SORTBY_BIC = 'B';
    /** The value of AICc sorting. */
    static final char SORTBY_AICC = 'C';
    /** The value of DT sorting. */
    static final char SORTBY_DT = 'D';
    /** The value of likelihood sorting. */
    static final char SORTBY_LNL = 'E';
    /** Set of descriptions of sort modes. */
    static final String[] SORTBY_NAMES = {
        "AIC",
        "BIC",
        "AICc",
        "DT",
        "-lnL"
    };
    /** Set of values of sort modes. */
    static final char[] SORTBY_VALUES = {
        SORTBY_AIC,
        SORTBY_BIC,
        SORTBY_AICC,
        SORTBY_DT,
        SORTBY_LNL
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
    /** The default setting of property "Sort by". */
    static final char DEFAULT_SORT_BY = SORTBY_AIC;
    /** The default setting of property "Optimization strategy mode". */
    static final int DEFAULT_STRATEGY_MODE = OPTIMIZE_FIXED_BIONJ;
    /** The default setting of property "Sample size mode". */
    static final int DEFAULT_SAMPLE_SIZE_MODE = SIZEMODE_ALIGNMENT;
    // Tree properties
    /** Tree property for write branch length averaging */
    static final String PROP_BRANCH_LENGTH_AVERAGING = "branch_length_averaging";
}
