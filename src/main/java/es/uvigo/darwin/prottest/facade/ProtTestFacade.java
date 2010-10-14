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

import java.io.IOException;
import java.io.PrintWriter;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO;
import es.uvigo.darwin.prottest.facade.util.SelectionChunk;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ModelUpdaterObserver;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import java.io.FileNotFoundException;

/**
 * Declaration of general services of ProtTest-HPC
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
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
     */
    public void printModelsSorted(InformationCriterion informationCriterion);

    /**
     * Calculates a Information Criterion wrapped into a Selection chunk object,
     * which is a value-object
     * 
     * @param alignment the alignment.
     * @param models the optimized models .
     * @param criterion the Information Criterion Constant.
     * @param sampleSizeMode the sample size mode.
     * @param sampleSize the sample size (if custom).
     * @param confidenceInterval the confidence interval.
     */
    public SelectionChunk computeInformationCriterion(Alignment alignment, Model[] models,
            int criterion, int sampleSizeMode, double sampleSize,
            double confidenceInterval);

//	**********************************************************
//	                   INPUT SERVICES
//	**********************************************************
    /**
     * Read alignment.
     * 
     * @param filename the filename
     * @param debug the debug
     * 
     * @return the alignment
     * 
     * @throws AlignmentParseException the alignment parse exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Alignment readAlignment(String filename, boolean debug)
            throws AlignmentParseException, FileNotFoundException, IOException;

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
    public Tree readTree(PrintWriter out, String filename, boolean debug)
            throws TreeFormatException, FileNotFoundException, IOException;

//	**********************************************************
//	   					 MISC SERVICES
//	**********************************************************
    public ApplicationOptions configure(ProtTestParameterVO parameters)
            throws IOException, AlignmentParseException, ProtTestInternalException;

    public void addObserver(ModelUpdaterObserver o);

    public void update(ObservableModelUpdater o, Model model, ApplicationOptions options);

    public int getNumberOfThreads();

    public void setNumberOfThreads(int numThreads);
}
