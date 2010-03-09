package es.uvigo.darwin.prottest.util.printer;

import java.io.PrintWriter;

import pal.io.FormattedOutput;

// TODO: Auto-generated Javadoc
/**
 * The Class ProtTestFormattedOutput.
 */
public class ProtTestFormattedOutput {

	/** The Constant NAN. */
	private static final String NAN = "NaN";
	
	/** The formatter. */
	private static FormattedOutput formatter;

	static {
		formatter = FormattedOutput.getInstance();
	}
	
	/**
	 * Space.
	 * 
	 * @param size the size
	 * @param c the c
	 * 
	 * @return the string
	 */
	public static String space(int size, char c) {
		return FormattedOutput.space(size, c);
	}
	
	/**
	 * Gets the decimal string.
	 * 
	 * @param number the number
	 * @param width the width
	 * 
	 * @return the decimal string
	 */
	public static String getDecimalString(double number, int width) {
		String strValue;
		if (Double.isNaN(number))//.isInfinite(number))
			strValue = NAN;
		else
			strValue = formatter.getDecimalString(number, width);
			
		return strValue;
	}

	/**
	 * Display decimal.
	 * 
	 * @param out the out
	 * @param number the number
	 * @param width the width
	 * 
	 * @return the int
	 */
	public static int displayDecimal(PrintWriter out, double number, int width) {
		int result = 0;
		if (Double.isNaN(number)) {
			formatter.displayLabel(out, NAN, width);
			result = NAN.length();
		}
		else
			result = formatter.displayDecimal(out, number, width);
		
		return result;
	}
}
