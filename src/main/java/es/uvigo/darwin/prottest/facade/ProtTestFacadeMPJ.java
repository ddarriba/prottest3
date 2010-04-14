package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import pal.tree.Tree;

import mpi.MPI;
import es.uvigo.darwin.prottest.facade.strategy.DistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.DynamicDistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.ImprovedDynamicDistributionStrategy;
import es.uvigo.darwin.prottest.facade.strategy.StaticDistributionStrategy;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
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
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

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
	
	private CheckPointManager cpManager;
	
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
        if (mpjMe == 0)
            t[0] = super.calculateBionjJTT(options);
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
                                        ((ProtTestStatus)cpManager.getLastCheckpoint()).getModels(),
                                        options.getAlignment());
			} else {
				arrayListModel = new SingleModelCollection(options.getAlignment());
				Properties modelProperties = new Properties();
				if (options.isPlusF())
					modelProperties.setProperty(Model.PROP_PLUS_F, "true");
				arrayListModel.addModelCartesian(options.getMatrices(), options.getDistributions(), modelProperties,
						options.getAlignment(), options.getTree(), options.ncat);
			}
		}
		String strategyProp = ApplicationGlobals.properties.getProperty("parallel_strategy", "static");
                
		if (strategyProp.equals("dynamic"))
			strategy = new DynamicDistributionStrategy(mpjMe, mpjSize, options, cpManager);
		else if (strategyProp.equals("dynamic_improved"))
			strategy = new ImprovedDynamicDistributionStrategy(mpjMe, mpjSize, options, cpManager);
		else
			strategy = new StaticDistributionStrategy(mpjMe, mpjSize, options);
		strategy.addObserver(this);
		
		PrintWriter out = options.getPrinter().getOutputWriter();
		PrintWriter err = options.getPrinter().getErrorWriter();
		Model[] allModels = null;
		
		//For each model, for each distribution,... optimize the model and calculate some statistics:
		if (mpjMe == 0) {
			out.println("**********************************************************");
			//this is only for doing output prettier
			if(options.getSampleSizeMode() == ApplicationGlobals.SIZEMODE_SHANNON || options.getSampleSizeMode() == ApplicationGlobals.SIZEMODE_SHANNON_NxL) {
				double tmpSampleSize = ProtTestAlignment.calculateShannonSampleSize(options.getAlignment(),options.getSampleSizeMode(),true);
				err.println("Shannon entropy based sample size: "+ ProtTestFormattedOutput.getDecimalString(tmpSampleSize,2));
			}
			out.println("Observed number of invariant sites: " + ProtTestAlignment.calculateInvariableSites(options.getAlignment(), false));
			ProtTestAlignment.printFrequencies(ProtTestAlignment.getFrequencies(options.getAlignment()), out );
			out.println("**********************************************************");
			out.println("");
			
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
					out.println("");
					model.printReport(out, options.isDebug());
					out.println("");
				}
				out.println("************************************************************");
				out.println("Date: " +   (new Date()).toString());
				for (int i = 0; i < mpjSize; i++)
					out.println("Runtime processor "+ i +":  " +   Utilities.arrangeRuntime(runtimes[i]));
				out.println("Minimum runtime: " + Utilities.arrangeRuntime(Utilities.getMin(runtimes)));
				out.println("Maximum runtime: " + Utilities.arrangeRuntime(Utilities.getMax(runtimes)));
				out.println("Average runtime: " + Utilities.arrangeRuntime(Utilities.getAverage(runtimes)));
				out.println("");			out.println("");
				out.flush();
			}
		}
		else {
			out.println("************************************************************");
			String runtimeStr = Utilities.calculateRuntime(startTime, endTime);
			out.println("Date   :  " +   (new Date()).toString());
			out.println("Runtime:  " +   runtimeStr);
			out.println("");			out.println("");
		}
		
		return allModels;
	}
}
