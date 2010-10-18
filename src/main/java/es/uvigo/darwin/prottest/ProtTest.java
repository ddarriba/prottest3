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
package es.uvigo.darwin.prottest;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import es.uvigo.darwin.prottest.consensus.Consensus;
import mpi.MPI;
import es.uvigo.darwin.prottest.facade.ProtTestFacade;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeMPJ;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeSequential;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeThread;
import es.uvigo.darwin.prottest.facade.TreeFacade;
import es.uvigo.darwin.prottest.facade.TreeFacadeImpl;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.DT;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.LNL;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.util.FixedBitSet;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import es.uvigo.darwin.prottest.util.printer.ProtTestPrinter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import pal.misc.Identifier;
import pal.tree.Tree;

/**
 * This is the main class of ProtTest. It calls the methods in the
 * concrete facade to interact with the model layer of the application.
 * 
 * @author Diego Darriba
 */
public class ProtTest {

    /** The Constant versionNumber. */
    public static final String versionNumber = "3.0-beta";
    /** The Constant versionDate. */
    public static final String versionDate = "18th October 2010";
    /** The MPJ rank of the process. It is only useful if MPJ is running.*/
    public static int MPJ_ME;
    /** The MPJ size of the communicator. It is only useful if MPJ is running.*/
    public static int MPJ_SIZE;
    /** The MPJ running state. */
    public static boolean MPJ_RUN;
    /** The ProtTest factory. */
    private static ProtTestFactory factory;

    /**
     * The main method. It initializes the MPJ runtime environment, parses 
     * the application arguments, initializes the application options and 
     * starts the analysis of the substitution models.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {

        ProtTestLogger logger = ProtTestLogger.getDefaultLogger();
        logger.setStdHandlerLevel(Level.INFO);

        // initializing MPJ environment (if available)
        try {
            String[] argsApp = MPI.Init(args);
            MPJ_ME = MPI.COMM_WORLD.Rank();
            MPJ_SIZE = MPI.COMM_WORLD.Size();
            MPJ_RUN = true;
            args = argsApp;
        } catch (Exception e) {
            MPJ_ME = 0;
            MPJ_SIZE = 1;
            MPJ_RUN = false;
        }

        args = ProtTestFactory.initialize(args);
        factory = ProtTestFactory.getInstance();

        // parse arguments
        ApplicationOptions opts = new ApplicationOptions();

        int numThreads = 1;
        try {
            // check arguments and get application options
            numThreads = Integer.parseInt(
                    factory.createProtTestArgumentParser(args, opts).
                    getValue(ProtTestArgumentParser.PARAM_NUM_THREADS));
        } catch (IllegalArgumentException e) {
            if (MPJ_ME == 0) {
                System.err.println(e.getMessage());
                ApplicationOptions.usage();
            }
            finalize(1);
        }

        // get the facade instance
        TreeFacade treeFacade = new TreeFacadeImpl();
        ProtTestFacade facade;
        if (MPJ_RUN) {
            facade = new ProtTestFacadeMPJ(MPJ_RUN, MPJ_ME, MPJ_SIZE);
        } else if (numThreads > 1) {
            facade = new ProtTestFacadeThread(numThreads);
        } else {
            facade = new ProtTestFacadeSequential();
        }

        /* if multiple models are optimized together, the execution will be
        monitorized  */
        if (MPJ_RUN || numThreads > 1) {
            facade.addObserver(new ModelUpdaterObserver() {

                @Override
                public void update(ObservableModelUpdater o, Model model,
                        ApplicationOptions options) {
                    if (model.isComputed() && options != null) {
                        System.out.println("Computed: " + model.getModelName() + " (" + model.getLk() + ")");
                    } else {
                        System.out.println("Computing " + model.getModelName() + "...");
                    }

                }
            });
        }

        if (opts.isDebug()) {
            logger.setStdHandlerLevel(Level.ALL);
        }

        if (MPJ_ME == 0) {
            ProtTestPrinter.printHeader();
            opts.reportComplete();
        }

        Model[] models;
        try {

            // model optimization
            models = facade.startAnalysis(opts);

            // analyze results
            if (MPJ_ME == 0) {
                ModelCollection allModelsList =
                        new SingleModelCollection(models, opts.getAlignment());

                InformationCriterion ic;
                switch (opts.getSortBy()) {
                    case SORTBY_AIC:
                        ic = new AIC(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case SORTBY_BIC:
                        ic = new BIC(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case SORTBY_AICC:
                        ic = new AICc(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case SORTBY_DT:
                        ic = new DT(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case SORTBY_LNL:
                        ic = new LNL(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    default:
                        throw new ProtTestInternalException(
                                "Unrecognized information criterion");
                }

                // let's print results:
                facade.printModelsSorted(ic);

                // display 7-framework comparison
                if (opts.isAll()) {
                    PrintFramework.printFrameworksComparison(ic.getModelCollection());
                }

                // display best model tree in ASCII
                if (opts.isDisplayASCIITree()) {
                    logger.infoln(treeFacade.toASCII(ic.getBestModel().getTree()));
                }

                // display best model tree in Newick
                if (opts.isDisplayNewickTree()) {
                    logger.infoln(treeFacade.toNewick(ic.getBestModel().getTree(), true, true, false));
                }

                // display consensus tree data
                if (opts.isDisplayConsensusTree()) {

                    Consensus consensus = treeFacade.createConsensus(ic, opts.getConsensusThreshold());

                    logger.infoln("");
                    logger.infoln("");
                    logger.infoln("----------------------------------------");
                    logger.infoln("------------ Consensus Tree ------------");
                    logger.infoln("----------------------------------------");
                    logger.infoln("Selection criterion: . . . . " + SORTBY_NAMES[opts.getSortBy() - 'A']);
                    logger.infoln("Sample size: . . . . . . . . " + opts.getSampleSize());
                    logger.infoln("Consensus support threshold: " + opts.getConsensusThreshold());
                    logger.infoln("----------------------------------------");
                    logger.infoln("");

                    Set<FixedBitSet> keySet = consensus.getCladeSupport().keySet();
                    List<FixedBitSet> splitsInConsensus = new ArrayList<FixedBitSet>();
                    List<FixedBitSet> splitsOutFromConsensus = new ArrayList<FixedBitSet>();

                    for (FixedBitSet fbs : keySet) {
                        if (fbs.cardinality() > 1) {
                            double psupport = (1.0 * consensus.getCladeSupport().get(fbs)) / 1.0;
                            if (psupport < opts.getConsensusThreshold()) {
                                splitsOutFromConsensus.add(fbs);
                            } else {
                                splitsInConsensus.add(fbs);
                            }
                        }
                    }

                    logger.infoln("# # # # # # # # # # # # # # # #");
                    logger.infoln(" ");
                    logger.infoln("Species in order:");
                    logger.infoln(" ");

                    for (int i = 0; i < consensus.getIdGroup().getIdCount(); i++) {
                        Identifier id = consensus.getIdGroup().getIdentifier(i);
                        logger.infoln("    " + (i + 1) + ". " + id.getName());
                    }
                    logger.infoln(" ");
                    logger.infoln("# # # # # # # # # # # # # # # #");
                    logger.infoln(" ");
                    logger.infoln("Sets included in the consensus tree");
                    logger.infoln(" ");
                    logger.info("    ");
                    for (int i = 0; i < consensus.getIdGroup().getIdCount(); i++) {
                        logger.info(String.valueOf(i + 1));
                    }
                    logger.infoln(" ");
                    for (FixedBitSet fbs : splitsInConsensus) {
                        logger.infoln("    " + fbs.splitRepresentation() + " ( " +
                                Utilities.round(consensus.getCladeSupport().
                                get(fbs), 5) + " )");
                    }
                    logger.infoln(" ");
                    logger.infoln("Sets NOT included in consensus tree");
                    logger.infoln(" ");
                    logger.info("    ");
                    for (int i = 0; i < consensus.getIdGroup().getIdCount(); i++) {
                        logger.info(String.valueOf(i + 1));
                    }
                    logger.infoln(" ");
                    for (FixedBitSet fbs : splitsOutFromConsensus) {
                        logger.infoln("    " + fbs.splitRepresentation() + " ( " +
                                Utilities.round(consensus.getCladeSupport().
                                get(fbs), 5) + " )");
                    }
                    logger.infoln("\n");
                    Tree consensusTree = consensus.getConsensusTree();
                    String newickTree = treeFacade.toNewick(consensusTree, true, true, true);
                    logger.infoln(newickTree);
                    logger.infoln("");
                    logger.infoln(treeFacade.toASCII(consensusTree));
                    logger.infoln("");
                    logger.infoln(treeFacade.branchInfo(consensusTree));
                    logger.infoln("");
                    logger.infoln(treeFacade.heightInfo(consensusTree));

                }

            }

        } catch (ProtTestInternalException e) {
            logger.severeln(e.getMessage());
            finalize(-1);
        }

        // finalize execution

        finalize(0);

        if (MPJ_RUN) {
            MPI.Finalize();
        }
    }

    /**
     * Finalizes the MPJ runtime environment. When an error occurs, it
     * aborts the execution of every other processes.
     * 
     * @param status the finalization status
     */
    public static void finalize(int status) {

        if (status != 0) {
            if (MPJ_RUN) {
                MPI.COMM_WORLD.Abort(status);
            }
        }

        if (MPJ_RUN) {
            MPI.Finalize();
        }

        System.exit(status);

    }
}
