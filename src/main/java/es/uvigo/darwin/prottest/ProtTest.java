package es.uvigo.darwin.prottest;

import java.io.PrintWriter;

import mpi.MPI;
import pal.util.XMLConstants;
import es.uvigo.darwin.prottest.facade.ProtTestFacade;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeMPJ;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeSequential;
import es.uvigo.darwin.prottest.facade.ProtTestFacadeThread;
import es.uvigo.darwin.prottest.facade.TreeFacade;
import es.uvigo.darwin.prottest.facade.TreeFacadeImpl;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.LNL;
import es.uvigo.darwin.prottest.selection.printer.AminoAcidPrintFramework;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.util.argumentparser.ProtTestArgumentParser;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.exception.AlignmentFormatException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import es.uvigo.darwin.prottest.util.printer.ProtTestPrinter;
import java.util.logging.Level;
import pal.tree.Tree;

/**
 * This is the main class of ProtTest. It calls the methods in the
 * concrete facade to interact with the model layer of the application.
 */
public class ProtTest implements XMLConstants {

    /** The Constant versionNumber. */
    public static final String versionNumber = "3.0-beta";
    /** The Constant versionDate. */
    public static final String versionDate = "1st December 2009";
    /** The MPJ rank of the process. It is only useful if MPJ is running. */
    public static int MPJ_ME;
    /** The MPJ size of the communicator. It is only useful if MPJ is running. */
    public static int MPJ_SIZE;
    /** The MPJ running state. */
    public static boolean MPJ_RUN;
    /** The output writer. */
    private static PrintWriter outputWriter;
    /** The error writer. */
    private static PrintWriter errorWriter;
    /** The ProtTest factory. */
    private static ProtTestFactory factory;
    /** The application printer. */
    private static ProtTestPrinter printer;

    /**
     * The main method. It initializes the MPJ runtime environment, parses 
     * the application arguments, initializes the application options and 
     * starts the analysis of the substitution models.
     * 
     * @param args the args
     */
    public static void main(String[] args) {

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
        outputWriter = new PrintWriter(System.out);
        errorWriter = new PrintWriter(System.err);

        // parse arguments
        ApplicationOptions opts = new ApplicationOptions();
        printer = factory.createModelTestPrinter(outputWriter, errorWriter);
        opts.setPrinter(printer);
        int numThreads = 1;
        try {
            // check arguments and get application options
            numThreads = Integer.parseInt(
                    factory.createModelTestArgumentParser(args, opts).getValue(ProtTestArgumentParser.PARAM_NUM_THREADS));
        } catch (IllegalArgumentException e) {
            if (MPJ_ME == 0) {
                System.err.println(e.getMessage());
                ApplicationOptions.usage(printer.getErrorWriter());
            }
            finalize(1);
        } catch (AlignmentFormatException e) {
            if (MPJ_ME == 0) {
                System.err.println(e.getMessage());
            }
            finalize(1);
        }

        Level level = Level.INFO;
        if (opts.isDebug())
            level = Level.ALL;
        ProtTestLogger.getDefaultLogger().setStdHandlerLevel(level);
        
        TreeFacade treeFacade = new TreeFacadeImpl();
        ProtTestFacade facade;
        if (MPJ_RUN) {
            facade = new ProtTestFacadeMPJ(MPJ_RUN, MPJ_ME, MPJ_SIZE);
        } else if (numThreads > 1) {
            facade = new ProtTestFacadeThread(numThreads);
        } else {
            facade = new ProtTestFacadeSequential();
        }

        if (MPJ_RUN || numThreads > 1) {
            facade.addObserver(new ModelUpdaterObserver() {

                @Override
                public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {

                    System.out.println("Computed: " + model.getModelName() + " (" + model.getLk() + ")");
                    System.out.println("");

                }
            });
        }

        if (MPJ_ME == 0) {
            printer.printHeader();
            opts.report(printer.getOutputWriter());
            printer.getOutputWriter().flush();
        }

        Model[] models;
        try {
            models = facade.startAnalysis(opts);

            if (MPJ_ME == 0) {
                ModelCollection allModelsList = new SingleModelCollection(models, opts.getAlignment());
                InformationCriterion ic;
                //let's print results:
                switch (opts.getSortBy()) {
                    case ApplicationGlobals.SORTBY_AIC:
                        ic = new AIC(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case ApplicationGlobals.SORTBY_BIC:
                        ic = new BIC(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case ApplicationGlobals.SORTBY_AICC:
                        ic = new AICc(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    case ApplicationGlobals.SORTBY_LNL:
                        ic = new LNL(allModelsList, 1.0, opts.getSampleSize());
                        break;
                    default:
                        throw new ProtTestInternalException(
                                "Unrecognized information criterion");
                }
                ic.printModelsSorted(printer.getOutputWriter());

                PrintFramework pf = new AminoAcidPrintFramework(ic);
                if (opts.isDisplayASCIITree()) {
                    pf.displayASCIITree(printer.getOutputWriter());
                }
                if (opts.isDisplayNewickTree()) {
                    pf.displayNewickTree(printer.getOutputWriter());
                }
                if (opts.isDisplayConsensusTree()) {
                    printer.getOutputWriter().println("");
                    printer.getOutputWriter().println("");
                    printer.getOutputWriter().println("***********************************************");
                    printer.getOutputWriter().println("           Consensus tree (" +
                            opts.getConsensusThreshold() + ")");
                    printer.getOutputWriter().println("***********************************************");
                    Tree consensus = treeFacade.createConsensusTree(ic, opts.getConsensusThreshold());
                    printer.getOutputWriter().println(treeFacade.toASCII(consensus));
                }
            }
        } catch (ProtTestInternalException e) {
            printer.getErrorWriter().println(e.getMessage());
            finalize(-1);
        }

        outputWriter.flush();
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

//		ApplicationOptions.deleteTemporaryFiles();

        if (status != 0) {
            printer.getErrorWriter().flush();
            if (MPJ_RUN) {
                MPI.COMM_WORLD.Abort(status);
            }
        } else {
            printer.getOutputWriter().flush();
        }

        if (MPJ_RUN) {
            MPI.Finalize();
        }

        System.exit(status);

    }
}
