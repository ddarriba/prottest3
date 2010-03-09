package es.uvigo.darwin.prottest.selection.printer;

import java.io.PrintWriter;
import java.util.Collections;

import pal.tree.TreeUtils;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.StatFramework;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.comparator.LKComparator;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

// TODO: Auto-generated Javadoc
/**
 * The Class PrintFramework.
 */
public abstract class PrintFramework {
	
	private static final double NO_SAMPLE_SIZE = 0.0;
	
	/** The information criterion. */
	private InformationCriterion informationCriterion;
	
	/**
	 * Gets the information criterion.
	 * 
	 * @return the information criterion
	 */
	protected InformationCriterion getInformationCriterion() {
		return informationCriterion;
	}
	
	/**
	 * Instantiates a new prints the framework.
	 * 
	 * @param informationCriterion the information criterion
	 */
	public PrintFramework(InformationCriterion informationCriterion) {
		this.informationCriterion = informationCriterion;
	}

	/**
	 * Prints the models sorted.
	 * 
	 * @param out the out
	 */
	public final void printModelsSorted(PrintWriter out) {
		
		int fieldLength = 15;
		int numberOfFields = 5;
		int lineLength = fieldLength * numberOfFields;

		Model bestModel = informationCriterion.getBestModel();
		ModelCollection models = informationCriterion.getModelCollection();
		
		String columns[] = new String[5];
		columns[0] = "Model";
		columns[1] = "delta"+ informationCriterion.getCriterionName();
		columns[2] = informationCriterion.getCriterionName();
		columns[3] = informationCriterion.getCriterionName() + "w";
		columns[4] = "-lnL";
		
		out.println(ProtTestFormattedOutput.space(lineLength, '*'));
		out.println("Best model according to "+informationCriterion.getCriterionName()+": " + bestModel.getModelName());
		out.println(ProtTestFormattedOutput.space(lineLength, '*'));
		for (int i = 0; i < numberOfFields; i++) {
			out.print(columns[i]);
			out.print(ProtTestFormattedOutput.space(fieldLength - columns[i].length(), ' '));
		}
		out.println("");
		out.println(ProtTestFormattedOutput.space(lineLength, '-'));
		for(Model model : models) {
			int len = 0;
			
			out.print(model.getModelName());
			out.print(ProtTestFormattedOutput.space(fieldLength-model.getModelName().length(), ' '));

			len = ProtTestFormattedOutput.displayDecimal(out, informationCriterion.get(model).getDeltaValue(), 2); 
			out.print(ProtTestFormattedOutput.space(fieldLength - len, ' '));
			len = ProtTestFormattedOutput.displayDecimal(out, informationCriterion.get(model).getValue(), 2);
			out.print(ProtTestFormattedOutput.space(fieldLength - len, ' '));
			len = ProtTestFormattedOutput.displayDecimal(out, informationCriterion.get(model).getWeightValue(), 2);
			out.print(ProtTestFormattedOutput.space(fieldLength - len, ' '));
			len = ProtTestFormattedOutput.displayDecimal(out, model.getLk(), 2);
			out.print(ProtTestFormattedOutput.space(fieldLength - len, ' '));

			out.println("");
		}
		
			out.println(ProtTestFormattedOutput.space(lineLength, '-'));
//			out.println(" *: models sorted according to this column"                  );
			out.println(ProtTestFormattedOutput.space(lineLength, '-'));
			
			printRelativeImportance(out);
			
			printModelAveragedEstimation(out);
		
	}
	
	/**
	 * Display ascii tree.
	 * 
	 * @param out the out
	 */
	public void displayASCIITree(PrintWriter out) {
		out.println("");
		out.println("************************************************************");
		out.println("Tree according to best model (" + informationCriterion.getBestModel().getModelName() + ")");
		TreeUtils.report(informationCriterion.getBestModel().getTree(), out);
		out.println("************************************************************");
		out.println("");
	}
	
	/**
	 * Display newick tree.
	 * 
	 * @param out the out
	 */
	public void displayNewickTree(PrintWriter out) {
		out.println("");
		out.println("************************************************************");
		out.println("Tree according to best model (" + informationCriterion.getBestModel().getModelName() + ")");
		TreeUtils.printNH   (informationCriterion.getBestModel().getTree(), out);
		out.println("************************************************************");
		out.println("");
	}
	
	/**
	 * Prints the frameworks comparison.
	 * 
	 * @param out the out
	 */
	public void printFrameworksComparison(PrintWriter out) {
		
		ModelCollection models = informationCriterion.getModelCollection();
		
		double[] aic   = new double[models.size()];
		double[] aicc1 = new double[models.size()];
		double[] aicc2 = new double[models.size()];
		double[] aicc3 = new double[models.size()];
		double[] bic1  = new double[models.size()];
		double[] bic2  = new double[models.size()];
		double[] bic3  = new double[models.size()];
		String[] modelNames  = new String[models.size()];
		double   sampleSize1, sampleSize2, sampleSize3;

		sampleSize1 = ProtTestAlignment.calculateSampleSize(models.getAlignment(), ApplicationGlobals.SIZEMODE_ALIGNMENT, NO_SAMPLE_SIZE);
		sampleSize2 = ProtTestAlignment.calculateSampleSize(models.getAlignment(), ApplicationGlobals.SIZEMODE_SHANNON, NO_SAMPLE_SIZE);
		sampleSize3 = ProtTestAlignment.calculateSampleSize(models.getAlignment(), ApplicationGlobals.SIZEMODE_SHANNON_NxL, NO_SAMPLE_SIZE);
		
		InformationCriterion aicS1 = new AIC(models, 1.0, sampleSize1);
		InformationCriterion aiccS1 = new AICc(models, 1.0, sampleSize1);
		InformationCriterion aiccS2 = new AICc(models, 1.0, sampleSize2);
		InformationCriterion aiccS3 = new AICc(models, 1.0, sampleSize3);
		InformationCriterion bicS1 = new BIC(models, 1.0, sampleSize1);
		InformationCriterion bicS2 = new BIC(models, 1.0, sampleSize2);
		InformationCriterion bicS3 = new BIC(models, 1.0, sampleSize3);
		Collections.sort(models, new LKComparator());
		for(int i = 0; i < models.size(); i++) {
			Model model = models.get(i);
			modelNames [i]  = model.getModelName();
			
			aicc1[i]  = aiccS1.get(model).getValue(); 
			bic1 [i]  = bicS1.get(model).getValue(); 
			aic  [i]  = aicS1.get(model).getValue(); 

			aicc2[i]  = aiccS2.get(model).getValue(); 
			bic2 [i]  = bicS2.get(model).getValue(); 

			aicc3[i]  = aiccS3.get(model).getValue(); 
			bic3 [i]  = bicS3.get(model).getValue(); 

		}
		
		StatFramework aicF, aicc1F, aicc2F, aicc3F, bic1F, bic2F, bic3F;
		aicF   = new StatFramework(aicS1, "AIC",  "AIC");
		aicc1F = new StatFramework(aiccS1, "AICc(1)", "second-order AIC (sample size: "+ApplicationGlobals.SIZE_MODES[ApplicationGlobals.SIZEMODE_ALIGNMENT]+")");
		aicc2F = new StatFramework(aiccS2, "AICc(2)", "second-order AIC (sample size: "+ApplicationGlobals.SIZE_MODES[ApplicationGlobals.SIZEMODE_SHANNON]+")");
		aicc3F = new StatFramework(aiccS3, "AICc(3)", "second-order AIC (sample size: "+ApplicationGlobals.SIZE_MODES[ApplicationGlobals.SIZEMODE_SHANNON_NxL]+")");
		bic1F  = new StatFramework(bicS1, "BIC(1)",  "BIC (sample size: "+ApplicationGlobals.SIZE_MODES[ApplicationGlobals.SIZEMODE_ALIGNMENT]+")");
		bic2F  = new StatFramework(bicS2, "BIC(2)",  "BIC (sample size: "+ApplicationGlobals.SIZE_MODES[ApplicationGlobals.SIZEMODE_SHANNON]+")");
		bic3F  = new StatFramework(bicS3, "BIC(3)",  "BIC (sample size: "+ApplicationGlobals.SIZE_MODES[ApplicationGlobals.SIZEMODE_SHANNON_NxL]+")");
		
		//Hala, ahora a imprimir:
		out.println(ProtTestFormattedOutput.space(100, '-'));
		out.println("Table: Weights(Ranking) of the candidate models under the different frameworks");
		out.println(ProtTestFormattedOutput.space(100, '-'));
		out.println("model          AIC         AICc-1      AICc-2      AICc-3      BIC-1       BIC-2       BIC-3");
		String model__, tmp;
		for(int i = 0; i < models.size(); i++) {
			model__ = aicF.getModelName(i);
			out.print(model__ + ProtTestFormattedOutput.space(15-model__.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getWeight(model__), 2)   + "(" + aicF.getRanking(model__)   + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getWeight(model__), 2) + "(" + aicc1F.getRanking(model__) + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getWeight(model__), 2) + "(" + aicc2F.getRanking(model__) + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getWeight(model__), 2) + "(" + aicc3F.getRanking(model__) + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getWeight(model__), 2)  + "(" + bic1F.getRanking(model__)  + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getWeight(model__), 2)  + "(" + bic2F.getRanking(model__)  + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getWeight(model__), 2)  + "(" + bic3F.getRanking(model__)  + ")";
			out.print(ProtTestFormattedOutput.space(0-tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			out.println("");
		}
		out.println(ProtTestFormattedOutput.space(100, '-'));
		out.println("Relative importance of");
		out.println("parameters     AIC         AICc-1      AICc-2      AICc-3      BIC-1       BIC-2       BIC-3");
		out.print  ("+G" + ProtTestFormattedOutput.space(13, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getAlphaImp(),       2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getAlphaImp(),     2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getAlphaImp(),     2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getAlphaImp(),     2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getAlphaImp(),      2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getAlphaImp(),      2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getAlphaImp(),      2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
		out.println("");
		out.print  ("+I" + ProtTestFormattedOutput.space(13, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getInvImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getInvImp(),       2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getInvImp(),       2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getInvImp(),       2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getInvImp(),        2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getInvImp(),        2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getInvImp(),        2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
		out.println("");
		out.print  ("+I+G" + ProtTestFormattedOutput.space(11, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getGIImp(),          2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getGIImp(),        2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getGIImp(),        2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getGIImp(),        2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getGIImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getGIImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getGIImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
		out.println("");
		out.print  ("+F" + ProtTestFormattedOutput.space(13, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getFImp(),           2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getFImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getFImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getFImp(),         2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getFImp(),          2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getFImp(),          2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getFImp(),          2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
		out.println("");
		out.println(ProtTestFormattedOutput.space(100, '-'));
		out.println("Model-averaged estimate of");
		out.println("parameters     AIC         AICc-1      AICc-2      AICc-3      BIC-1       BIC-2       BIC-3");
		out.print  ("alpha (+G)" + ProtTestFormattedOutput.space(5, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallAlpha(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallAlpha(), 2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallAlpha(), 2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallAlpha(), 2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallAlpha(),  2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallAlpha(),  2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallAlpha(),  2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
		out.println("");
		out.print  ("p-inv (+I)" + ProtTestFormattedOutput.space(5, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallInv(),     2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallInv(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallInv(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallInv(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallInv(),    2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallInv(),    2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallInv(),    2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(), ' '));
		out.println("");
		out.print  ("alpha (+I+G)" + ProtTestFormattedOutput.space(3, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallAlphaGI(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallAlphaGI(), 2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallAlphaGI(), 2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallAlphaGI(), 2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallAlphaGI(),  2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallAlphaGI(),  2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallAlphaGI(),  2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
		out.println("");
		out.print  ("p-inv (+I+G)" + ProtTestFormattedOutput.space(3, ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallInvGI(),     2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallInvGI(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallInvGI(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallInvGI(),   2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallInvGI(),    2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallInvGI(),    2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallInvGI(),    2);
			out.print  (tmp + ProtTestFormattedOutput.space(12-tmp.length(),   ' '));
		out.println("");
		out.println(ProtTestFormattedOutput.space(100, '-'));
		out.println("AIC   : Akaike Information Criterion framework.");
		out.println("AICc-x: Second-Order Akaike framework.");
		out.println("BIC-x : Bayesian Information Criterion framework.");
		out.println("AICc/BIC-1: sample size as: number of sites in the alignment                           (" + sampleSize1 + ")");
		out.println("AICc/BIC-2: sample size as: Sum of position's Shannon Entropy over the whole alignment (" + ProtTestFormattedOutput.getDecimalString(sampleSize2,1) + ")");
		out.println("AICc/BIC-3: sample size as: align. length x num sequences x averaged (0-1)Sh. Entropy  (" + ProtTestFormattedOutput.getDecimalString(sampleSize3,1) + ")");
		out.println(ProtTestFormattedOutput.space(100, '-'));
		out.println("");
	}
	
	/**
	 * Prints the relative importance.
	 * 
	 * @param out the out
	 */
	abstract void printRelativeImportance(PrintWriter out);
	
	/**
	 * Prints the model averaged estimation.
	 * 
	 * @param out the out
	 */
	abstract void printModelAveragedEstimation(PrintWriter out);
	
	
}
