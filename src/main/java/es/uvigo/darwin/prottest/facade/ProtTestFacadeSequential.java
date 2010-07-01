package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.checkpoint.status.ProtTestStatus;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;
import java.io.StringWriter;

/**
 * A sequential implementation of the ProtTest facade.
 */
public class ProtTestFacadeSequential extends ProtTestFacadeImpl {

    private CheckPointManager cpManager;
    private ModelCollection modelSet;
    /** The ProtTest factory to instantiate some application objects. */
    private ProtTestFactory factory = ProtTestFactory.getInstance();

    public Model[] analyze(ApplicationOptions options) {

        //For each model, for each distribution,... optimize the model and calculate some statistics:

        println("**********************************************************");
        //this is only for doing output prettier
        if (options.getSampleSizeMode() == SIZEMODE_SHANNON || options.getSampleSizeMode() == SIZEMODE_SHANNON_NxL) {
            double tmpSampleSize = ProtTestAlignment.calculateShannonSampleSize(options.getAlignment(), options.getSampleSizeMode(), true);
            println("Shannon entropy based sample size: " + ProtTestFormattedOutput.getDecimalString(tmpSampleSize, 2));
        }
        println("Observed number of invariant sites: " + ProtTestAlignment.calculateInvariableSites(options.getAlignment(), false));
        StringWriter sw = new StringWriter();
        ProtTestAlignment.printFrequencies(ProtTestAlignment.getFrequencies(options.getAlignment()),
                new PrintWriter(sw));
        sw.flush();
        println(sw.toString());
        println("**********************************************************");
        println("");

        //TimeStamp timer = new TimeStamp();
        Date startDate = new Date();
        long startTime = startDate.getTime();

        int numberOfModels = 0;

        ModelCollection arrayListModel = new SingleModelCollection(options.getAlignment());

        //Adding support for checkpointing
        ProtTestStatus initialStatus = new ProtTestStatus(null, options);
        cpManager = new CheckPointManager();

        if (cpManager.loadStatus(initialStatus)) {
            arrayListModel = new SingleModelCollection(
                    ((ProtTestStatus) cpManager.getLastCheckpoint()).getModels(),
                    options.getAlignment());
        } else {
            Properties modelProperties = new Properties();

            if (options.isPlusF()) {
                modelProperties.setProperty(Model.PROP_PLUS_F, "true");
            }
            arrayListModel.addModelCartesian(options.getMatrices(), options.getDistributions(), modelProperties,
                    options.getAlignment(), options.getTree(), options.ncat);
        }
        numberOfModels = arrayListModel.size();
        modelSet = arrayListModel;

        flush();

        RunEstimator[] runenv = new RunEstimator[modelSet.size()];

        int current = 0;
        for (Model model : modelSet) {

            runenv[current] = factory.createRunEstimator(options, model);
            runenv[current].addObserver(this);
            runenv[current].run();

            runenv[current].report();
            flush();
            current++;

        }

        long endTime = System.currentTimeMillis();

        Model[] allModels = new Model[numberOfModels];

        println("************************************************************");
        String runtimeStr = Utilities.calculateRuntime(startTime, endTime);
        println("Date   :  " + (new Date()).toString());
        println("Runtime:  " + runtimeStr);
        println("");
        println("");
        allModels = modelSet.toArray(new Model[0]);

        cpManager.done();
        return allModels;
    }

    @Override
    public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {

        if (model.isComputed() && options != null) {
            if (cpManager != null) {
                ProtTestStatus newStatus = new ProtTestStatus(modelSet.toArray(new Model[0]), options);
                cpManager.setStatus(newStatus);
            }
        }

        super.update(o, model, options);
    }
}
