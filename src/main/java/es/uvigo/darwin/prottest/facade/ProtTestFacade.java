/*
 * 
 */
package es.uvigo.darwin.prottest.facade;

import java.io.IOException;
import java.io.PrintWriter;

import pal.alignment.Alignment;
import pal.alignment.AlignmentParseException;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO;
import es.uvigo.darwin.prottest.facade.util.SelectionChunk;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.util.exception.AlignmentFormatException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;

/**
 * The facade to provide ProtTest functionality to external objects.
 */
public interface ProtTestFacade extends ModelUpdaterObserver {
	
//	**********************************************************
//				   ALIGNMENT ANALYSIS SERVICES
//	**********************************************************
	
	/**
	 * Starts the analysis of the alignment having the application options
	 * given in the command line. It will also print results as given in the
	 * application options.
	 * 
	 * @param options the execution options 
	 * 
	 * @return set of optimized models
	 */
	public Model[] startAnalysis(ApplicationOptions options);

//	**********************************************************
//	    			RESULT ANALYSIS SERVICES
//	**********************************************************
	
	/**
	 * Prints the models sorted according to the selected information criterion.
	 * 
	 * @param informationCriterion the information criterion to sort the models
	 * @param outputWriter the output writer where the output will be written
	 */
	public void printModelsSorted(InformationCriterion informationCriterion,
			PrintWriter outputWriter);
	
	public SelectionChunk computeInformationCriterion(Alignment alignment, Model[] models, 
			int criterion, int sampleSizeMode, double sampleSize,
			double confidenceInterval);
	

		
//	**********************************************************
//	                   INPUT SERVICES
//	**********************************************************

	/**
	 * Read alignment.
	 * 
	 * @param out the out
	 * @param filename the filename
	 * @param debug the debug
	 * 
	 * @return the alignment
	 * 
	 * @throws AlignmentParseException the alignment parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Alignment readAlignment (PrintWriter out, String filename, boolean debug) 
		throws 	AlignmentFormatException;
	
	/**
	 * Read alignment.
	 * 
	 * @param out the out
	 * @param filename the filename
	 * @param debug the debug
	 * 
	 * @return the alignment
	 * 
	 * @throws AlignmentParseException the alignment parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Tree readTree (PrintWriter out, String filename, boolean debug) 
		throws 	TreeFormatException;
	
//	**********************************************************
//	   					 MISC SERVICES
//	**********************************************************
	
	public ApplicationOptions configure(ProtTestParameterVO parameters)
	throws IOException, AlignmentParseException, ProtTestInternalException;

//	public void configure(ApplicationOptions options);

//	public void reportOptions(PrintWriter out);
	
	public void addObserver(ModelUpdaterObserver o);
	
	public void update(ObservableModelUpdater o, Model model, ApplicationOptions options);
	
	public int getNumberOfThreads();
	
	public void setNumberOfThreads(int numThreads);
}
