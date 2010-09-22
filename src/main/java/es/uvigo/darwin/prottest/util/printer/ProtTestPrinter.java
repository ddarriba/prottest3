package es.uvigo.darwin.prottest.util.printer;

import java.io.PrintWriter;
import java.util.Date;

import es.uvigo.darwin.prottest.ProtTest;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import java.io.File;

/**
 * The Class ProtTestPrinter provides a common PrintWriter module to
 * the whole application. It encapsulates both output and error
 * PrintWriter.
 */
public class ProtTestPrinter {

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
                + ProtTestFormattedOutput.space(13 - ProtTest.versionNumber.length(), ' ') +
                                      "Fast selection of the best-fit models of protein evolution");
        println("(c) 2009-2010                     Diego Darriba, Guillermo Taboada, Ramón Doallo");
        println("(FA, DP)            Facultad de Biologia, Universidad de Vigo, 36200 Vigo, Spain");
        println("(FA, DP) Facultade de Informática, Universidade da Coruña, 15071 A Coruña, Spain");
        println("Contact:                                       ddarriba@udc.es, dposada@uvigo.es");
        println("--------------------------------------------------------------------------------");
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
        println("********************************************************");
        println("                  ALIGNMENT ANALYSIS                    ");
        println("********************************************************");
        println("");
    }

    /**
     * Prints out the header of the selection section
     */
    public static void printSelectionHeader(String criterionName) {
        println("");
        println("");
        println("********************************************************");
        println(ProtTestFormattedOutput.space(28 - criterionName.length()/2, ' ') + criterionName);
        println("********************************************************");
    }

    /**
     * Prints out the header of the tree display
     */
    public static void printTreeHeader(String modelName) {
        println("");
        println("********************************************************");
        println(ProtTestFormattedOutput.space(28 - modelName.length()/2, ' ') + modelName);
        println("********************************************************");
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
