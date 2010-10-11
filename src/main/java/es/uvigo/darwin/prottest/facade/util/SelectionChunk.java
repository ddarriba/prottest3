package es.uvigo.darwin.prottest.facade.util;

import java.util.Collection;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;

/**
 * A value object to calculate information criteria. It works as
 * a wrapper for the information criteria class.
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
 */
public class SelectionChunk {

    /**
     * Akaike Information Criteiron constant.
     */
    public static final int AIC = 1;
    /**
     * Bayesian Information Criteiron constant.
     */
    public static final int BIC = 2;
    /**
     * Corrected Akaike Information Criteiron constant.
     */
    public static final int AICC = 3;
    /**
     * Decision Theory Criteiron constant.
     */
    public static final int DT = 4;
    /**
     * log Likelihood Criterion constant.
     */
    public static final int LNL = 5;
    /**
     * The inner information criterion
     */
    private InformationCriterion informationCriterion;

    /**
     * Gets the inner information criterion
     * 
     * @return the information criterion
     */
    public InformationCriterion getInformationCriterion() {
        return informationCriterion;
    }

    /**
     * Instantiates a new Selection Chunk.
     * 
     * @param informationCriterion the inner information criterion
     */
    public SelectionChunk(InformationCriterion informationCriterion) {
        this.informationCriterion = informationCriterion;
    }

    public boolean existGammaModels() {
        return informationCriterion.isExistGammaModels();
    }

    public boolean existInvModels() {
        return informationCriterion.isExistInvModels();
    }

    public boolean existGammaInvModels() {
        return informationCriterion.isExistGammaInvModels();
    }

    public boolean existFModels() {
        return informationCriterion.isExistFModels();
    }
    
    /**
     * Gets the criterion value of a specific substitution model.
     * 
     * @param model the substitution model
     * 
     * @return the criterion value
     */
    public double getValue(Model model) {
        return informationCriterion.get(model).getValue();
    }

    /**
     * Gets the incremental criterion value of a specific substitution model.
     * 
     * @param model the substitution model
     * 
     * @return the incremental criterion value
     */
    public double getDeltaValue(Model model) {
        return informationCriterion.get(model).getDeltaValue();
    }

    /**
     * Gets the relative weight of a specific substitution model.
     * 
     * @param model the substitution model
     * 
     * @return the relative weight
     */
    public double getWeightValue(Model model) {
        return informationCriterion.get(model).getWeightValue();
    }

    /**
     * Gets the overall importance of the alpha parameter.
     * 
     * @return the overall importance of the +G models.
     */
    public double getOverallAlpha() {
        return informationCriterion.getOverallAlpha();
    }

    /**
     * Gets the overall importance of the alpha parameter on +I+G models..
     * 
     * @return the overall importance.
     */
    public double getOverallAlphaInv() {
        return informationCriterion.getOverallAlphaInv();
    }

    /**
     * Gets the overall importance of the proportion of invariant sites
     * parameter.
     * 
     * @return the overall importance of the +I models.
     */
    public double getOverallInv() {
        return informationCriterion.getOverallInv();
    }

    /**
     * Gets the overall importance of the proportion of invariant sites 
     * parameter on +I+G models.
     * 
     * @return the overall importance
     */
    public double getOverallInvAlpha() {
        return informationCriterion.getOverallInvAlpha();
    }

    /**
     * Gets the relative importance of +G parameter.
     * 
     * @return the relative importance
     */
    public double getAlphaImportance() {
        return informationCriterion.getAlphaImportance();
    }

    /**
     * Gets the relative importance of +I parameter.
     * 
     * @return the relative importance
     */
    public double getInvImportance() {
        return informationCriterion.getInvImportance();
    }

    /**
     * Gets the relative importance of +I+G parameter.
     * 
     * @return the relative importance
     */
    public double getAlphaInvImportance() {
        return informationCriterion.getAlphaInvImportance();
    }

    /**
     * Gets the relative importance of empirical frequencies parameter.
     * 
     * @return the relative importance
     */
    public double getFImportance() {
        return informationCriterion.getFImportance();
    }

    /**
     * Gets the models which belong to the confidence interval.
     * 
     * @return the models in the confidence interval.
     */
    public Collection<SelectionModel> getConfidenceModels() {
        return informationCriterion.getConfidenceModels();
    }
}
