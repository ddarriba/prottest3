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
package es.uvigo.darwin.prottest.util.collection;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.comparator.ModelDistributionHeuristic;
import java.util.Collections;

/**
 *
 * @author Diego Darriba
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
