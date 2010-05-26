package es.uvigo.darwin.prottest.util.fileio;

import converter.Converter;
import converter.DefaultFactory;
import converter.Factory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.util.Vector;

import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;

import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import pal.alignment.Alignment;
import pal.alignment.ReadAlignment;
import pal.tree.ReadTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;

// TODO: Auto-generated Javadoc
import parser.ParseException;
/**
 * The Class AlignmentReader.
 */
public class AlignmentReader {

    /**
     * Instantiates a new alignment reader.
     */
    public AlignmentReader() {

    }

    /**
     * Read alignment.
     * 
     * @param out the out
     * @param filename the filename
     * @param debug the debug
     * 
     * @return the alignment
     * 
     * @throws AlignmentParseException the alignment parse exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Alignment readAlignment(PrintWriter output, String filename, boolean debug)
            throws AlignmentParseException, FileNotFoundException, IOException {

        Alignment alignment;
        StringBuffer text = new StringBuffer();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String s;
        while ((s = br.readLine()) != null) {
            text.append(s + "\r\n");
        }
        br.close();


        //Get options
        String in = text.toString();
        String inO = "";
        String inP = "";
        String inF = "";
        boolean autodetect = true;
        boolean collapse = false;
        boolean gaps = false;
        boolean missing = false;
        int limit = 0;
        String out = "";
        String outO = "Linux";
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            outO = "MacOS";
        } else if (os.startsWith("Linux")) {
            outO = "Linux";
        } else if (os.startsWith("Win")) {
            outO = "Windows";
        }
        String outP = "ProtTest";
        String outF = "PHYLIP";
        boolean lower = false;
        boolean numbers = false;
        boolean sequential = true;
        boolean match = false;

        //Get converter and convert MSA
        Factory factory = new DefaultFactory();
        Converter converter;

        //Inicializar logger
        Logger logger = Logger.getLogger("alter" + System.currentTimeMillis());
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        for (Handler handler : ProtTestLogger.getDefaultLogger().getHandlers()) {
            logger.addHandler(handler);
        }

        try {
            converter = factory.getConverter(inO, inP, inF, autodetect,
                    collapse, gaps, missing, limit,
                    outO, outP, outF, lower, numbers, sequential, match, logger.getName());
            out = converter.convert(in);
        } catch (UnsupportedOperationException ex) {
            throw new AlignmentParseException("There's some error in your data: " + ex.getMessage());
        } catch (ParseException ex) {
            throw new AlignmentParseException("There's some error in your data: " + ex.getMessage());
        }

        PushbackReader pr = new PushbackReader(new StringReader(out));
        alignment = readAlignment(output, pr, debug);

        if (alignment == null) {
            throw new AlignmentParseException("There's some error in your data, exiting...");
        }

        return alignment;
    }

    /**
     * Read alignment.
     * 
     * @param out the out
     * @param filename the filename
     * @param debug the debug
     * 
     * @return the alignment
     * 
     * @throws AlignmentParseException the alignment parse exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Alignment readAlignment(PrintWriter out, PushbackReader pr, boolean debug)
            throws AlignmentParseException {

        if (debug) {
            out.println("");
            out.println("**********************************************************");
            out.println("  Reading alignment...");
        }
        Alignment alignment = null;
        try {
            alignment = new ReadAlignment(pr);
        } catch (pal.alignment.AlignmentParseException e) {
            if (debug) {
                out.println("Error: Alignment parsing problem");
            }
            throw new AlignmentParseException(e.getMessage());
        } catch (IOException e) {
            if (debug) {
                out.println("Error: File not found (IO error)");
            }
            throw new AlignmentParseException(e.getMessage());
        } catch (Exception e) {
            if (debug) {
                out.println("Error: Alignment parsing problem");
            }
            throw new AlignmentParseException(e.getMessage());
        }

        Vector<String> seqNames = new Vector<String>(alignment.getSequenceCount());
        for (int i = 0; i < alignment.getSequenceCount(); i++) {
            seqNames.add(alignment.getIdentifier(i).getName());
        }

        String currString;
        int size = alignment.getSequenceCount();
        for (int i = 0; i < size; i++) {
            currString = (String) seqNames.elementAt(i);
            for (int j = i + 1; j < size; j++) {
                if (seqNames.elementAt(j).equals(currString)) {
                    throw new AlignmentParseException("ERROR: There are duplicated taxa names in the alignment: " + currString);
                }
            }
        }

        //TODO: print TAXA and check if data is in correct format
        if (debug) {
            for (int i = 0; i < alignment.getSequenceCount(); i++) {
                out.println("    Sequence #" + (i + 1) + ": " + alignment.getIdentifier(i).getName());
            }
            out.println("   Alignment contains " + alignment.getSequenceCount() + " sequences of length " + alignment.getSiteCount());
            out.println("");
            out.println("**********************************************************");
            out.println("");
        }

        return alignment;
    }

    /**
     * Read tree.
     * 
     * @param out the out
     * @param filename the filename
     * @param debug the debug
     * 
     * @return the tree
     * 
     * @throws TreeFormatException the tree format exception
     */
    public static Tree readTree(PrintWriter out, String filename, boolean debug)
            throws TreeFormatException {
        //FOR PHYML:
        //if tree is not in newick format, reformat and save to a new file:
        //TreeUtils: public static void printNH(Tree tree, java.io.PrintWriter out)
        if (debug) {
            out.println("Reading tree...");
        }
        Tree tree;
        try {
            tree = new ReadTree(filename);
        } catch (TreeParseException e) {
            throw new TreeFormatException("Error: Wrong tree format : " + e.getMessage());
        } catch (IOException e) {
            throw new TreeFormatException("Error: " + e.getMessage());
        }
        if (debug) {
            out.println("Tree contains " + tree.getExternalNodeCount() + " external nodes");
            out.println("");
        }
        return tree;
    }

    /**
     * Read tree.
     * 
     * @param out the out
     * @param in the tree pushback reader
     * @param debug the debug
     * 
     * @return the tree
     * 
     * @throws TreeFormatException the tree format exception
     */
    public static Tree readTree(PrintWriter out, PushbackReader in, boolean debug)
            throws TreeFormatException {
        //FOR PHYML:
        //if tree is not in newick format, reformat and save to a new file:
        //TreeUtils: public static void printNH(Tree tree, java.io.PrintWriter out)
        if (debug) {
            out.println("Reading tree...");
        }
        Tree tree;
        try {
            tree = new ReadTree(in);
        } catch (TreeParseException e) {
            throw new TreeFormatException("Error: Wrong tree format : " + e.getMessage());
        }
        if (debug) {
            out.println("Tree contains " + tree.getExternalNodeCount() + " external nodes");
            out.println("");
        }
        return tree;
    }
}
