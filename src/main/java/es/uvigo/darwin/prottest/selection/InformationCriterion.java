package es.uvigo.darwin.prottest.selection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import java.util.ArrayList;
import pal.alignment.Alignment;

/**
 * Generic implementation of an information criterion for model selection.
 * Model selection is used to model-averaging and estimate parameter importance
 * in an evolutionary context after computing likelihood values for every model.
 */
public abstract class InformationCriterion {

    /** The best model according the concrete criterion. */
    protected SelectionModel bestModel;
    /** The models including some information about the criterion. */
    protected List<SelectionModel> selectionModels;
    /** HashMap to quick find the selection models. */
    protected HashMap<Model, SelectionModel> hashModels;
    /** The confidence interval. */
    protected double confidenceInterval;
    /** The set of models that fall into the confidence interval. */
    protected List<SelectionModel> confidenceModels;
    /** The cumulative weight of the parameter. */
    protected double cumWeight;
    /** The overall alpha value. */
    protected double overallAlpha = 0.0;
    /** The overall invariant value. */
    protected double overallInv = 0.0;
    /** The overall alpha + invariant value. */
    protected double overallAlphaInv = 0.0;
    /** The overall invariant + alpha value. */
    protected double overallInvAlpha = 0.0;
    /** The alpha parameter importance. */
    protected double alphaImportance = 0.0;
    /** The invariant parameter importance. */
    protected double invImportance = 0.0;
    /** The alpha + invariant parameter importance. */
    protected double alphaInvImportance = 0.0;
    /** The +F parameter importance. */
    protected double fImportance = 0.0;
    /** The sample size. */
    protected double sampleSize;
    /** The alignment */
    protected Alignment alignment;

    /**
     * Gets the overall alpha value.
     * 
     * @return the overall alpha value
     */
    public double getOverallAlpha() {
        return overallAlpha;
    }

    /**
     * Gets the overall invariant value.
     * 
     * @return the overall invariant value
     */
    public double getOverallInv() {
        return overallInv;
    }

    /**
     * Gets the overall alpha + invariant value.
     * 
     * @return the overall alpha + invariant value
     */
    public double getOverallAlphaInv() {
        return overallAlphaInv;
    }

    /**
     * Gets the overall invariant + alpha value.
     * 
     * @return the overall invariant + alpha value
     */
    public double getOverallInvAlpha() {
        return overallInvAlpha;
    }

    /**
     * Gets the alpha parameter importance.
     * 
     * @return the alpha parameter importance
     */
    public double getAlphaImportance() {
        return alphaImportance;
    }

    /**
     * Gets the invariant parameter importance.
     * 
     * @return the invariant parameter importance
     */
    public double getInvImportance() {
        return invImportance;
    }

    /**
     * Gets the alpha + invariant parameter importance.
     * 
     * @return the alpha + invariant parameter importance
     */
    public double getAlphaInvImportance() {
        return alphaInvImportance;
    }

    /**
     * Gets the +F parameter importance.
     * 
     * @return the +F parameter importance
     */
    public double getFImportance() {
        return fImportance;
    }

    /**
     * Gets the sample size.
     * 
     * @return the sample size
     */
    public double getSampleSize() {
        return sampleSize;
    }

    /**
     * Gets the confidence interval value
     * 
     * @return the confidence interval
     */
    public double getConfidenceInterval() {
        return confidenceInterval;
    }

    /**
     * Gets the list of selection models.
     * 
     * @return the list of selection models
     */
    public List<SelectionModel> getSelectionModels() {
        return selectionModels;
    }

    /**
     * Instantiates a new model selection criterion.
     * 
     * @param models the models to compute
     * @param confidenceInterval the confidence interval
     * @param sampleSize the sample size if different of the default
     */
    public InformationCriterion(ModelCollection models,
            double confidenceInterval, double sampleSize) {
        this.alignment = models.getAlignment();
        this.sampleSize = sampleSize;
        int numberOfModels = models.size();
        this.confidenceInterval = confidenceInterval;
        this.hashModels = new HashMap<Model, SelectionModel>(numberOfModels);
        this.confidenceModels = new ArrayList<SelectionModel>();
        this.selectionModels = getSelectionModels(models);
        Collections.sort(selectionModels);
        compute();
    }

    /**
     * Gets the list of selection models.
     * 
     * @param models the models to calculate the information criterion
     * 
     * @return the list of models having the information about the concrete criterion
     */
    protected abstract List<SelectionModel> getSelectionModels(List<Model> models);

    /**
     * Computes the delta criterion, the weight and the cumulative weight of the criterion
     * over the model collection.
     */
    private void compute() {
        double minValue, sumExp, cum;

        bestModel = selectionModels.get(0);
        minValue = bestModel.getValue();

        // Calculate differences
        sumExp = 0;
        for (SelectionModel model : selectionModels) {
            model.setDeltaValue(
                    model.getValue() - minValue);
            sumExp += Math.exp(-0.5 * model.getDeltaValue());
        }

        // Calculate weights
        for (SelectionModel model : selectionModels) {
            if (model.getDeltaValue() > 1000) {
                model.setWeightValue(0.0);
            } else {
                model.setWeightValue(
                        Math.exp(-0.5 * model.getDeltaValue()) / sumExp);
            }
        }

        // Calculate cumulative weights, overalls and parameters importance
        cum = 0.0;
        alphaImportance = 0.0;
        invImportance = 0.0;
        alphaInvImportance = 0.0;
        fImportance = 0.0;
        overallAlpha = 0.0;
        overallInv = 0.0;
        overallAlphaInv = 0.0;
        overallInvAlpha = 0.0;
        for (SelectionModel model : selectionModels) {
            cum += model.getWeightValue();
            model.setCumulativeWeightValue(cum);

            if (model.getModel().isGamma() && model.getModel().isInv()) {
                alphaInvImportance += model.getWeightValue();
                overallAlphaInv += model.getWeightValue() * model.getModel().getAlpha();
                overallInvAlpha += model.getWeightValue() * model.getModel().getInv();
            } else if (model.getModel().isGamma()) {
                alphaImportance += model.getWeightValue();
                overallAlpha += model.getWeightValue() * model.getModel().getAlpha();
            } else if (model.getModel().isInv()) {
                invImportance += model.getWeightValue();
                overallInv += model.getWeightValue() * model.getModel().getInv();
            }
            if (model.getModel().isPlusF()) {
                fImportance += model.getWeightValue();
            }
        }

        overallAlpha /= alphaImportance;
        overallInv /= invImportance;
        overallInvAlpha /= alphaInvImportance;
        overallAlphaInv /= alphaInvImportance;

        // confidence interval
        buildConfidenceInterval();
    }

    /**
     * Builds the confidence interval of selected models
     * and their cumulative weight
     * 
     * The model that just passed the confidence will be or not
     * in the interval by chance (see below).
     */
    public void buildConfidenceInterval() {
        confidenceModels = new ArrayList<SelectionModel>();

        // first construct the confidence interval for models
        if (confidenceInterval >= 1.0) {

            for (SelectionModel model : selectionModels) {
                model.setInConfidenceInterval(true);
                confidenceModels.add(model);
            }
            cumWeight = 1.0;

        } else {
            cumWeight = 0.0;
            SelectionModel lastModel = null;
            for (SelectionModel model : selectionModels) {
                if (model.getCumulativeWeightValue() <= confidenceInterval) {
                    confidenceModels.add(model);
                    cumWeight += model.getWeightValue();
                } else {
                    lastModel = model;
                    break;
                }
            }

            // lets decide whether the model that just passed the confidence
            // interval should be included (suggested by John Huelsenbeck)
            double probOut = (lastModel.getCumulativeWeightValue() - confidenceInterval) / lastModel.getWeightValue();
            double probIn = 1.0 - probOut;
            Random generator = new Random();
            double randomNumber = generator.nextDouble();
            if (randomNumber <= probIn) {
                lastModel.setInConfidenceInterval(true);
                confidenceModels.add(lastModel);
                cumWeight += lastModel.getWeightValue();
            } else {
                lastModel.setInConfidenceInterval(false);
            }
        }
    }

    /**
     * All iterator.
     * 
     * @return the iterator< selection model>
     */
    public Iterator<SelectionModel> allIterator() {
        return selectionModels.iterator();
    }

    /**
     * Confidence collection.
     * 
     * @return the collection of confidence selection models
     */
    public Collection<SelectionModel> getConfidenceModels() {
        return confidenceModels;
    }

    /**
     * Gets the model collection.
     * 
     * @return the model collection
     */
    public ModelCollection getModelCollection() {
        ModelCollection models = new SingleModelCollection(alignment);
        for (SelectionModel model : confidenceModels) {
            models.add(model.getModel());
        }
        return models;
    }

    /**
     * Gets the SelectionModel in an specific position of the
     * collection, sorted by this criterion value.
     * 
     * @param index the index
     * 
     * @return the SelectionModel 
     */
    public SelectionModel get(int index) {
        return selectionModels.get(index);
    }

    /**
     * Gets the SelectionModel filled with this criterion data that
     * wraps the specified model.
     * @see SelectionModel
     * 
     * @param model the model contained in the SelectionModel
     * 
     * @return the SelectionModel that wraps the model
     * 
     */
    public SelectionModel get(Model model) {
        return hashModels.get(model);
    }

    /**
     * Gets the best model according with the concrete criterion.
     * (i.e. the model with the lowest criterion value)
     * @see SelectionModel#getValue()
     * 
     * @return the best model
     */
    public Model getBestModel() {
        return bestModel.getModel();
    }

    /**
     * Gets the criterion name.
     * 
     * @return the criterion name
     */
    public abstract String getCriterionName();
}
