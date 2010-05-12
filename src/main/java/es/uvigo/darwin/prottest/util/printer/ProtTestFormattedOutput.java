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
        {
            strValue = NAN;
        } else {
            strValue = formatter.getDecimalString(number, width);
        }

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
        } else {
            result = formatter.displayDecimal(out, number, width);
        }

        return result;
    }

    /**
     * turns an integer into a String, aligned to a reference number,
     * (introducing space at the left side)
     *
     * @param num number to be printed
     * @param maxNum reference number
     */
    public static String getIntegerString(int num, int maxNum) {
        StringBuffer sb = new StringBuffer();
        int lenNum = Integer.toString(num).length();
        int lenMaxNum = Integer.toString(maxNum).length();

        if (lenNum < lenMaxNum) {
            for (int i = 0; i < num; i++) {
                sb.append(' ');
            }
        }
        sb.append(num);

        return sb.toString();
    }

    /**
     * print integer, aligned to a reference number,
     * (introducing space at the left side)
     *
     * @param out output stream
     * @param num number to be printed
     * @param maxNum reference number
     */
    public static void displayInteger(PrintWriter out, int num, int maxNum) {
        int lenNum = Integer.toString(num).length();
        int lenMaxNum = Integer.toString(maxNum).length();

        if (lenNum < lenMaxNum) {
            multiplePrint(out, ' ', lenMaxNum - lenNum);
        }
        out.print(num);
    }

    /**
     * print whitespace of length of a string displaying a given integer
     *
     * @param output stream
     * @param maxNum number
     */
    public static void displayIntegerWhite(PrintWriter out, int maxNum) {
        int lenMaxNum = Integer.toString(maxNum).length();

        multiplePrint(out, ' ', lenMaxNum);
    }

    /**
     * print label with a prespecified length
     * (label will be shortened or spaces will introduced, if necessary)
     *
     * @param out output stream
     * @param label label to be printed
     * @param width desired length
     */
    public static void displayLabel(PrintWriter out, String label, int width) {
        int len = label.length();

        if (len == width) {
            // Print as is
            out.print(label);
        } else if (len < width) {
            // fill rest with spaces
            out.print(label);
            multiplePrint(out, ' ', width - len);
        } else {
            // Print first width characters
            for (int i = 0; i < width; i++) {
                out.print(label.charAt(i));
            }
        }
    }

    /**
     * repeatedly print a character
     *
     * @param out output stream
     * @param c   character
     * @param num number of repeats
     */
    public static void multiplePrint(PrintWriter out, char c, int num) {
        for (int i = 0; i < num; i++) {
            out.print(c);
        }
    }
}
