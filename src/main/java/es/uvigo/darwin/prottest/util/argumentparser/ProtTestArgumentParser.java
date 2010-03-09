package es.uvigo.darwin.prottest.util.argumentparser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;

/**
 * The Class ModelTestArgumentParser.
 */
public abstract class ProtTestArgumentParser {
	
	/** Data type parameter token. */
	public static final String PARAM_DATA_TYPE 					= "d";		// data type
	
	/** Nucleotide data type parameter value. */
	public static final String DATA_TYPE_NUCLEOTIDE				= "nt";
	
	/** Amino-acid data type parameter value. */
	public static final String DATA_TYPE_AMINOACID				= "aa";		// default
		
	/** Number of threads parameter token. */
	public static final String PARAM_NUM_THREADS				= "threads";
	
	/** Alignment file parameter token. */
	public static final String PARAM_ALIGNMENT_FILE 			= "i";		// alignment file (required)
	
	/** Output file parameter token. */
	public static final String PARAM_OUTPUT_FILE 				= "o";		// output file

	/** Tree file parameter token. */
	public static final String PARAM_TREE_FILE					= "t";		// tree file
	
	/** Sort by parameter token. */
	public static final String PARAM_SORT_BY					= "sort";	// sort field
	
	/** Display all framework comparison parameter token. */
	public static final String PARAM_ALL_FRAMEWORK_COMPARISON	= "all";	// display 7-framework comparison table
	
	/** Optimization strategy selection parameter token. */
	public static final String PARAM_OPTIMIZATION_STRATEGY		= "S";		// optimization strategy
	
	/** Sample size mode parameter token. */
	public static final String PARAM_SAMPLE_SIZE_MODE			= "sample";	// sample size for AICc and BIC corrections
	
	/** Specific sample size parameter token. */
	public static final String PARAM_SPECIFIC_SAMPLE_SIZE		= "size";	// specific sample size
	
	/** Display Newick tree parameter token. */
	public static final String PARAM_DISPLAY_NEWICK_TREE		= "t1";		// display newick tree
	
	/** Display ASCII tree parameter token. */
	public static final String PARAM_DISPLAY_ASCII_TREE			= "t2";		// display ASCII tree
	
	/** Display Consensus tree parameter token. */
	public static final String PARAM_DISPLAY_CONSENSUS_TREE		= "tc";		// display Consensus tree
	
	/** Include models with observed frequencies parameter token. */
	public static final String PARAM_PLUSF						= "F";
	
	/** Include models with a proportion invariable sites parameter token. */
	public static final String PARAM_PLUSI						= "I";
	
	/** Include models with rate variation among sites and number of categories parameter token. */
	public static final String PARAM_PLUSG						= "G";
	
	/** Include models with a proportion of invariable sites and rate variation parameter token. */
	public static final String PARAM_PLUSIG						= "IG";
	
	/** User defined number of categories parameter token. */
	public static final String PARAM_NCAT						= "ncat";
	
	/** Include models with all distributions (like -I -G). */
	public static final String PARAM_ALL_DISTRIBUTIONS			= "all-distributions";
	
	/** Verbose mode parameter token. */
	public static final String PARAM_VERBOSE					= "verbose";
	
	/** Hashtable to associate parameter tokens within parameter arguments requirements. */
	protected static Hashtable<String, Boolean> valuesRequired;
	
	/** Hashtable to associate parameter tokens within parameter arguments range. */
	protected static HashMap<String, String[]> argumentValues;
	
	/** Hashtable to handle special arguments like "all-distributions". */
	protected static HashMap<String, Map<String, String>> specialArguments;
		
	/** The default properties. */
	private static Properties defaultProperties;
	
	static {
	
		valuesRequired = new Hashtable<String, Boolean>();
		argumentValues = new HashMap<String, String[]>();
		specialArguments = new HashMap<String, Map<String, String>>();
		valuesRequired.put(PARAM_ALIGNMENT_FILE, true);
		valuesRequired.put(PARAM_OUTPUT_FILE, true);
		valuesRequired.put(PARAM_TREE_FILE, true);
		String[] sortByValues = String.valueOf(ApplicationGlobals.SORTBY_VALUES).split("");
		valuesRequired.put(PARAM_SORT_BY, true);
		argumentValues.put(PARAM_SORT_BY, sortByValues);
		String[] optimizationStrategies = new String[ApplicationGlobals.OPTIMIZE_VALUES.length];
		for (int index = 0; index < ApplicationGlobals.OPTIMIZE_VALUES.length; index++) {
			optimizationStrategies[index] = String.valueOf(ApplicationGlobals.OPTIMIZE_VALUES[index]);
		}
		valuesRequired.put(PARAM_OPTIMIZATION_STRATEGY, true);
		argumentValues.put(PARAM_OPTIMIZATION_STRATEGY, optimizationStrategies);
		String[] sampleSizeModes = {
				String.valueOf(ApplicationGlobals.SIZEMODE_SHANNON),
				String.valueOf(ApplicationGlobals.SIZEMODE_SHANNON_NxL),
				String.valueOf(ApplicationGlobals.SIZEMODE_ALIGNMENT),
				String.valueOf(ApplicationGlobals.SIZEMODE_ALIGNMENT_VAR),
				String.valueOf(ApplicationGlobals.SIZEMODE_NxL),
				String.valueOf(ApplicationGlobals.SIZEMODE_USERSIZE)   
		};
		valuesRequired.put(PARAM_NUM_THREADS, true);
		valuesRequired.put(PARAM_SAMPLE_SIZE_MODE, true);
		argumentValues.put(PARAM_SAMPLE_SIZE_MODE, sampleSizeModes);
		valuesRequired.put(PARAM_SPECIFIC_SAMPLE_SIZE, true);
		valuesRequired.put(PARAM_ALL_FRAMEWORK_COMPARISON, false);
		valuesRequired.put(PARAM_DISPLAY_NEWICK_TREE, false);
		valuesRequired.put(PARAM_DISPLAY_ASCII_TREE, false);
		valuesRequired.put(PARAM_DISPLAY_CONSENSUS_TREE, true);
		valuesRequired.put(PARAM_PLUSF, false);
		valuesRequired.put(PARAM_PLUSI, false);
		valuesRequired.put(PARAM_PLUSG, false);
		valuesRequired.put(PARAM_PLUSIG, false);
		valuesRequired.put(PARAM_NCAT, true);
		valuesRequired.put(PARAM_ALL_DISTRIBUTIONS, false);
		valuesRequired.put(PARAM_VERBOSE, false);
		Map<String,String> distributionsMap = new HashMap<String,String>(3);
		distributionsMap.put(PARAM_PLUSG, "T");
		distributionsMap.put(PARAM_PLUSI, "T");
		distributionsMap.put(PARAM_PLUSIG, "T");
		specialArguments.put(PARAM_ALL_DISTRIBUTIONS, distributionsMap);
		
		defaultProperties = new Properties();
		
		defaultProperties.setProperty(PARAM_NUM_THREADS, String.valueOf(ApplicationGlobals.DEFAULT_THREADS));
		defaultProperties.setProperty(PARAM_NCAT, String.valueOf(ApplicationGlobals.DEFAULT_NCAT));
		defaultProperties.setProperty(PARAM_SORT_BY, String.valueOf(ApplicationGlobals.DEFAULT_SORT_BY));
		defaultProperties.setProperty(PARAM_OPTIMIZATION_STRATEGY, String.valueOf(ApplicationGlobals.DEFAULT_STRATEGY_MODE));
		defaultProperties.setProperty(PARAM_SAMPLE_SIZE_MODE, String.valueOf(ApplicationGlobals.DEFAULT_SAMPLE_SIZE_MODE));
		defaultProperties.setProperty(PARAM_DATA_TYPE, DATA_TYPE_AMINOACID);
}
	
	/** The argument  prefix. */
	public static final String ARG_TOKEN = "-";
	
	/** The command line arguments. */
	private Properties arguments;
	
	/**
	 * Instantiates a new model test argument parser.
	 * 
	 * @param args the command line arguments
	 * @param options the application options
	 * 
	 * @throws IllegalArgumentException when exists some error in the command line argument
	 */
	public ProtTestArgumentParser(String[] args, ApplicationOptions options) 
		throws IllegalArgumentException {
		
		arguments = checkArgs(args);
		options.fillIn(this);
	}
	
	/**
	 * Check arguments are grammatically correct and parse them into
	 * the instance.
	 * 
	 * @param args the command line arguments
	 * 
	 * @return the argument properties
	 * 
	 * @throws IllegalArgumentException when exists some error in the command line argument
	 */
	private Properties checkArgs(String[] args) 
		throws IllegalArgumentException {

		Properties arguments = new Properties(defaultProperties);
		int i = 0;
		while (i < args.length) {

			String arg = args[i];
			if(!arg.startsWith(ARG_TOKEN)) {
				throw new IllegalArgumentException("Arguments must start with \"-\". The ofending argument was: " + arg);
			}
			
			arg = arg.substring(ARG_TOKEN.length());
			if (valuesRequired.containsKey(arg)) {
				if (valuesRequired.get(arg)) {
					if (i+1 < args.length) {
						i++;
						String value = args[i];
						if (argumentValues.containsKey(arg)) {
							String[] values = argumentValues.get(arg);
							if (!Arrays.asList(values).contains(value))
								throw new IllegalArgumentException("Invalid argument value " + value + " for parameter " + arg);
						}
						
						// special cases
						if (specialArguments.containsKey(arg)) {
							Map<String, String> mapValues = specialArguments.get(arg);
							for (String key : mapValues.keySet())
								if (mapValues.get(key).equals("?"))
									mapValues.put(key, value);
							putArgument(mapValues, arguments);
						} else
							putArgument(arg, value, arguments);
					} else {
						IllegalArgumentException e = new IllegalArgumentException("Parameter "+ arg +" requires a value"); 
						throw e;
					}
				} else {
					if (specialArguments.containsKey(arg)) {
						Map<String, String> mapValues = specialArguments.get(arg);
						putArgument(mapValues, arguments);
					} else
						putArgument(arg, "T", arguments);	
				}
			} else {
				throw new IllegalArgumentException("Invalid argument " + arg);
			}
			i++;
		}
		return arguments;
		
	}
	
	/**
	 * Checks if a concrete argument exists in the application.
	 * 
	 * @param arg the argument to check
	 * 
	 * @return true, if the argument has a value
	 */
	public boolean exists(String arg) {
		return arguments.containsKey(arg);
	}
	
	/**
	 * Checks if a boolean argument is set in the application.
	 * The method will return false if the boolean argument
	 * is set to 'false', the argument is not boolean or the
	 * argument does not exist in the argument list.
	 * 
	 * @param arg the argument to check
	 * 
	 * @return true, if the boolean argument is set to 'true'
	 */
	public boolean isSet(String arg) {
		boolean isSet;
		try {
			isSet = arguments.get(arg).equals("T");
		} catch (NullPointerException npe) {
			isSet = false;
		}
		return isSet;
	}
	
	/**
	 * Gets the value of an argument. If the argument key is not found,
	 * the default arguments will be checked. If the argument key does
	 * not exist, the method will return null.
	 * 
	 * @param arg the argument to check
	 * 
	 * @return the argument value
	 */
	public String getValue(String arg) {
		return arguments.getProperty(arg);
	}
	
	/**
	 * Puts an argument into the argument list, with specified key and value.
	 * If the argument was already set to a certain value, the argument will
	 * take the new value, and a warning message will be printed into
	 * standard error output.
	 * 
	 * @param key the argument key
	 * @param value the argument value
	 * @param arguments the argument list
	 */
	private void putArgument(String key, String value, Properties arguments){
		if (arguments.containsKey(key))
			System.err.println("WARNING! Repeated argument \"" + key + "\". New value is " + value);
		arguments.setProperty(key, value);
	}
	
	/**
	 * Puts a set of couples argument-value into the argument list.
	 * For each argument that was already set to a certain value, the 
	 * argument will take the new value, and a warning message will be 
	 * printed into standard error output.
	 * 
	 * @param items the new arguments to put
	 * @param arguments the argument list
	 */
	private void putArgument(Map<String, String> items, Properties arguments){
		// arguments.putAll(items);
		for (String key : items.keySet())
			putArgument(key, items.get(key), arguments);
	}
	
	/**
	 * Gets the matrix name list of all supported matrices.
	 * 
	 * @return the matrix name list
	 */
	public abstract List<String> getMatrices();

}
