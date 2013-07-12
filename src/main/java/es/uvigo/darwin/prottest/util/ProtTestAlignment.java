/*
Copyright (C) 2009  Diego Darriba, Federico Abascal

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
package es.uvigo.darwin.prottest.util;

import static es.uvigo.darwin.prottest.global.ApplicationGlobals.*;

import java.io.PrintWriter;

import pal.alignment.Alignment;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

/**
 * The Class ProtTestAlignment provides some operations about Alignment, as
 * could be the empirical frequencies or the sample size calculation.
 * 
 * @author Federico Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public abstract class ProtTestAlignment {

	/**
	 * Calculate sample size.
	 * 
	 * @param alignment
	 *            the alignment to calculate sample size
	 * @param sampleSizeMode
	 *            the sample size mode
	 * @param customSampleSize
	 *            the user sample size
	 * 
	 * @return the double
	 */
	public static double calculateSampleSize(Alignment alignment) {
		// For AICc and BIC frameworks
		return alignment.getSiteCount();
	}

	/**
	 * Calculate invariable sites.
	 * 
	 * @param alignment
	 *            the alignment
	 * @param verbose
	 *            the verbose
	 * 
	 * @return the int
	 */
	public static int calculateInvariableSites(Alignment alignment,
			boolean verbose) {
		// use this function to estimate a good starting value for the
		// InvariableSites distribution.
		int numSites = alignment.getSiteCount();
		int numSeqs = alignment.getSequenceCount();
		int inv = 0;
		int tmp;
		boolean tmpInv = true;
		for (int i = 0; i < numSites; i++) {
			tmp = indexOfChar(alignment.getData(0, i));
			tmpInv = true;
			for (int j = 0; j < numSeqs; j++) {
				if (indexOfChar(alignment.getData(j, i)) != tmp) { // if at
																	// least one
																	// difference
																	// in column
																	// i:
					j = numSeqs; // we exit this for.
					tmpInv = false; // i is not an invariable site.
				}
			}
			if (tmpInv)
				inv++;
		}
		if (verbose)
			System.out.println("Observed number of invariant sites: " + inv);
		return inv;
	}

	/**
	 * Gets the frequencies.
	 * 
	 * @param alignment
	 *            the alignment
	 * 
	 * @return the frequencies
	 */
	public static double[] getFrequencies(Alignment alignment) {
		int numSites = alignment.getSiteCount();
		int numSeqs = alignment.getSequenceCount();
		double freqs[] = new double[AMINOACID_NUM_STATES];
		int aas[] = new int[AMINOACID_NUM_STATES];
		int total_aas = 0;
		for (int i = 0; i < AMINOACID_NUM_STATES; i++)
			aas[i] = 0;

		for (int i = 0; i < numSites; i++)
			for (int j = 0; j < numSeqs; j++) {
				int index = indexOfChar(alignment.getData(j, i));
				if (index >= 0) {
					aas[index]++;
					total_aas++;
				}
			}

		for (int i = 0; i < AMINOACID_NUM_STATES; i++)
			freqs[i] = (double) aas[i] / (double) total_aas;

		return freqs;
	}

	/**
	 * Prints the frequencies.
	 * 
	 * @param freqs
	 *            the freqs
	 * @param out
	 *            the out
	 */
	public static void printFrequencies(double[] freqs, PrintWriter out) {

		out.println("Observed aminoacid frequencies:");
		for (int i = 0; i < AMINOACID_NUM_STATES; i++) {
			char aa = charOfIndex(i);
			out.print(" " + aa + ": "
					+ ProtTestFormattedOutput.getDecimalString(freqs[i], 3)
					+ "   ");
			if ((i + 1) % 5 == 0)
				out.println("");
		}
	}

	/**
	 * Char of index.
	 * 
	 * @param c
	 *            the c
	 * 
	 * @return the char
	 */
	public static char charOfIndex(int c) {
		char[] charSet = { 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L',
				'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y' };
		if (c >= 0 && c <= charSet.length)
			return charSet[c];
		else {
			return '?';
		}
	}

	/**
	 * Index of char.
	 * 
	 * @param c
	 *            the c
	 * 
	 * @return the int
	 */
	private static int indexOfChar(char c) {
		char[] charSet = { 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L',
				'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y' };
		for (int charIndex = 0; charIndex < charSet.length; charIndex++)
			if (charSet[charIndex] == c)
				return charIndex;

		return -1;
	}
}
