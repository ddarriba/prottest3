package es.uvigo.darwin.prottest.global;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The global application settings and parameter names.
 */
public abstract class ApplicationGlobals {

    /** The exit status for signals caused by the non existence of properties file. */
    public static final int EXIT_NO_PROPERTIES = 101;
    // analyzers
    /** The Constant ANALYZER_PHYML. It is used to check the analyzer in the properties file */
    public static final String ANALYZER_PHYML = "phyml";
    /** The Constant ANALYZER_RAXML. It is used to check the analyzer in the properties file */
    public static final String ANALYZER_RAXML = "raxml";
    public static final String PROP_BRANCH_LENGTH_AVERAGING = "branch_length_averaging";
    // not including gaps (20 amino-acids)
    /** The Constant NUM_STATES. */
    public static final int NUM_STATES = 20;
    /** The application properties. */
    public static Properties properties;
    // frequencies distribution
    /** The value of Uniform Frequencies Distribution. */
    public static final int FREQ_DISTRIBUTION_UNIFORM = 1;
    /** The value of Empirical Frequencies Distribution. */
    public static final int FREQ_DISTRIBUTION_EMPIRICAL = 2;
    /** The value of Maximum Likelihood Frequencies Distribution. */
    public static final int FREQ_DISTRIBUTION_MAXIMUM_LIKELIHOOD = 3;
    /** The value of any other frequencies distribution. */
    public static final int FREQ_DISTRIBUTION_OTHER = 4;
    // size modes
    /** The Constant SIZEMODE_SHANNON. */
    public static final int SIZEMODE_SHANNON = 0;
    /** The Constant SIZEMODE_SHANNON_NxL. */
    public static final int SIZEMODE_SHANNON_NxL = 1;
    /** The Constant SIZEMODE_ALIGNMENT. */
    public static final int SIZEMODE_ALIGNMENT = 2;
    /** The Constant SIZEMODE_ALIGNMENT_VAR. */
    public static final int SIZEMODE_ALIGNMENT_VAR = 3;
    /** The Constant SIZEMODE_NxL. */
    public static final int SIZEMODE_NxL = 4;
    /** The Constant SIZEMODE_USERSIZE. */
    public static final int SIZEMODE_USERSIZE = 5;
    /** Set of values of size modes. */
    public static final int[] SIZE_MODE_VALUES = {
        SIZEMODE_SHANNON,
        SIZEMODE_SHANNON_NxL,
        SIZEMODE_ALIGNMENT,
        SIZEMODE_ALIGNMENT_VAR,
        SIZEMODE_NxL,
        SIZEMODE_USERSIZE
    };
    /** Set of descriptions of size modes. */
    public static final String[] SIZE_MODES = {
        "Shannon-entropy Sum",
        "Average (0-1)Shannon-entropy x NxL",
        "Total number of characters (aligment length)",
        "Number of variable characters",
        "Alignment length x num taxa (NxL)",
        "Specified by the user"
    };
    // optimization strategies
    /** The Constant OPTIMIZE_FIXED_BIONJ. */
    public static final int OPTIMIZE_FIXED_BIONJ = 0;
    /** The Constant OPTIMIZE_BIONJ. */
    public static final int OPTIMIZE_BIONJ = 1;
    /** The Constant OPTIMIZE_ML. */
    public static final int OPTIMIZE_ML = 2;
    /** The Constant OPTIMIZE_USER. */
    public static final int OPTIMIZE_USER = 3;
    /** Set of descriptions of the optimization strategies. */
    public static final String[] STRATEGIES = {
        "Fixed BIONJ JTT",
        "BIONJ Tree",
        "Maximum Likelihood tree",
        "User defined topology"
    };
    /** Set of values of the optimization strategies. */
    public static final int[] OPTIMIZE_VALUES = {
        OPTIMIZE_FIXED_BIONJ,
        OPTIMIZE_BIONJ,
        OPTIMIZE_ML,
        OPTIMIZE_USER
    };
    // sort modes
    /** The value of AIC sorting. */
    public static final char SORTBY_AIC = 'A';
    /** The value of BIC sorting. */
    public static final char SORTBY_BIC = 'B';
    /** The value of AICc sorting. */
    public static final char SORTBY_AICC = 'C';
    /** The value of DT sorting. */
    public static final char SORTBY_DT = 'D';
    /** The value of likelihood sorting. */
    public static final char SORTBY_LNL = 'E';
    /** Set of values of sort modes. */
    public static final char[] SORTBY_VALUES = {
        SORTBY_AIC,
        SORTBY_BIC,
        SORTBY_AICC,
        SORTBY_DT,
        SORTBY_LNL
    };
    /** Set of descriptions of sort modes. */
    public static final String[] SORTBY_NAMES = {
        "AIC",
        "BIC",
        "AICc",
        "DT",
        "-lnL"
    };
    // default settings
    /** The default setting of property "Display Newick tree". */
    public static final boolean DEFAULT_DISPLAY_NEWICK_TREE = false;
    /** The default setting of property "Number of threads". */
    public static final int DEFAULT_THREADS = 1;
    /** The default setting of property "Display ASCII tree". */
    public static final boolean DEFAULT_DISPLAY_ASCII_TREE = false;
    /** The default setting of property "Display Consensus tree". */
    public static final boolean DEFAULT_DISPLAY_CONSENSUS_TREE = false;
    /** The default setting of property "Debug" (Verbose mode). */
    public static final boolean DEFAULT_DEBUG = false;
    /** The default setting of property "Display a 7-framework comparison". */
    public static final boolean DEFAULT_COMPARE_ALL = false;
    /** The default setting of property "Compute all matrices". */
    public static final boolean DEFAULT_MATRICES = false;
    /** The default setting of property "Compute all distributions". */
    public static final boolean DEFAULT_DISTRIBUTIONS = false;
    /** The default setting of property "Number of categories". */
    public static final int DEFAULT_NCAT = 4;
    /** The default setting of property "Sort by". */
    public static final char DEFAULT_SORT_BY = 'A';
    /** The default setting of property "Optimization strategy mode". */
    public static final int DEFAULT_STRATEGY_MODE = ApplicationGlobals.OPTIMIZE_FIXED_BIONJ;
    /** The default setting of property "Sample size mode". */
    public static final int DEFAULT_SAMPLE_SIZE_MODE = ApplicationGlobals.SIZEMODE_ALIGNMENT;

    static {
        properties = new Properties();
        try {
            FileInputStream prop = new FileInputStream("prottest.properties");
            properties.load(prop);
        } catch (IOException e) {
            System.err.println("Properties file (prottest.properties) cannot be resolved");
            System.exit(EXIT_NO_PROPERTIES);
        }

    }
    public static final String DEFAULT_SNAPSHOT_DIR = "snapshot/";

    /**
     * Gets the supported matrices.
     * 
     * @return the supported matrices
     */
    public abstract List<String> getSupportedMatrices();

    /**
     * Gets the model name from matrix and distribution names. This
     * method is useful specially when a custom matrix is set, so
     * his internal name is not representative for the user.
     * 
     * @param matrix the matrix name
     * @param frequenciesDistribution the frequencies distribution name
     * 
     * @return the model name
     */
    public abstract String getModelName(String matrix, int frequenciesDistribution);
}
