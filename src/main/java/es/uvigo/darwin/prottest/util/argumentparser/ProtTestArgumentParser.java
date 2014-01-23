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

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import es.uvigo.darwin.prottest.global.ProtTestConsoleParameters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;

/**
 * The Class ModelTestArgumentParser.
 */
public abstract class ProtTestArgumentParser 
        implements ProtTestConsoleParameters {

    
    /** Hashtable to associate parameter tokens within parameter localArguments requirements. */
    protected static final HashMap<String, Boolean> valuesRequired;
    /** Hashtable to associate parameter tokens within parameter localArguments range. */
    protected static final HashMap<String, String[]> argumentValues;
    /** Hashtable to handle special localArguments like "all-distributions". */
    protected static final HashMap<String, Map<String, String>> specialArguments;
    /** The default APPLICATION_PROPERTIES. */
    private static final Properties defaultProperties;
    /** The argument  prefix. */
    public static final String ARG_TOKEN = "-";
    /** The command line localArguments. */
    private Properties arguments;
    
    static {

        valuesRequired = new HashMap<String, Boolean>();
        argumentValues = new HashMap<String, String[]>();
        specialArguments = new HashMap<String, Map<String, String>>();
        valuesRequired.put(PARAM_ALIGNMENT_FILE, true);
        valuesRequired.put(PARAM_OUTPUT_FILE, true);
        valuesRequired.put(PARAM_TREE_FILE, true);
        String[] optimizationStrategies = new String[OPTIMIZE_VALUES.length];
        for (int index = 0; index < OPTIMIZE_VALUES.length; index++) {
            optimizationStrategies[index] = String.valueOf(OPTIMIZE_VALUES[index]);
        }
        valuesRequired.put(PARAM_OPTIMIZATION_STRATEGY, true);
        argumentValues.put(PARAM_OPTIMIZATION_STRATEGY, optimizationStrategies);
        
        String[] treeSearchOps = {
        		TREE_SEARCH_NNI,
        		TREE_SEARCH_SPR,
        		TREE_SEARCH_BEST
        };
        String[] enabling = {
        		"enabled",
        		"disabled"
        };
        
        valuesRequired.put(PARAM_TREE_SEARCH_OP, true);
        argumentValues.put(PARAM_TREE_SEARCH_OP, treeSearchOps);
        
        valuesRequired.put(PARAM_NUM_THREADS, true);
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
        valuesRequired.put(PARAM_DO_AIC, false);
        valuesRequired.put(PARAM_DO_BIC, false);
        valuesRequired.put(PARAM_DO_AICC, false);
        valuesRequired.put(PARAM_DO_DT, false);
        valuesRequired.put(PARAM_LOGGING, true);
        argumentValues.put(PARAM_LOGGING, enabling);
        Map<String, String> distributionsMap = new HashMap<String, String>(3);
        distributionsMap.put(PARAM_PLUSG, "T");
        distributionsMap.put(PARAM_PLUSI, "T");
        distributionsMap.put(PARAM_PLUSIG, "T");
        specialArguments.put(PARAM_ALL_DISTRIBUTIONS, distributionsMap);

        defaultProperties = new Properties();

        defaultProperties.setProperty(PARAM_NUM_THREADS, String.valueOf(DEFAULT_THREADS));
        defaultProperties.setProperty(PARAM_NCAT, String.valueOf(DEFAULT_NCAT));
        defaultProperties.setProperty(PARAM_OPTIMIZATION_STRATEGY, String.valueOf(DEFAULT_STRATEGY_MODE));
        defaultProperties.setProperty(PARAM_DATA_TYPE, DATA_TYPE_AMINOACID);
    }

    /**
     * Instantiates a new model test argument parser.
     * 
     * @param args the command line localArguments
     * @param options the application options
     * 
     * @throws IllegalArgumentException when exists some error in the command line argument
     */
    public ProtTestArgumentParser(String[] args, ApplicationOptions options)
            throws IllegalArgumentException {

        arguments = checkArgs(args);
        
        if (!(exists(PARAM_DO_BIC) || exists(PARAM_DO_AIC)
        		|| exists(PARAM_DO_AICC) || exists(PARAM_DO_DT))) {
        	putArgument(PARAM_DO_BIC, "T", arguments);
        }
        
        options.fillIn(this);
    }

    /**
     * Check localArguments are grammatically correct and parse them into
     * the instance.
     * 
     * @param args the command line localArguments
     * 
     * @return the argument APPLICATION_PROPERTIES
     * 
     * @throws IllegalArgumentException when exists some error in the command line argument
     */
    protected Properties checkArgs(String[] args)
            throws IllegalArgumentException {

        Properties localArguments = new Properties(defaultProperties);
        int i = 0;
        while (i < args.length) {

            String arg = args[i];
            if (!arg.startsWith(ARG_TOKEN)) {
                System.err.print("Command line: ");
                for (String simpleArg : args) {
                    System.err.print(simpleArg + " ");
                }
                System.err.println(" ");
                throw new IllegalArgumentException("Arguments must start with \"-\". The ofending argument was: " + arg);
            }

            arg = arg.substring(ARG_TOKEN.length());
            if (valuesRequired.containsKey(arg)) {
                if (valuesRequired.get(arg)) {
                    if (i + 1 < args.length) {
                        i++;
                        String value = args[i];
                        if (argumentValues.containsKey(arg)) {
                            String[] values = argumentValues.get(arg);
                            if (!Arrays.asList(values).contains(value.toLowerCase())) {
                                throw new IllegalArgumentException("Invalid argument value " + value + " for parameter " + arg);
                            }
                        }

                        // special cases
                        if (specialArguments.containsKey(arg)) {
                            Map<String, String> mapValues = specialArguments.get(arg);
                            for (String key : mapValues.keySet()) {
                                if (mapValues.get(key).equals("?")) {
                                    mapValues.put(key, value);
                                }
                            }
                            putArgument(mapValues, localArguments);
                        } else {
                            putArgument(arg, value, localArguments);
                        }
                    } else {
                        IllegalArgumentException e = new IllegalArgumentException("Parameter " + arg + " requires a value");
                        throw e;
                    }
                } else {
                    if (specialArguments.containsKey(arg)) {
                        Map<String, String> mapValues = specialArguments.get(arg);
                        putArgument(mapValues, localArguments);
                    } else {
                        putArgument(arg, "T", localArguments);
                    }
                }
            } else {
                // Obsolete arguments checking...
                if (arg.equals("-sort")) {
                    System.err.println(" WARNING! \"sort\" argument is obsolete since 3.2 version. You should use one or more of the following instead: -AIC -AICC -BIC -DT");
                }
                throw new IllegalArgumentException("Invalid argument " + arg);
            }
            i++;
        }
        
        return localArguments;

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
     * the default localArguments will be checked. If the argument key does
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
     * @param localArguments the argument list
     */
    private void putArgument(String key, String value, Properties arguments) {
        if (arguments.containsKey(key)) {
            System.err.println("WARNING! Repeated argument \"" + key + "\". New value is " + value);
        }
        arguments.setProperty(key, value);
    }

    /**
     * Puts a set of couples argument-value into the argument list.
     * For each argument that was already set to a certain value, the 
     * argument will take the new value, and a warning message will be 
     * printed into standard error output.
     * 
     * @param items the new localArguments to put
     * @param localArguments the argument list
     */
    private void putArgument(Map<String, String> items, Properties arguments) {
        // localArguments.putAll(items);
        for (String key : items.keySet()) {
            putArgument(key, items.get(key), arguments);
        }
    }

    /**
     * Gets the matrix name list of all supported matrices.
     * 
     * @return the matrix name list
     */
    public abstract List<String> getMatrices();
}
