package es.uvigo.darwin.prottest.facade.util;

import java.util.Collection;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;

public class SelectionChunk {

	public static final int AIC = 1;
	public static final int BIC = 2;
	public static final int AICC = 3;
	public static final int DT = 4;
	public static final int LNL = 5;
	
	private InformationCriterion informationCriterion;
        
        public InformationCriterion getInformationCriterion() {
            return informationCriterion;
        }

	public SelectionChunk(InformationCriterion informationCriterion) {
		this.informationCriterion = informationCriterion;
	}
	
	public double getValue(Model model) {
		return informationCriterion.get(model).getValue();
	}
	
	public double getDeltaValue(Model model) {
		return informationCriterion.get(model).getDeltaValue();
	}
	
	public double getWeightValue(Model model) {
		return informationCriterion.get(model).getWeightValue();
	}
	
	public double getOverallAlpha() {
		return informationCriterion.getOverallAlpha();
	}
	
	public double getOverallAlphaInv() {
		return informationCriterion.getOverallAlphaInv();
	}
	
	public double getOverallInv() {
		return informationCriterion.getOverallInv();
	}
	
	public double getOverallInvAlpha() {
		return informationCriterion.getOverallInvAlpha();
	}
	
	public double getAlphaImportance() {
		return informationCriterion.getAlphaImportance();
	}
	
	public double getInvImportance() {
		return informationCriterion.getInvImportance();
	}
	
	public double getAlphaInvImportance() {
		return informationCriterion.getAlphaInvImportance();
	}
	
	public double getFImportance() {
		return informationCriterion.getFImportance();
	}
	
	public Collection<SelectionModel> getConfidenceModels() {
		return informationCriterion.getConfidenceModels();
	}
}
