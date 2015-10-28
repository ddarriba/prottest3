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

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.APPLICATION_PROPERTIES;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import mpi.MPI;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.exe.util.TemporaryFileManager;
import es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.DynamicDistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.HybridDistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.ImprovedDynamicDistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.StaticDistributionStrategy;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.checkpoint.status.ProtTestStatus;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.comparator.AminoAcidModelNaturalComparator;
import es.uvigo.darwin.prottest.util.comparator.ModelDistributionHeuristic;

/**
 * A parallel implementation of the ProtTest facade.
 */
public class ProtTestFacadeMPJ extends ProtTestFacadeImpl {

    /** The parallel distribution strategy. */
    private DistributionStrategy strategy;
    /** Boolean variable to show if MPJ environment is running. Some
     * distribution strategies can be used even if the execution is
     * sequential. */
    private boolean mpjRun;
    /** The MPJ rank of the process. It is only useful if MPJ is running. */
    private int mpjMe;
    /** The MPJ size of the communicator. It is only useful if MPJ is running. */
    private int mpjSize;
    /** The thread pool size. */
    private int poolSize = 1;
    private CheckPointManager cpManager;

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#getNumberOfThreads()
     */
    @Override
    public int getNumberOfThreads() {
        return poolSize;
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#setNumberOfThreads(int)
     */
    @Override
    public void setNumberOfThreads(int numThreads) {
//		options.setNumberOfThreads(numThreads);
        this.poolSize = numThreads;
    }
    
    /**
     * Instantiates a new parallel ProtTest facade.
     * 
     * @param mpjRun the running state of MPJ
     * @param mpjMe the rank of the current process in MPJ
     * @param mpjSize the size of the MPJ communicator
     */
    public ProtTestFacadeMPJ(boolean mpjRun, int mpjMe, int mpjSize) {
        this.mpjRun = mpjRun;
        this.mpjMe = mpjMe;
        this.mpjSize = mpjSize;
    }

    @Override
    protected Tree calculateBionjJTT(ApplicationOptions options) {
        Tree[] t = new Tree[1];
        if (mpjMe == 0) {
            t[0] = super.calculateBionjJTT(options);
        }
        MPI.COMM_WORLD.Bcast(t, 0, 1, MPI.OBJECT, 0);
        return t[0];
    }

    public Model[] analyze(ApplicationOptions options) {

        ModelCollection arrayListModel = null;
        if (mpjMe == 0) {
            //Adding support for checkpointing
            ProtTestStatus initialStatus = new ProtTestStatus(null, options);

            cpManager = new CheckPointManager();
            if (cpManager.loadStatus(initialStatus)) {
                arrayListModel = new SingleModelCollection(
                        ((ProtTestStatus) cpManager.getLastCheckpoint()).getModels(),
                        options.getAlignment());
            } else {
                arrayListModel = new SingleModelCollection(options.getAlignment());
                Properties modelProperties = new Properties();
                if (options.isPlusF()) {
                    modelProperties.setProperty(Model.PROP_PLUS_F, "true");
                }
                arrayListModel.addModelCartesian(options.getMatrices(), options.getDistributions(), modelProperties,
                        options.getAlignment(), options.getTree(), options.ncat);
            }
        }
        String strategyProp = APPLICATION_PROPERTIES.getProperty("parallel_strategy", "static");

        if (strategyProp.equals("dynamic")) {
            strategy = new DynamicDistributionStrategy(mpjMe, mpjSize, options, cpManager);
        } else if (strategyProp.equals("dynamic_improved")) {
            strategy = new ImprovedDynamicDistributionStrategy(mpjMe, mpjSize, options, cpManager);
        } else if (strategyProp.equals("hybrid")) {
            int numberOfThreads;
            try {
                numberOfThreads = Integer.parseInt(APPLICATION_PROPERTIES.getProperty("number_of_threads", 
                    String.valueOf(Runtime.getRuntime().availableProcessors())));
            } catch (NumberFormatException ex) {
                numberOfThreads = Runtime.getRuntime().availableProcessors();
            }
            setNumberOfThreads(numberOfThreads);
            strategy = new HybridDistributionStrategy(mpjMe, mpjSize, options, cpManager, getNumberOfThreads());
            TemporaryFileManager.synchronize(options.getAlignment(), options.getTree(),
                getNumberOfThreads());
        } else {
            strategy = new StaticDistributionStrategy(mpjMe, mpjSize, options);
        }
        strategy.addObserver(this);

        Model[] allModels = null;

        //For each model, for each distribution,... optimize the model and calculate some statistics:
        if (mpjMe == 0) {
            println("**********************************************************");
            //this is only for doing output prettier
            println("Observed number of invariant sites: " + ProtTestAlignment.calculateInvariableSites(options.getAlignment(), false));
            StringWriter sw = new StringWriter();
            ProtTestAlignment.printFrequencies(ProtTestAlignment.getFrequencies(options.getAlignment()), new PrintWriter(sw));
            sw.flush();
            println(sw.toString());
            println("**********************************************************");
            println("");

            allModels = strategy.distribute(arrayListModel, new ModelDistributionHeuristic());
        } else {
            strategy.request();
        }
        long startTime = strategy.getStartTime();
        long endTime = strategy.getEndTime();
        if (mpjRun) {

            long[] runtime = {endTime - startTime};
            long[] runtimes = new long[mpjSize];
            // gathering run times
            MPI.COMM_WORLD.Gather(runtime, 0, 1, MPI.LONG,
                    runtimes, 0, 1, MPI.LONG, 0);
            if (mpjMe == 0) {
                List<Model> sortedModels = new SingleModelCollection(allModels, options.getAlignment());
                Collections.sort(sortedModels, new AminoAcidModelNaturalComparator());
                for (Model model : sortedModels) {
                    println("");
                    model.printReport();
                    println("");
                }
                println("************************************************************");
                println("Date: " + (new Date()).toString());
                for (int i = 0; i < mpjSize; i++) {
                    println("Runtime processor " + i + ":  " + Utilities.arrangeRuntime(runtimes[i]));
                }
                println("Minimum runtime: " + Utilities.arrangeRuntime(Utilities.getMin(runtimes)));
                println("Maximum runtime: " + Utilities.arrangeRuntime(Utilities.getMax(runtimes)));
                println("Average runtime: " + Utilities.arrangeRuntime(Utilities.getAverage(runtimes)));
                println("");
                println("");
                flush();
            }
        } else {
            println("************************************************************");
            String runtimeStr = Utilities.calculateRuntime(startTime, endTime);
            println("Date   :  " + (new Date()).toString());
            println("Runtime:  " + runtimeStr);
            println("");
            println("");
        }

        return allModels;
    }
}
