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
package es.uvigo.darwin.prottest.util.factory;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.exe.PhyMLv3AminoAcidRunEstimator;
import es.uvigo.darwin.prottest.exe.RaxMLAminoAcidRunEstimator;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.global.RaxmlAminoAcidApplicationGlobals;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.AminoAcidModel;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.printer.AminoAcidPrintFramework;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.util.argumentparser.AminoAcidArgumentParser;
import es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

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
    /** Unique log handler. */
    private Handler logHandler;

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
                    "Cannot create factory (unexistent sort : " + sort + ")");
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
    public ProtTestArgumentParser createProtTestArgumentParser(String[] args, ApplicationOptions options) {
        ProtTestArgumentParser mtap = null;
        switch (sort) {
            case PROTEIC:
                mtap = new AminoAcidArgumentParser(args, options);
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Unsupported operation: nucleic data");
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
        String analyzer = APPLICATION_PROPERTIES.getProperty("analyzer");
        switch (sort) {
            case PROTEIC:
                if (analyzer.equals(ANALYZER_RAXML)) {
                    ap = new RaxmlAminoAcidApplicationGlobals();
                } else if (analyzer.equals(ANALYZER_PHYML)) {
                    ap = new AminoAcidApplicationGlobals();
                } else {
                    throw new ProtTestInternalException("Analyzer " + analyzer + " not supported by RunEstimator. Check your prottest.properties file");
                }
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Unsupported operation: nucleic data");
        }
        return ap;
    }

    /**
     * Creates a new ProtTest object.
     * 
     * @param matrix the matrix
     * @param distribution the distribution
     * @param modelProperties the model APPLICATION_PROPERTIES
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
                throw new ProtTestInternalException("Unsupported operation: nucleic data");
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
        return createRunEstimator(options, model, 1);
    }

    /**
     * Creates a new ProtTest object.
     * 
     * @param options the options
     * @param model the model
     * 
     * @return the run estimator
     */
    public RunEstimator createRunEstimator(ApplicationOptions options, Model model, int numberOfThreads) {
        RunEstimator runEstimator = null;
        String analyzer = APPLICATION_PROPERTIES.getProperty("analyzer");
        if (analyzer.equals(ANALYZER_PHYML)) {
            switch (sort) {
                case PROTEIC:
                    runEstimator = new PhyMLv3AminoAcidRunEstimator(options, model, numberOfThreads);
                    break;
                case NUCLEIC:
                    throw new ProtTestInternalException("Unsupported operation: nucleic data");
            }
        } else if (analyzer.equals(ANALYZER_RAXML)) {
            switch (sort) {
                case PROTEIC:
                    runEstimator = new RaxMLAminoAcidRunEstimator(options, model);
                    break;
                case NUCLEIC:
                    throw new ProtTestInternalException("Unsupported operation: nucleic data");
            }
        } else {
            throw new ProtTestInternalException("Analyzer " + analyzer + " not supported by RunEstimator");
        }
        return runEstimator;
    }

    public Handler createLogHandler() throws IOException {
        //   Log level is configurable:
        //   'info'    Only general information messages are logged (default)
        //   'fine'    General debug information is also logged
        //   'finer'   More complex debug information is logged
        //   'finest'  All activity is tracked

        if (logHandler == null) {

            String[] supportedLevels = {"INFO", "FINE", "FINER", "FINEST"};

            String logDirName = APPLICATION_PROPERTIES.getProperty("log_dir");
            String level = APPLICATION_PROPERTIES.getProperty("log_level", "info").toUpperCase();
            boolean supported = false;
            for (String testLevel : supportedLevels) {
                supported |= testLevel.equals(level);
            }

            if (logDirName != null && supported) {
                File logDir = new File(logDirName);
                if (!logDir.exists()) {
                    logDir.mkdirs();
                }
                File logFile = File.createTempFile(
                        "prottest3_", ".log", logDir);
                FileOutputStream fos = new FileOutputStream(logFile);
                logHandler = new StreamHandler(fos, new ProtTestLogFormatter());
                logHandler.setLevel(Level.parse(level));
            }

        }
        return logHandler;

    }

    /**
     * Creates a new ProtTest object.
     *
     * @return the prints the framework
     */
    public PrintFramework createPrintFramework() {
        PrintFramework pf = null;

        switch (sort) {
            case PROTEIC:
                pf = new AminoAcidPrintFramework();
                break;
            case NUCLEIC:
                throw new ProtTestInternalException("Unsupported operation: nucleic data");
        }

        return pf;
    }
}
