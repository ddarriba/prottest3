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
package es.uvigo.darwin.prottest.facade;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.exe.util.TemporaryFileManager;
import es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO;
import es.uvigo.darwin.prottest.facade.util.SelectionChunk;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.DT;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.LNL;
import es.uvigo.darwin.prottest.selection.printer.AminoAcidPrintFramework;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.fileio.AlignmentReader;

import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import java.io.StringWriter;
import static es.uvigo.darwin.prottest.util.logging.ProtTestLogger.*;

/**
 * An abstract implementation of the ProtTest facade.
 */
public abstract class ProtTestFacadeImpl
        extends ObservableModelUpdater
        implements ProtTestFacade {

//	/** The options. */
//	protected ApplicationOptions options;
    /** The Constant AIC. */
    public static final int AIC = SelectionChunk.AIC;
    /** The Constant BIC. */
    public static final int BIC = SelectionChunk.BIC;
    /** The Constant AICC. */
    public static final int AICC = SelectionChunk.AICC;
    /** The Constant DT. */
    public static final int DT = SelectionChunk.DT;
    /** The Constant LK. */
    public static final int LNL = SelectionChunk.LNL;

    /**
     * Instantiates a new prot test facade implementation.
     */
    public ProtTestFacadeImpl() {
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#printModelsSorted(es.uvigo.darwin.prottest.selection.InformationCriterion, java.io.PrintWriter)
     */
    public void printModelsSorted(InformationCriterion informationCriterion) {

        PrintFramework printFramework = new AminoAcidPrintFramework();

        printFramework.printModelsSorted(informationCriterion);

    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#readAlignment(java.io.PrintWriter, java.lang.String, boolean)
     */
    public Alignment readAlignment(String filename, boolean debug)
            throws AlignmentParseException, FileNotFoundException, IOException {

        StringWriter sw = new StringWriter();
        Alignment alignment = AlignmentReader.readAlignment(new PrintWriter(sw), filename, debug);
        
        sw.flush();
        ProtTestLogger.getDefaultLogger().infoln(sw.toString());
        
        return alignment;
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#readTree(java.io.PrintWriter, java.lang.String, boolean)
     */
    public Tree readTree(PrintWriter out, String filename, boolean debug)
            throws TreeFormatException, FileNotFoundException, IOException {

        return AlignmentReader.readTree(out, filename, debug);
    
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#update(es.uvigo.darwin.prottest.observer.Observable, java.lang.Object)
     */
    public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {
        notifyObservers(model, options);
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#computeInformationCriterion(pal.alignment.Alignment, es.uvigo.darwin.prottest.model.Model[], int, int, double)
     */
    public SelectionChunk computeInformationCriterion(Alignment alignment, Model[] models,
            int criterion, int sampleSizeMode, double sampleSize,
            double confidenceInterval) {

        ModelCollection modelCollection = new SingleModelCollection(models, alignment);

        InformationCriterion informationCriterion;

        double calculatedSampleSize = ProtTestAlignment.calculateSampleSize(alignment, sampleSizeMode, sampleSize);

        switch (criterion) {
            case AIC:
                informationCriterion = new AIC(modelCollection, confidenceInterval, calculatedSampleSize);
                break;
            case BIC:
                informationCriterion = new BIC(modelCollection, confidenceInterval, calculatedSampleSize);
                break;
            case AICC:
                informationCriterion = new AICc(modelCollection, confidenceInterval, calculatedSampleSize);
                break;
            case DT:
                informationCriterion = new DT(modelCollection, confidenceInterval, calculatedSampleSize);
                break;
            case LNL:
                informationCriterion = new LNL(modelCollection, confidenceInterval, calculatedSampleSize);
                break;
            default:
                throw new ProtTestInternalException("Unexistent information criterion");
        }

        return new SelectionChunk(informationCriterion);

    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#getNumberOfThreads()
     */
    public int getNumberOfThreads() {
        // single thread (default)
        return 1;
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#setNumberOfThreads(int)
     */
    public void setNumberOfThreads(int numThreads) {
        throw new ProtTestInternalException("No threading support");
    }

    protected Tree calculateBionjJTT(ApplicationOptions options) {

        Model jttModel = ProtTestFactory.getInstance().createModel("JTT", options.getDistribution("Uniform"), new Properties(),
                options.getAlignment(), null, 0);
        TemporaryFileManager.synchronize(
                options.getAlignment(), null, 1);
        RunEstimator treeEstimator = ProtTestFactory.getInstance().createRunEstimator(options, jttModel);
        treeEstimator.run();

        return jttModel.getTree();
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#startAnalysis(es.uvigo.darwin.prottest.util.printer.ProtTestPrinter)
     */
    public Model[] startAnalysis(ApplicationOptions options) {

        if (options.getTreeFile() == null) {
            // this way, the starting topology is the same for every model
            if (options.strategyMode == OPTIMIZE_FIXED_BIONJ) {
                // use JTT BIONJ Tree
                Tree jttTree = calculateBionjJTT(options);

                options.setTree(jttTree);
            }
        }
        TemporaryFileManager.synchronize(options.getAlignment(), options.getTree(),
                getNumberOfThreads());
        Model[] models = analyze(options);

        return models;
    }

    /**
     * Analyze.
     * 
     * @param options the execution options
     * 
     * @return the set of optimized models
     */
    public abstract Model[] analyze(ApplicationOptions options);

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#configure(es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO)
     */
    public ApplicationOptions configure(ProtTestParameterVO parameters)
            throws IOException, AlignmentParseException, ProtTestInternalException {
        ApplicationOptions options = new ApplicationOptions();
        if (parameters.getAlignment() != null) {
            options.setAlignment(parameters.getAlignment());
            options.setAlignmentFilename(parameters.getAlignmentFilePath());
        } else {
            options.setAlignment(parameters.getAlignmentFilePath());
        }
        options.setNumberOfCategories(parameters.ncat);
        for (String matrix : parameters.getMatrices()) {
            options.addMatrix(matrix);
        }
        for (String distribution : parameters.getDistributions()) {
            options.addDistribution(distribution);
        }
        options.setPlusF(parameters.isPlusF());
        options.setTreeFile(parameters.getTreeFilePath());
        options.setStrategyMode(parameters.getStrategyMode());
        return options;
    }

    protected void print(String message) {
        info(message, ProtTestFacade.class);
    }

    protected void println(String message) {
        infoln(message, ProtTestFacade.class);
    }

    protected void error(String message) {
        severe(message, ProtTestFacade.class);
    }

    protected void errorln(String message) {
        severeln(message, ProtTestFacade.class);
    }
    
    protected void warnln(String message) {
        warningln(message, ProtTestFacade.class);
    }

    protected void verbose(String message) {
        fine(message, ProtTestFacade.class);
    }

    protected void verboseln(String message) {
        fineln(message, ProtTestFacade.class);
    }

    protected void flush() {
        ProtTestLogger.flush(ProtTestFacade.class);
    }
}
