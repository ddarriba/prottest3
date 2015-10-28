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
package es.uvigo.darwin.prottest.selection.printer;

import static es.uvigo.darwin.prottest.global.ProtTestConstants.IMPORTANCE_PRECISSION;

import java.util.Collections;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.DT;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.util.StatFramework;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.comparator.LKComparator;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

/**
 * Utility to print application data into loggers.
 */
public abstract class PrintFramework {

	/**
	 * Prints the models sorted by a concrete information criterion.
	 */
	public final void printModelsSorted(
			InformationCriterion informationCriterion) {

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
		println("Best model according to "
				+ informationCriterion.getCriterionName() + ": "
				+ bestModel.getModelName());
		double confPercent = informationCriterion.getConfidenceInterval() * 100;
		println("Confidence Interval: " + confPercent);
		println(ProtTestFormattedOutput.space(lineLength, '*'));
		for (int i = 0; i < numberOfFields; i++) {
			print(columns[i]);
			print(ProtTestFormattedOutput.space(
					fieldLength - columns[i].length(), ' '));
		}
		println("");
		println(ProtTestFormattedOutput.space(lineLength, '-'));
		for (Model model : models) {
			print(model.getModelName());
			print(ProtTestFormattedOutput.space(fieldLength
					- model.getModelName().length(), ' '));

			String decimal;

			decimal = ProtTestFormattedOutput.getDecimalString(
					informationCriterion.get(model).getDeltaValue(), 2);
			print(decimal);
			print(ProtTestFormattedOutput.space(fieldLength - decimal.length(),
					' '));
			decimal = ProtTestFormattedOutput.getDecimalString(
					informationCriterion.get(model).getValue(), 2);
			print(decimal);
			print(ProtTestFormattedOutput.space(fieldLength - decimal.length(),
					' '));
			decimal = ProtTestFormattedOutput.getDecimalString(
					informationCriterion.get(model).getWeightValue(), 2);
			print(decimal);
			print(ProtTestFormattedOutput.space(fieldLength - decimal.length(),
					' '));
			decimal = ProtTestFormattedOutput.getDecimalString(
					-1 * model.getLk(), 2);
			print(decimal);
			print(ProtTestFormattedOutput.space(fieldLength - decimal.length(),
					' '));

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

		boolean includeI, includeG, includeIG, includeF;
		includeI = includeG = includeIG = includeF = false;

		double[] aic = new double[models.size()];
		double[] aicc = new double[models.size()];
		double[] bic = new double[models.size()];
		double[] dt = new double[models.size()];

		String[] modelNames = new String[models.size()];

		InformationCriterion aicS = new AIC(models, 1.0, 0);
		InformationCriterion aiccS = new AICc(models, 1.0, models.getAlignment().getSiteCount());
		InformationCriterion bicS = new BIC(models, 1.0, models.getAlignment().getSiteCount());
		InformationCriterion dtS = new DT(models, 1.0, models.getAlignment().getSiteCount());

		Collections.sort(models, new LKComparator());
		for (int i = 0; i < models.size(); i++) {
			Model model = models.get(i);

			includeI |= model.isInv();
			includeG |= model.isGamma();
			includeIG |= model.isInv() && model.isGamma();
			includeF |= model.isPlusF();

			modelNames[i] = model.getModelName();

			aicc[i] = aiccS.get(model).getValue();
			dt[i] = dtS.get(model).getValue();
			bic[i] = bicS.get(model).getValue();
			aic[i] = aicS.get(model).getValue();

		}

		StatFramework aicF, aiccF, bicF, dtF;
		aicF = new StatFramework(aicS, "AIC", "AIC");
		aiccF = new StatFramework(aiccS, "AICc",
				"second-order AIC");
		bicF = new StatFramework(bicS, "BIC", "BIC");
		dtF = new StatFramework(dtS, "DT", "DT");
		

		// Hala, ahora a imprimir:
		println(ProtTestFormattedOutput.space(100, '-'));
		println("Table: Weights(Ranking) of the candidate models under the different frameworks");
		println(ProtTestFormattedOutput.space(100, '-'));
		println("model          AIC         AICc        BIC         DT");
		String model__, tmp;
		for (int i = 0; i < models.size(); i++) {
			model__ = aicF.getModelName(i);
			print(model__
					+ ProtTestFormattedOutput.space(15 - model__.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(
					aicF.getWeight(model__), 2)
					+ "(" + aicF.getRanking(model__) + ")";
			print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp
					+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(
					aiccF.getWeight(model__), 2)
					+ "(" + aiccF.getRanking(model__) + ")";
			print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp
					+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(
					bicF.getWeight(model__), 2)
					+ "(" + bicF.getRanking(model__) + ")";
			print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp
					+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
			tmp = ProtTestFormattedOutput.getDecimalString(
					dtF.getWeight(model__), 2)
					+ "(" + dtF.getRanking(model__) + ")";
			print(ProtTestFormattedOutput.space(0 - tmp.length(), ' ') + tmp
					+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
			println("");
		}
		if (includeG | includeI | includeIG | includeF) {
			println(ProtTestFormattedOutput.space(100, '-'));
			println("Relative importance of");
			println("parameters     AIC         AICc        BICc       DT");
			if (includeG) {
				print("+G" + ProtTestFormattedOutput.space(13, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aicF.getAlphaImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getAlphaImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getAlphaImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getAlphaImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
			if (includeI) {
				print("+I" + ProtTestFormattedOutput.space(13, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aicF.getInvImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getInvImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getInvImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getInvImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
			if (includeIG) {
				print("+I+G" + ProtTestFormattedOutput.space(11, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(aicF.getGIImp(),
						2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getGIImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getGIImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getGIImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
			if (includeF) {
				print("+F" + ProtTestFormattedOutput.space(13, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(aicF.getFImp(),
						2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getFImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getFImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getFImp(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
		}
		if (includeG | includeI | includeIG) {
			println(ProtTestFormattedOutput.space(100, '-'));
			println("Model-averaged estimate of");
			println("parameters     AIC         AICc        BIC         DT");
			if (includeG) {
				print("alpha (+G)" + ProtTestFormattedOutput.space(5, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aicF.getOverallAlpha(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getOverallAlpha(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getOverallAlpha(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getOverallAlpha(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
			if (includeI) {
				print("p-inv (+I)" + ProtTestFormattedOutput.space(5, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aicF.getOverallInv(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getOverallInv(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getOverallInv(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getOverallInv(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
			if (includeIG) {
				print("alpha (+I+G)" + ProtTestFormattedOutput.space(3, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aicF.getOverallAlphaGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getOverallAlphaGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getOverallAlphaGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getOverallAlphaGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
				print("p-inv (+I+G)" + ProtTestFormattedOutput.space(3, ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aicF.getOverallInvGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						aiccF.getOverallInvGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						bicF.getOverallInvGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				tmp = ProtTestFormattedOutput.getDecimalString(
						dtF.getOverallInvGI(), 2);
				print(tmp
						+ ProtTestFormattedOutput.space(12 - tmp.length(), ' '));
				println("");
			}
		}
		println(ProtTestFormattedOutput.space(100, '-'));
		println("AIC   : Akaike Information Criterion framework.");
		println("AICc  : Second-Order Akaike framework.");
		println("BIC   : Bayesian Information Criterion framework.");
		println("DT    : Decision Theory Criterion framework.");
		println(ProtTestFormattedOutput.space(100, '-'));
		println("");
	}

	public static String getDisplayValue(double value, String parameter,
			boolean existModels) {
		String toDisplay;
		if (existModels) {
			toDisplay = ProtTestFormattedOutput.getDecimalString(value,
					IMPORTANCE_PRECISSION);
		} else {
			toDisplay = "No " + parameter + " models";
		}
		return toDisplay;
	}

	/**
	 * Prints the relative importance.
	 * 
	 * @param ic
	 *            the information criterion
	 */
	abstract void printRelativeImportance(InformationCriterion ic);

	/**
	 * Prints the model averaged estimation.
	 * 
	 * @param ic
	 *            the information criterion
	 */
	abstract void printModelAveragedEstimation(InformationCriterion ic);
}
