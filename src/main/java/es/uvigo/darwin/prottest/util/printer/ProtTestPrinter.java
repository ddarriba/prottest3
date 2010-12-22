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
package es.uvigo.darwin.prottest.util.printer;

import java.io.PrintWriter;
import java.util.Date;

import es.uvigo.darwin.prottest.ProtTest;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import java.io.File;

/**
 * The Class ProtTestPrinter provides a common PrintWriter module to
 * the whole application. It encapsulates both output and error
 * PrintWriter.
 */
public class ProtTestPrinter {

    private static final String H_RULE =
            "********************************************************";
    /** The output print writer. */
    private PrintWriter out;
    /** The error print writer. */
    private PrintWriter err;

    /**
     * Instantiates a new ProtTestPrinter.
     * 
     * @param out the output writer
     * @param err the error writer
     */
    public ProtTestPrinter(PrintWriter out, PrintWriter err) {
        this.out = out;
        this.err = err;
    }

    /**
     * Prints out the header section of the application.
     */
    public static void printHeader() {
        println("");
        println("");
        println("ProtTest " + ProtTest.versionNumber
                + ProtTestFormattedOutput.space(27 - ProtTest.versionNumber.length(), ' ') +
                                                    "Fast selection of the best-fit models of protein evolution");
        println("(c) 2009-2010   Diego Darriba (1,2), Guillermo Taboada (2), Ramón Doallo (2), David Posada (1)");
        println("(1)                               Facultad de Biologia, Universidad de Vigo, 36200 Vigo, Spain");
        println("(2)                    Facultade de Informática, Universidade da Coruña, 15071 A Coruña, Spain");
        println("Contact:                                                     ddarriba@udc.es, dposada@uvigo.es");
        println("----------------------------------------------------------------------------------------------");
        println("");
        println((new Date()).toString());
        println("OS = " + System.getProperty("os.name") +
                " (" + System.getProperty("os.version") + ")");
        println("");
    }

    /**
     * Prints out the header of the pre-analysis section.
     */
    public static void printPreAnalysisHeader() {
        println("");
        println(H_RULE);
        println(center("ALIGNMENT ANALYSIS"));
        println(H_RULE);
        println("");
    }

    /**
     * Prints out the header of the selection section
     */
    public static void printSelectionHeader(String criterionName) {
        println("");
        println("");
        println(H_RULE);
        println(center(criterionName));
        println(H_RULE);
    }

    /**
     * Prints out the header of the tree display
     */
    public static void printTreeHeader(String modelName) {
        println("");
        println(H_RULE);
        println(center(modelName));
        println(H_RULE);
        println("");
    }

    public static void printExecutionHeader(ApplicationOptions options) {
        println("");
        println(H_RULE);
        println(center("MODEL OPTIMIZATION"));
        println(H_RULE);
        options.reportModelOptimization();
        println("");
    }

    /**
     * Prints file data
     */
    public static void printFileData(File f) {
        println("");
        println("File: " + f.getAbsolutePath());
        println("Size: " + f.length());
        println("");
    }
    public static void println(String text) {
        ProtTestLogger.infoln(text, ProtTestPrinter.class);
    }

    /**
     * Prints out the footer section of the application.
     */
    public static void printFooter() {
        println("");
        println("");
        println("ProtTest-HPC - " + ProtTest.versionNumber);
    }
    private static String center(String text) {
        return ProtTestFormattedOutput.space((H_RULE.length() - text.length())/2, ' ') + text;
    }

    /**
     * Gets the output writer.
     * 
     * @return the output writer
     */
    public PrintWriter getOutputWriter() {
        return out;
    }

    /**
     * Sets the output writer.
     * 
     * @param out the new output writer
     */
    public void setOutputWriter(PrintWriter out) {
        this.out = out;
    }

    /**
     * Gets the error writer.
     * 
     * @return the error writer
     */
    public PrintWriter getErrorWriter() {
        return err;
    }

    /**
     * Sets the error writer.
     * 
     * @param err the new error writer
     */
    public void setErrorWriter(PrintWriter err) {
        this.err = err;
    }

    /**
     * Flushes output writer.
     */
    public void flush() {
        out.flush();
    }

    /**
     * Flushes error writer.
     */
    public void flushError() {
        err.flush();
    }

    /**
     * Closes error writer.
     */
    public void closeError() {
        err.close();
    }
}
