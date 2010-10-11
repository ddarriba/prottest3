package es.uvigo.darwin.prottest.selection.printer;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import java.util.Collections;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.StatFramework;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.comparator.LKComparator;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

/**
 * Utility to print application data into loggers.
 */
public abstract class PrintFramework {

    /** Constant sample size when it's not user defined. Just for visibility */
    private static final double NO_SAMPLE_SIZE = 0.0;

    /**
     * Prints the models sorted by a concrete information criterion.
     */
    public final void printModelsSorted(InformationCriterion informationCriterion) {

        int fieldLength = 15;
        int numberOfFields = 5;
        int lineLength = fieldLength * numberOfFields;

        Model bestModel = informationCriterion.getBestModel();
        ModelCollection models = informationCriterion.getModelCollection();

        String columns[] = new String[5];
        columns[0] = "Model";
        columns[1] = "delta" + informationCriterion.getCriterionName();
        columns[2] = informationCriterion.getCriterionName();
        columns[3] = informationCriterion.getCriterionName() + "w";
        columns[4] = "-lnL";

        println("");
        println(ProtTestFormattedOutput.space(lineLength, '*'));
        println("Best model according to " + informationCriterion.getCriterionName() + ": " + bestModel.getModelName());
        println("Sample Size:         " + informationCriterion.getSampleSize());
        double confPercent = informationCriterion.getConfidenceInterval() * 100;
        println("Confidence Interval: " + confPercent);
        println(ProtTestFormattedOutput.space(lineLength, '*'));
        for (int i = 0; i < numberOfFields; i++) {
            print(columns[i]);
            print(ProtTestFormattedOutput.space(fieldLength - columns[i].length(), ' '));
        }
        println("");
        println(ProtTestFormattedOutput.space(lineLength, '-'));
        for (Model model : models) {
            print(model.getModelName());
            print(ProtTestFormattedOutput.space(fieldLength - model.getModelName().length(), ' '));

            String decimal;

            decimal = ProtTestFormattedOutput.getDecimalString(informationCriterion.get(model).getDeltaValue(), 2);
            print(decimal);
            print(ProtTestFormattedOutput.space(fieldLength - decimal.length(), ' '));
            decimal = ProtTestFormattedOutput.getDecimalString(informationCriterion.get(model).getValue(), 2);
            print(decimal);
            print(ProtTestFormattedOutput.space(fieldLength - decimal.length(), ' '));
            decimal = ProtTestFormattedOutput.getDecimalString(informationCriterion.get(model).getWeightValue(), 2);
            print(decimal);
            print(ProtTestFormattedOutput.space(fieldLength - decimal.length(), ' '));
            decimal = ProtTestFormattedOutput.getDecimalString(model.getLk(), 2);
            print(decimal);
            print(ProtTestFormattedOutput.space(fieldLength - decimal.length(), ' '));

            println("");
        }

        println(ProtTestFormattedOutput.space(lineLength, '-'));

        println(ProtTestFormattedOutput.space(lineLength, '-'));

        printRelativeImportance(informationCriterion);

        printModelAveragedEstimation(informationCriterion);

    }

    protected static void print(String text) {
        ProtTestLogger.getDefaultLogger().info(text);
    }

    protected static void println(String text) {
        ProtTestLogger.getDefaultLogger().infoln(text);
    }

    /**
     * Prints the comparison over the 7 frameworks.
     */
    public static void printFrameworksComparison(ModelCollection models) {

        double[] aic = new double[models.size()];
        double[] aicc1 = new double[models.size()];
        double[] aicc2 = new double[models.size()];
        double[] aicc3 = new double[models.size()];
        double[] bic1 = new double[models.size()];
        double[] bic2 = new double[models.size()];
        double[] bic3 = new double[models.size()];
        String[] modelNames = new String[models.size()];
        double sampleSize1, sampleSize2, sampleSize3;

        sampleSize1 = ProtTestAlignment.calculateSampleSize(models.getAlignment(), SIZEMODE_ALIGNMENT, NO_SAMPLE_SIZE);
        sampleSize2 = ProtTestAlignment.calculateSampleSize(models.getAlignment(), SIZEMODE_SHANNON, NO_SAMPLE_SIZE);
        sampleSize3 = ProtTestAlignment.calculateSampleSize(models.getAlignment(), SIZEMODE_SHANNON_NxL, NO_SAMPLE_SIZE);

        InformationCriterion aicS1 = new AIC(models, 1.0, sampleSize1);
        InformationCriterion aiccS1 = new AICc(models, 1.0, sampleSize1);
        InformationCriterion aiccS2 = new AICc(models, 1.0, sampleSize2);
        InformationCriterion aiccS3 = new AICc(models, 1.0, sampleSize3);
        InformationCriterion bicS1 = new BIC(models, 1.0, sampleSize1);
        InformationCriterion bicS2 = new BIC(models, 1.0, sampleSize2);
        InformationCriterion bicS3 = new BIC(models, 1.0, sampleSize3);
        Collections.sort(models, new LKComparator());
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            modelNames[i] = model.getModelName();

            aicc1[i] = aiccS1.get(model).getValue();
            bic1[i] = bicS1.get(model).getValue();
            aic[i] = aicS1.get(model).getValue();

            aicc2[i] = aiccS2.get(model).getValue();
            bic2[i] = bicS2.get(model).getValue();

            aicc3[i] = aiccS3.get(model).getValue();
            bic3[i] = bicS3.get(model).getValue();

        }

        StatFramework aicF, aicc1F, aicc2F, aicc3F, bic1F, bic2F, bic3F;
        aicF = new StatFramework(aicS1, "AIC", "AIC");
        aicc1F = new StatFramework(aiccS1, "AICc(1)", "second-order AIC (sample size: " + SIZE_MODE_NAMES[SIZEMODE_ALIGNMENT] + ")");
        aicc2F = new StatFramework(aiccS2, "AICc(2)", "second-order AIC (sample size: " + SIZE_MODE_NAMES[SIZEMODE_SHANNON] + ")");
        aicc3F = new StatFramework(aiccS3, "AICc(3)", "second-order AIC (sample size: " + SIZE_MODE_NAMES[SIZEMODE_SHANNON_NxL] + ")");
        bic1F = new StatFramework(bicS1, "BIC(1)", "BIC (sample size: " + SIZE_MODE_NAMES[SIZEMODE_ALIGNMENT] + ")");
        bic2F = new StatFramework(bicS2, "BIC(2)", "BIC (sample size: " + SIZE_MODE_NAMES[SIZEMODE_SHANNON] + ")");
        bic3F = new StatFramework(bicS3, "BIC(3)", "BIC (sample size: " + SIZE_MODE_NAMES[SIZEMODE_SHANNON_NxL] + ")");

        //Hala, ahora a imprimir:
        println(ProtTestFormattedOutput.space(100, '-'));
        println("Table: Weights(Ranking) of the candidate models under the different frameworks");
        println(ProtTestFormattedOutput.space(100, '-'));
        println("model          AIC         AICc-1      AICc-2      AICc-3      BIC-1       BIC-2       BIC-3");
        String model__, tmp;
        for (int i = 0; i < models.size(); i++) {
            model__ = aicF.getModelName(i);
            print(model__ + ProtTestFormattedOutput.space(15 - model__.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(aicF.getWeight(model__), 2) + "(" + aicF.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getWeight(model__), 2) + "(" + aicc1F.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getWeight(model__), 2) + "(" + aicc2F.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getWeight(model__), 2) + "(" + aicc3F.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getWeight(model__), 2) + "(" + bic1F.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getWeight(model__), 2) + "(" + bic2F.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getWeight(model__), 2) + "(" + bic3F.getRanking(model__) + ")";
            print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
            println("");
        }
        println(ProtTestFormattedOutput.space(100, '-'));
        println("Relative importance of");
        println("parameters     AIC         AICc-1      AICc-2      AICc-3      BIC-1       BIC-2       BIC-3");
        print("+G" + ProtTestFormattedOutput.space(13, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getAlphaImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        print("+I" + ProtTestFormattedOutput.space(13, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getInvImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        print("+I+G" + ProtTestFormattedOutput.space(11, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getGIImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        print("+F" + ProtTestFormattedOutput.space(13, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getFImp(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        println(ProtTestFormattedOutput.space(100, '-'));
        println("Model-averaged estimate of");
        println("parameters     AIC         AICc-1      AICc-2      AICc-3      BIC-1       BIC-2       BIC-3");
        print("alpha (+G)" + ProtTestFormattedOutput.space(5, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallAlpha(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        print("p-inv (+I)" + ProtTestFormattedOutput.space(5, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallInv(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        print("alpha (+I+G)" + ProtTestFormattedOutput.space(3, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallAlphaGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        print("p-inv (+I+G)" + ProtTestFormattedOutput.space(3, ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicF.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc1F.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc2F.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(aicc3F.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic1F.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic2F.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        tmp = ProtTestFormattedOutput.getDecimalString(bic3F.getOverallInvGI(), 2);
        print(tmp + ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
        println("");
        println(ProtTestFormattedOutput.space(100, '-'));
        println("AIC   : Akaike Information Criterion framework.");
        println("AICc-x: Second-Order Akaike framework.");
        println("BIC-x : Bayesian Information Criterion framework.");
        println("AICc/BIC-1: sample size as: number of sites in the alignment                           (" + sampleSize1 + ")");
        println("AICc/BIC-2: sample size as: Sum of position's Shannon Entropy over the whole alignment (" + ProtTestFormattedOutput.getDecimalString(sampleSize2, 1) + ")");
        println("AICc/BIC-3: sample size as: align. length x num sequences x averaged (0-1)Sh. Entropy  (" + ProtTestFormattedOutput.getDecimalString(sampleSize3, 1) + ")");
        println(ProtTestFormattedOutput.space(100, '-'));
        println("");
    }

    public static String getDisplayValue(double value, String parameter, boolean existModels) {
        String toDisplay;
        if (existModels) {
            toDisplay = ProtTestFormattedOutput.getDecimalString(value, IMPORTANCE_PRECISSION);
        } else {
            toDisplay = "No " + parameter + " models";
        }
        return toDisplay;
    }

    /**
     * Prints the relative importance.
     * 
     * @param ic the information criterion
     */
    abstract void printRelativeImportance(InformationCriterion ic);

    /**
     * Prints the model averaged estimation.
     * 
     * @param ic the information criterion
     */
    abstract void printModelAveragedEstimation(InformationCriterion ic);
}
