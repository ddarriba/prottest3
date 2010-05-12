package es.uvigo.darwin.prottest.util.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.exe.PhyMLv3AminoAcidRunEstimator;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.global.RaxmlAminoAcidApplicationGlobals;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.AminoAcidModel;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.printer.AminoAcidPrintFramework;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.util.argumentparser.AminoAcidArgumentParser;
import es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * A factory for creating ProtTest objects.
 */
public class ProtTestFactory {

    /** The Constant PROTEIC. */
    public static final int PROTEIC = 1;
    /** The Constant NUCLEIC. */
    public static final int NUCLEIC = 2;
    /** The Constant MAX_SORT. */
    private static final int MAX_SORT = 2;
    /** The instance. */
    private static ProtTestFactory instance;
    /** The sort. */
    private int sort;

    /**
     * Instantiates a new prot test factory.
     * 
     * @param sort the sort
     * 
     * @throws IllegalArgumentException the illegal argument exception
     */
    private ProtTestFactory(int sort)
            throws IllegalArgumentException {
        if (sort <= 0 || sort > MAX_SORT) {
            throw new IllegalArgumentException(
                    "Cannot create factory (unexistent sort)");
        }
        this.sort = sort;
    }

    /**
     * Initialize.
     * 
     * @param args the args
     * 
     * @return the string[]
     */
    public static String[] initialize(String[] args) {
        if (instance == null) {
            int sort;
            List<String> argList = new ArrayList<String>(Arrays.asList(args));
            int index = argList.indexOf(ProtTestArgumentParser.ARG_TOKEN + ProtTestArgumentParser.PARAM_DATA_TYPE);
            if (index < 0) //set default
            {
                sort = PROTEIC;
            } else {
                String value = argList.get(index + 1);
                argList.remove(index + 1);
                argList.remove(index);
                args = argList.toArray(new String[0]);
                if (value.equals(ProtTestArgumentParser.DATA_TYPE_AMINOACID)) {
                    sort = PROTEIC;
                } else if (value.equals(ProtTestArgumentParser.DATA_TYPE_NUCLEOTIDE)) {
                    sort = NUCLEIC;
                } else {
                    throw new IllegalArgumentException("Invalid data type " + value);
                }
            }
            instance = new ProtTestFactory(sort);
            return args;
        } else {
            throw new ProtTestInternalException("ModelTestFactory was already initialized");
        }
    }

    /**
     * Gets the single instance of ProtTestFactory.
     * 
     * @return single instance of ProtTestFactory
     */
    public static ProtTestFactory getInstance() {
        if (instance == null) {
            initialize(new String[0]);
        }
//			throw new ProtTestInternalException("ModelTestFactory should be initialized");

        return instance;
    }

    /**
     * Creates a new ProtTest object.
     * 
     * @param args the args
     * @param options the options
     * 
     * @return the prot test argument parser
     */
    public ProtTestArgumentParser createModelTestArgumentParser(String[] args, ApplicationOptions options) {
        ProtTestArgumentParser mtap = null;
        switch (sort) {
            case PROTEIC:
                mtap = new AminoAcidArgumentParser(args, options);
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Unsupported operation");
        }
        return mtap;
    }

    /**
     * Gets the application globals.
     * 
     * @return the application globals
     */
    public ApplicationGlobals getApplicationGlobals() {
        ApplicationGlobals ap = null;
        String analyzer = ApplicationGlobals.properties.getProperty("analyzer");
        switch (sort) {
            case PROTEIC:
                if (analyzer.equals(ApplicationGlobals.ANALYZER_RAXML)) {
                    ap = new RaxmlAminoAcidApplicationGlobals();
                } else if (analyzer.equals(ApplicationGlobals.ANALYZER_PHYML)) {
                    ap = new AminoAcidApplicationGlobals();
                } else {
                    throw new ProtTestInternalException("Analyzer not supported by RunEstimator");
                }
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Unsupported operation");
        }
        return ap;
    }

    /**
     * Creates a new ProtTest object.
     * 
     * @param matrix the matrix
     * @param distribution the distribution
     * @param modelProperties the model properties
     * 
     * @return the model
     */
    public Model createModel(String matrix, int distribution, Properties modelProperties,
            Alignment alignment, Tree tree, int ncat) {
        Model model = null;
        boolean plusF;
        if (modelProperties != null) {
            plusF = Boolean.parseBoolean(
                    modelProperties.getProperty(
                    AminoAcidModel.PROP_PLUS_F, "false"));
        } else {
            plusF = false;
        }
        switch (sort) {
            case PROTEIC:
                model = new AminoAcidModel(matrix, distribution, plusF,
                        alignment, tree, ncat);
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Unsupported operation");
        }
        return model;
    }

    /**
     * Creates a new ProtTest object.
     * 
     * @param options the options
     * @param model the model
     * 
     * @return the run estimator
     */
    public RunEstimator createRunEstimator(ApplicationOptions options, Model model) {
        RunEstimator runEstimator = null;
        String analyzer = ApplicationGlobals.properties.getProperty("analyzer");
        if (analyzer.equals(ApplicationGlobals.ANALYZER_PHYML)) {
            switch (sort) {
                case PROTEIC:
                    runEstimator = new PhyMLv3AminoAcidRunEstimator(options, model);
                    break;
                case NUCLEIC:
                    throw new ProtTestInternalException("Unsupported operation");
            }
        } else if (analyzer.equals(ApplicationGlobals.ANALYZER_RAXML)) {
            switch (sort) {
                case PROTEIC:
//				runEstimator = new RaxMLAminoAcidRunEstimator(options, model);
                    break;
                case NUCLEIC:
                    throw new ProtTestInternalException("Unsupported operation");
            }
        } else {
            throw new ProtTestInternalException("Analyzer not supported by RunEstimator");
        }
        return runEstimator;
    }

    /**
     * Creates a new ProtTest object.
     * 
     * @param informationCriterion the information criterion
     * 
     * @return the prints the framework
     */
    public PrintFramework createPrintFramework(InformationCriterion informationCriterion) {
        PrintFramework pf = null;

        switch (sort) {
            case PROTEIC:
                pf = new AminoAcidPrintFramework(informationCriterion);
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Not implemented");
        }

        return pf;
    }
}
