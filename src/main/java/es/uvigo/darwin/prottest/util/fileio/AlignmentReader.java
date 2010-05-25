package es.uvigo.darwin.prottest.util.fileio;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.util.Vector;

import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import pal.alignment.Alignment;
import pal.alignment.ReadAlignment;
import pal.tree.ReadTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;

// TODO: Auto-generated Javadoc
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
    public Alignment readAlignment(PrintWriter out, String filename, boolean debug)
            throws AlignmentParseException, FileNotFoundException {

        PushbackReader pr = new PushbackReader(new FileReader(new File(filename)));
        return readAlignment(out, pr, debug);
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
    public Alignment readAlignment(PrintWriter out, PushbackReader pr, boolean debug)
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
    public Tree readTree(PrintWriter out, String filename, boolean debug)
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
    public Tree readTree(PrintWriter out, PushbackReader in, boolean debug)
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
