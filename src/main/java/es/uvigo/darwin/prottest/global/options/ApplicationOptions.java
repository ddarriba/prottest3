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
package es.uvigo.darwin.prottest.global.options;

import static es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser;
import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.fileio.AlignmentReader;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import static es.uvigo.darwin.prottest.util.logging.ProtTestLogger.*;

/**
 * The ProtTest application options models the common global options for the
 * application execution. It is a singleton class which gathers all
 * necessary options to run.
 */
public class ApplicationOptions {

    /** Verbose mode on/off. */
    protected boolean debug = DEFAULT_DEBUG;
    /** The sample size mode. */
    protected int sampleSizeMode = DEFAULT_SAMPLE_SIZE_MODE;
    /** The sample size. It is only useful if the sample size mode is custom */
    protected double sampleSize = 0.0;
    /** The alignment filename. */
    protected String align_file = null;
    /** The tree filename. */
    protected String tree_file = null;
    /** The log filename. */
    protected String log_file = null;
    /** The alignment, according to the alignment input file. */
    protected Alignment alignment;
    /** The tree, according to the tree filename. */
    protected Tree tree;
    /** The number of categories. It is only useful if the distribution is gamma */
    public int ncat = 4;
    /** The optimization strategy mode. */
    public int strategyMode = DEFAULT_STRATEGY_MODE;
    /** The matrices of the models to optimize. */
    private List<String> matrices = new ArrayList<String>();	//{"JTT", "MtREV", "Dayhoff", "WAG"};
    /** The distributions of the models to optimize. */
    private List<Integer> distributions = new ArrayList<Integer>();	//{0,1,2,3};
    /** Hash table with the relationship between distribution name and distribution internal parameter. */
    private HashMap<String, Integer> distributionsHash;
    /** Boolean value to consider or not different kind of amino-acid frequencies. */
    private boolean plusF = false;
    // display options
    /** Display Newick tree on results. */
    private boolean displayNewickTree = DEFAULT_DISPLAY_NEWICK_TREE;
    /** Display ASCII tree on results. */
    private boolean displayASCIITree = DEFAULT_DISPLAY_ASCII_TREE;
    /** Display Consensus tree on results. */
    private boolean displayConsensusTree = DEFAULT_DISPLAY_CONSENSUS_TREE;
    /** Consensus threshold **/
    private double consensusThreshold;
    /** Display a 7-framework comparison. */
    private boolean compareAll = DEFAULT_COMPARE_ALL;
    /** Write a log file. */
    public boolean writeLog = true;
    /** Criterion to sort the models. */
    private boolean doAIC,doBIC,doAICc,doDT;
    /** Tree search operation */
    private String treeSearchOperation = TREE_SEARCH_NNI;

    /**
     * Sets the number of categories.
     * 
     * @param ncat the new number of categories
     */
    public void setNumberOfCategories(int ncat) {
        this.ncat = ncat;
    }

    /**
     * Gets the value of plusF attribute.
     * 
     * @return the plusF value
     */
    public boolean isPlusF() {
        return plusF;
    }

    /**
     * Sets the empirical amino-acid frequencies usage.
     * 
     * @param plusF the new value for consider empirical frequencies
     */
    public void setPlusF(boolean plusF) {
        this.plusF = plusF;
    }

    /**
     * Checks if is required to display newick tree.
     * 
     * @return true, if is required to display newick tree
     */
    public boolean isDisplayNewickTree() {
        return displayNewickTree;
    }

    /**
     * Checks if is required to display ascii tree.
     * 
     * @return true, if is required to display ascii tree
     */
    public boolean isDisplayASCIITree() {
        return displayASCIITree;
    }

    /**
     * Checks if is required to display consensus tree.
     * 
     * @return true, if is required to display consensus tree
     */
    public boolean isDisplayConsensusTree() {
        return displayConsensusTree;
    }

    /**
     * Gets the threshold to create consensus tree.
     * 
     * @return the threshold
     */
    public double getConsensusThreshold() {
        return consensusThreshold;
    }

    /**
     * Check if should compute the AIC criterion
     * 
     * @return true, if AIC should be computed
     */
    public boolean isAIC() {
        return doAIC;
    }
    
    /**
     * Check if should compute the BIC criterion
     * 
     * @return true, if BIC should be computed
     */
    public boolean isBIC() {
        return doBIC;
    }
    
    /**
     * Check if should compute the AICc criterion
     * 
     * @return true, if AICc should be computed
     */
    public boolean isAICc() {
        return doAICc;
    }
    
    /**
     * Check if should compute the DT criterion
     * 
     * @return true, if DT should be computed
     */
    public boolean isDT() {
        return doDT;
    }
    public String getTreeSearchOperation() {
		return treeSearchOperation;
	}

	public void setTreeSearchOperation(String treeSearchOperation) {
		this.treeSearchOperation = treeSearchOperation;
	}

	/**
     * Sets the alignment filename without checking
     * 
     * @param alignFile the alignment filename
     */
    public void setAlignmentFilename(String alignFile) {
        this.align_file = alignFile;
    }

    /**
     * Sets the alignment file
     * 
     * @param alignFile the alignment filename
     * 
     * @return true, if successful
     * 
     * @throws AlignmentParseException Signals that the alignment filename is not correct.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean setAlignment(String alignFile)
            throws AlignmentParseException,
            IOException {

        this.align_file = alignFile;

        StringWriter sw = new StringWriter();
        setAlignment(AlignmentReader.readAlignment(new PrintWriter(sw), alignFile, debug));
        sw.flush();
        println(sw.toString());

        return alignFile != null;
    }

    /**
     * Gets the alignment.
     * 
     * @return the alignment
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Gets the tree.
     * 
     * @return the tree
     */
    public Tree getTree() {
        return tree;
    }

    /**
     * Sets the alignment.
     * 
     * @param alignment the new alignment
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Sets the tree.
     * 
     * @param tree the new tree
     */
    public synchronized void setTree(Tree tree) {
        this.tree = tree;
    }

    /**
     * Checks if is debug (verbose mode).
     * 
     * @return true, if is debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug mode value.
     * 
     * @param debug the new debug mode value
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Checks if a 7-framework comparison should be displayed.
     * 
     * @return true, if a 7-framework comparison should be displayed
     */
    public boolean isAll() {
        return compareAll;
    }

    /**
     * Sets the 7-framework comparison display value.
     * 
     * @param all the new 7-framework comparison display value
     */
    public void setAll(boolean all) {
        this.compareAll = all;
    }

    /**
     * Gets the tree filename.
     * 
     * @return the tree filename
     */
    public String getTreeFile() {
        return tree_file;
    }

    /**
     * Sets the tree filename.
     * 
     * @param treeFile the new tree filename
     * 
     * @throws TreeFormatException Signals there was an error parsing the tree file
     */
    public void setTreeFile(String treeFile)
            throws TreeFormatException, FileNotFoundException, IOException {
        if (treeFile != null) {
            this.tree_file = treeFile;

            StringWriter sw = new StringWriter();
            setTree(AlignmentReader.readTree(new PrintWriter(sw), treeFile, debug));
            sw.flush();
            println(sw.toString());
        }
    }

    /**
     * Gets the number of model matrices.
     * 
     * @return the number of model matrices
     */
    public int getNumberOfMatrices() {
        return matrices.size();
    }

    /**
     * Gets the model matrices.
     * 
     * @return the model matrices
     */
    @SuppressWarnings("unchecked")
    public List<String> getMatrices() {
        List<String> matricesClone = new ArrayList<String>(matrices);
        return matricesClone;
    }

    /**
     * Sets the model matrices.
     * 
     * @param newMatrices the new model matrices
     */
    public void setMatrices(List<String> newMatrices) {
        matrices = newMatrices;
    }

    /**
     * Gets the matrix at a certain position.
     * 
     * @param pos the position of the matrix
     * 
     * @return the matrix at the required position
     */
    public String getMatrixAt(int pos) {
        return matrices.get(pos);
    }

    /**
     * Check if one matrix exists in the model matrices set of the application.
     * 
     * @param matrix the matrix to check out
     * 
     * @return true, if the set of matrices contains the model matrix
     */
    public boolean existsMatrix(String matrix) {
        return matrices.contains(matrix);
    }

    /**
     * Removes one matrix of the set.
     * 
     * @param matrix the matrix to remove
     * 
     * @return true, if the matrix was successfully removed
     */
    public boolean removeMatrix(String matrix) {
        return matrices.remove(matrix);
    }

    /**
     * Adds one matrix to the set.
     * 
     * @param matrix the matrix to add
     */
    public void addMatrix(String matrix) {
        if (!matrices.contains(matrix)) {
            matrices.add(matrix);
        }
    }

    /**
     * Gets the distributions.
     * 
     * @return the distributions
     */
    public List<Integer> getDistributions() {
        List<Integer> distributionsClone = new ArrayList<Integer>(distributions);
        return distributionsClone;
    }

    /**
     * Sets the distributions.
     * 
     * @param newDistributions the new distributions
     */
    public void setDistributions(List<Integer> newDistributions) {
        distributions = newDistributions;
    }

    /**
     * Gets the number of distributions.
     * 
     * @return the number of distributions
     */
    public int getNumberOfDistributions() {
        return distributions.size();
    }

    /**
     * Gets the distribution at a certain position of the list.
     * 
     * @param pos the position of the distribution to get
     * 
     * @return the distribution at the required position
     */
    public int getDistributionAt(int pos) {
        return distributions.get(pos);
    }

    /**
     * Gets the internal value of a distribution.
     * 
     * @param distribution the distribution name
     * 
     * @return the distribution value
     */
    public int getDistribution(String distribution) {
        return distributionsHash.get(distribution);
    }

    /**
     * Check if exist one distribution in the distribution set.
     * 
     * @param distribution the distribution to check out
     * 
     * @return true, if the set of distribution contains the distribution
     */
    public boolean existsDistribution(String distribution) {
        return distributionsHash.containsKey(distribution);
    }

    /**
     * Adds one distribution to the distributions set.
     * 
     * @param distribution the distribution to add
     */
    public void addDistribution(String distribution) {
        Integer distributionValue = distributionsHash.get(distribution);
        if (!distributions.contains(distributionValue)) {
            distributions.add(distributionValue);
        }
    }

    /**
     * Removes the distribution from the distributions set.
     * 
     * @param distribution the distribution to remove
     * 
     * @return true, if the distribution was successfully removed
     */
    public boolean removeDistribution(String distribution) {
        Integer distributionValue = distributionsHash.get(distribution);
        return distributions.remove(distributionValue);
    }

    /**
     * Gets the sample size mode.
     * 
     * @return the sample size mode
     */
    public int getSampleSizeMode() {
        return sampleSizeMode;
    }

    /**
     * Gets the sample size.
     * 
     * @return the sample size
     */
    public double getSampleSize() {
        return sampleSize;
    }

    /**
     * Sets the sample size mode.
     * 
     * @param sampleSizeMode the new sample size mode
     */
    public void setSampleSizeMode(int sampleSizeMode) {
        this.sampleSizeMode = sampleSizeMode;
    }

    /**
     * Sets the sample size.
     * 
     * @param sampleSize the new sample size
     */
    public void setSampleSize(double sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * Sets the strategy mode.
     * 
     * @param strategyMode the new strategy mode
     */
    public void setStrategyMode(int strategyMode) {
        if (strategyMode == OPTIMIZE_USER && getTreeFile() == null) {
            throw new ProtTestInternalException("User defined topology must be set");
        }
        this.strategyMode = strategyMode;
    }

    /**
     * Instantiates a new application options.
     */
    public ApplicationOptions() {
        initLists();
    }

    /**
     * Fill in attribute values with the arguments.
     * 
     * @param arguments the arguments too fill the application options instance in
     */
    public void fillIn(ProtTestArgumentParser arguments) {

        if (arguments.exists(ProtTestArgumentParser.PARAM_ALIGNMENT_FILE)) {
            try {
                setAlignment(arguments.getValue(ProtTestArgumentParser.PARAM_ALIGNMENT_FILE));
            } catch (AlignmentParseException e) {
                throw new IllegalArgumentException(e.getMessage());
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Required input file argument -i");
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_TREE_FILE)) {
            try {
                setTreeFile(arguments.getValue(ProtTestArgumentParser.PARAM_TREE_FILE));
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_OUTPUT_FILE)) {
            //open the PrintWriter with file
            try {
                FileOutputStream fo = new FileOutputStream(arguments.getValue(ProtTestArgumentParser.PARAM_OUTPUT_FILE));
                ProtTestLogger.getDefaultLogger().addHandler(fo, Level.INFO);
            } catch (FileNotFoundException fnfe) {
                throw new ProtTestInternalException(fnfe.getMessage());
            }
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_TREE_SEARCH_OP)) {
            setTreeSearchOperation(arguments.getValue(ProtTestArgumentParser.PARAM_TREE_SEARCH_OP));
        }
        setDebug(arguments.isSet(ProtTestArgumentParser.PARAM_VERBOSE));
        displayASCIITree = arguments.isSet(ProtTestArgumentParser.PARAM_DISPLAY_ASCII_TREE);
        displayNewickTree = arguments.isSet(ProtTestArgumentParser.PARAM_DISPLAY_NEWICK_TREE);
        displayConsensusTree = arguments.isSet(ProtTestArgumentParser.PARAM_DISPLAY_CONSENSUS_TREE);
        if (arguments.exists(ProtTestArgumentParser.PARAM_DISPLAY_CONSENSUS_TREE)) {
            displayConsensusTree = true;
            try {
                consensusThreshold = Double.parseDouble(arguments.getValue(ProtTestArgumentParser.PARAM_DISPLAY_CONSENSUS_TREE));
            } catch  (NumberFormatException e) {
                throw new IllegalArgumentException("Consensus threshold must be a number between 0.5 (majority rule) and 1 (strict). You used " + arguments.getValue(ProtTestArgumentParser.PARAM_DISPLAY_CONSENSUS_TREE));
            }
            if (consensusThreshold < 0.5 || consensusThreshold > 1.0) {
                throw new IllegalArgumentException("Consensus threshold must be a number between 0.5 (majority rule) and 1 (strict). You used " + consensusThreshold);
            }
        }
        setAll(arguments.isSet(ProtTestArgumentParser.PARAM_ALL_FRAMEWORK_COMPARISON));
        if (arguments.exists(ProtTestArgumentParser.PARAM_OPTIMIZATION_STRATEGY)) {
            if (getTreeFile() != null) {
                strategyMode = OPTIMIZE_USER;
            } else {
                strategyMode = Integer.parseInt(arguments.getValue(ProtTestArgumentParser.PARAM_OPTIMIZATION_STRATEGY));
                // Check
                if (strategyMode == OPTIMIZE_USER &&
                        getTreeFile() == null) {
                    throw new IllegalArgumentException("User defined topology must be set");
                }
            }
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_DO_AIC)) {
            doAIC = arguments.isSet(ProtTestArgumentParser.PARAM_DO_AIC);
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_DO_BIC)) {
            doBIC = arguments.isSet(ProtTestArgumentParser.PARAM_DO_BIC);
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_DO_AICC)) {
            doAICc = arguments.isSet(ProtTestArgumentParser.PARAM_DO_AICC);
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_DO_DT)) {
            doDT = arguments.isSet(ProtTestArgumentParser.PARAM_DO_DT);
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_SAMPLE_SIZE_MODE)) {
            sampleSizeMode = Integer.parseInt(arguments.getValue(ProtTestArgumentParser.PARAM_SAMPLE_SIZE_MODE));
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_SPECIFIC_SAMPLE_SIZE)) {
            sampleSize = Double.parseDouble(arguments.getValue(ProtTestArgumentParser.PARAM_SPECIFIC_SAMPLE_SIZE));
        }
        setSampleSize(ProtTestAlignment.calculateSampleSize(alignment, sampleSizeMode, sampleSize));

        boolean existsMatrix = false;
        for (String matrix : arguments.getMatrices()) {
            if (arguments.isSet(matrix)) {
                addMatrix(matrix);
                existsMatrix = true;
            } else {
                removeMatrix(matrix);
            }
        }
        if (!existsMatrix) {
            throw new IllegalArgumentException("You must specify at least one model matrix");
        }

        // distributions
        addDistribution("Uniform");
        boolean plusG = false, plusIG = false;
        if (arguments.isSet(ProtTestArgumentParser.PARAM_PLUSI)) {
            addDistribution("+I");
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_PLUSG)) {
            addDistribution("+G");
            plusG = true;
        }
        if (arguments.exists(ProtTestArgumentParser.PARAM_PLUSIG)) {
            addDistribution("+I+G");
            plusIG = true;
        }
        if (plusG || plusIG) {
            try {
                setNumberOfCategories(Integer.parseInt(arguments.getValue(ProtTestArgumentParser.PARAM_NCAT)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number of categories " + arguments.getValue(ProtTestArgumentParser.PARAM_PLUSG));
            }
        }
        if (arguments.isSet(ProtTestArgumentParser.PARAM_PLUSF)) {
            setPlusF(true);
        }
    }

    /**
     * Inits the Lists.
     */
    private void initLists() {
//        for(int i=0; i<ALL_MATRICES.length; i++) {
//            matrixes.addElement(ALL_MATRICES[i]);
//        }
        distributionsHash = new HashMap<String, Integer>();
        distributionsHash.put("Uniform", new Integer(Model.DISTRIBUTION_UNIFORM));
        distributionsHash.put("+I", new Integer(Model.DISTRIBUTION_INVARIABLE));
        distributionsHash.put("+G", new Integer(Model.DISTRIBUTION_GAMMA));
        distributionsHash.put("+I+G", new Integer(Model.DISTRIBUTION_GAMMA_INV));
    }

    /**
     * Display the command line usage of the application.
     */
    public static void usage() {
        println("-------------------------------------------------------------------------------------------------");
        println("Basic usage: ");
        println(" - Sequential version: ");
        println("        java -jar prottest-2.1.jar -i alignm_file [OPTIONS]");
        println(" - Parallel version: ");
        println("        mpjrun.sh -wdir $PWD/ -np [NUM_PROCS] -jar ModelTest-2.1.jar -i alignm_file [OPTIONS]");
        println("OPTIONS:");
        println(" -i alignment_filename");
        println("            Alignment input file (required)");
        println(" -t tree_filename");
        println("            Tree file       (optional) [default: NJ tree]");
        println(" -o output_filename");
        println("            Output file     (optional) [default: standard output]");
        println(" -[matrix]");
        print(  "            Include matrix (Amino-acid) = "  );
        int count = 0;
        List<String> matrices = ProtTestFactory.getInstance().getApplicationGlobals().getSupportedMatrices();
        for (String matrix : matrices) {
            print(matrix + " ");
            if (count == (7)) {
                println("");
                print("	                                  ");
            }
            count++;
        }
        println("");
        println("                If you don't specify any matrix, all matrices displayed above will");
        println("                be included.");
        println(" -I");
        println("            Include models with a proportion of invariable sites");
        println(" -G");
        println("            Include models with rate variation among sites and number of categories");
        println(" -IG");
        println("            include models with both +I and +G properties");
        println(" -all-distributions");
        println("            Include models with rate variation among sites, number of categories and both");
        println(" -ncat number_of_categories");
        println("            Define number of categories for +G and +I+G models [default: " + DEFAULT_NCAT + "]");
        println(" -F");
        println("            Include models with empirical frequency estimation ");
        println(" -AIC");
        println("            Display models sorted by Akaike Information Criterion (AIC)");
        println(" -BIC");
        println("            Display models sorted by Bayesian Information Criterion (BIC)");
        println(" -AICC");
        println("            Display models sorted by Corrected Akaike Information Criterion (AICc)");
        println(" -DT");
        println("            Display models sorted by Decision Theory Criterion");
        println(" -all");
        println("            Displays a 7-framework comparison table");
        println(" -S optimization_strategy");
        println("            Optimization strategy mode: [default: " + DEFAULT_STRATEGY_MODE + "]");
        for (int i = 0; i < OPTIMIZE_NAMES.length; i++) {
            println("             		" + i + ": " + OPTIMIZE_NAMES[i]);
        }
        println(" -s moves");
        println("            Tree search operation for ML search: ");
        println("            NNI (fastest), SPR (slowest), BEST (best of NNI and SPR) [default: NNI]");
        println(" -sample sample_size_mode");
        println("            Sample size for AICc and BIC corrections [default: " + DEFAULT_SAMPLE_SIZE_MODE + "]");
        for (int i = 0; i < SIZE_MODE_NAMES.length; i++) {
            println("             		" + i + ": " + SIZE_MODE_NAMES[i]);
        }
        println(" -size user_size  		");
        println("            Specified sample size, only for \"-sample " + SIZEMODE_USERSIZE + "\"");
        println(" -t1      				");
        println("            Display best-model's newick tree [default: " + DEFAULT_DISPLAY_NEWICK_TREE + "]");
        println(" -t2      				");
        println("            Display best-model's ASCII tree  [default: " + DEFAULT_DISPLAY_ASCII_TREE + "]");
        println(" -tc consensus_threshold ");
        println("            Display consensus tree with the specified threshold, between 0.5 and 1.0");
        println("            [0.5 = majority rule consensus ; 1.0 = strict consensus]");
        println(" -threads number_or_threads			");
        println("            Number of threads requested to compute (only if MPJ is not used) [default: " +
                DEFAULT_THREADS + "]");
        println(" -verbose");
        println("            Verbose mode [default: " + DEFAULT_DEBUG + "]");
        println("-------------------------------------------------------------------------------------------------");
        println("Example: ");
        println("- Sequential version:");
        println("    java -jar ModelTest-2.1.jar -i alignm_file -t tree_file -S 0 -all-distributions -F -AIC -BIC -tc 0.5 > output");
        println("- Parallel version:");
        println("    mpjrun.sh -wdir $PWD/ -np 2 -jar ModelTest-2.1.jar -i alignm_file -t tree_file -S 0 -all-distributions -F -AIC -BIC -tc 0.5");
    }

    public void reportModelOptimization() {
        String tmp;
        tmp = "BioNJ";
        if (tree_file != null) {
            tmp = tree_file;
        }
        println("");
        println("ProtTest options");
        println("----------------");
        println("  Alignment file........... : " + align_file);
        println("  Tree..................... : " + tmp);
        println("  StrategyMode............. : " + OPTIMIZE_NAMES[strategyMode]);

        println("  Candidate models......... : ");
        print("    Matrices............... : ");
        for (int i = 0; i < matrices.size(); i++) {
            print(matrices.get(i) + " ");
        }
        println("");
        print("    Distributions.......... : ");
        for (String dist : distributionsHash.keySet()) {
            Integer value = (Integer) distributionsHash.get(dist);
            if (distributions.contains(value)) {
                print(dist + " ");
            }
        }
        println("");
        println("    Observed frequencies... : " + plusF);
        println("");
    }

    /**
     * Report the current options.
     */
    public void reportComplete() {
        reportModelOptimization();

        println("  Statistical framework");
        println("    Sort models according to....: ");
        StringBuilder sb = new StringBuilder();
        if (doAIC) {
            sb.append(" AIC ");
        }
        if (doBIC) {
            sb.append(" BIC ");
        }
        if (doAICc) {
            sb.append(" AICc ");
        }
        if (doDT) {
            sb.append(" DT ");
        }
        if (!(doAIC|doBIC|doAICc|doDT))
            sb.append(" lnL");
        println("      " + sb.toString());
        if (sampleSizeMode == SIZEMODE_USERSIZE) {
            println("    Sample size.................: " + sampleSize);
        } else {
            println("    Sample size.................: " + sampleSize + " (not calculated yet)");
        }
        println("      sampleSizeMode............: " + SIZE_MODE_NAMES[sampleSizeMode]);

        println("  Other options:");
        println("    Display best tree in ASCII..: " + displayASCIITree);
        println("    Display best tree in Newick.: " + displayNewickTree);
        println("    Display consensus tree......: " + displayConsensusTree);
        if (displayConsensusTree) {
            println("    Consensus threshold.........: " + consensusThreshold);
        }
        println("    Verbose.....................: " + debug);

        println("");
    }

    private static void print(String message) {
        info(message, ApplicationOptions.class);
    }

    private static void println(String message) {
        infoln(message, ApplicationOptions.class);
    }

    private static void verbose(String message) {
        fine(message, ApplicationOptions.class);
    }

    private static void verboseln(String message) {
        fineln(message, ApplicationOptions.class);
    }
}

