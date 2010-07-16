/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.darwin.prottest.util.collection;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.comparator.ModelDistributionHeuristic;
import java.util.Collections;

/**
 *
 * @author diego
 */
public class ParallelModelQueue {

    /** List of models */
    private ModelCollection sortedModelList;
    /** Number of threads to schedule */
    private int maxAvThreads;
    /** Available threads */
    private int availableThreads;

    public ParallelModelQueue(int maxAvThreads, ModelCollection modelList) {

        this.maxAvThreads = maxAvThreads;
        this.availableThreads = maxAvThreads;

        this.sortedModelList = modelList;
        Collections.sort(this.sortedModelList, new ModelDistributionHeuristic());

    }

    public synchronized Model getNextModel() {

        if (availableThreads < 1) {
            return null;
        }

        Model nextModel = null;
        for (Model model : sortedModelList) {

            if (getNumberOfThreads(model) <= availableThreads) {
                nextModel = model;
                break;
            }

        }
        availableThreads -= getNumberOfThreads(nextModel);
        sortedModelList.remove(nextModel);
        return nextModel;

    }

    private int getNumberOfThreads(Model model) {

        int numberOfProcessors;
        if (!model.isGamma()) {
            numberOfProcessors = 1;
        } else if (!model.isInv()) {
            numberOfProcessors = 2;
        } else {
            numberOfProcessors = 4;
        }
        return Math.min(numberOfProcessors, maxAvThreads);

    }
}
