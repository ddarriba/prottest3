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
package es.uvigo.darwin.prottest.util.fileio;

import converter.Converter;
import converter.DefaultFactory;
import converter.Factory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;

import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;

import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
     * @param output the output writer
     * @param filename the filename
     * @param debug the debug
     * 
     * @return the alignment
     * 
     * @throws AlignmentParseException the alignment parse exception.
     * @throws FileNotFoundException Signals that the input filename does not exist.
     * @throws IOException Signals that an I/O exception has occured.
     */
    public static Alignment readAlignment(PrintWriter output, String filename, boolean debug)
            throws AlignmentParseException, FileNotFoundException, IOException {

        Alignment alignment;
        StringBuilder text = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String s;
        while ((s = br.readLine()) != null) {
            text.append(s).append("\r\n");
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

        try {
        	PushbackReader pr = new PushbackReader(new StringReader(out));
        	alignment = readAlignment(output, pr, debug);
        } catch (AlignmentParseException ex) {
        	sequential = false;
        	try {
                converter = factory.getConverter(inO, inP, inF, autodetect,
                        collapse, gaps, missing, limit,
                        outO, outP, outF, lower, numbers, sequential, match, logger.getName());
                out = converter.convert(in);
            } catch (UnsupportedOperationException exUO) {
                throw new AlignmentParseException("There's some error in your data: " + ex.getMessage());
            } catch (ParseException exP) {
                throw new AlignmentParseException("There's some error in your data: " + ex.getMessage());
            }
        	PushbackReader pr = new PushbackReader(new StringReader(out));
        	alignment = readAlignment(output, pr, debug);
        }
        
        if (alignment == null) {
            throw new AlignmentParseException("There's some error in your data, exiting...");
        }

        return alignment;
    }

    /**
     * Read alignment.
     * 
     * @param out the out
     * @param pr the pushback reader which contains the alignment
     * @param debug the debug state
     * 
     * @return the alignment
     * 
     * @throws AlignmentParseException the alignment parse exception
     * @throws IOException Signals that an I/O exception has occured.
     */
    public static Alignment readAlignment(PrintWriter out, PushbackReader pr, boolean debug)
            throws AlignmentParseException, IOException {

        if (debug) {
            out.println("");
            out.println("**********************************************************");
            out.println("  Reading alignment...");
        }
        Alignment alignment = null;
        try {
            alignment = new ReadAlignment(pr);
        } catch (pal.alignment.AlignmentParseException e) {
            throw new AlignmentParseException(e.getMessage());
        }

        List<String> seqNames = new ArrayList<String>(alignment.getSequenceCount());
        for (int i = 0; i < alignment.getSequenceCount(); i++) {
            seqNames.add(alignment.getIdentifier(i).getName());
        }

        String currString;
        int size = alignment.getSequenceCount();
        for (int i = 0; i < size; i++) {
            currString = seqNames.get(i);
            for (int j = i + 1; j < size; j++) {
                if (seqNames.get(j).equals(currString)) {
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
     * @throws TreeFormatException When the input filename is not in the right format
     * @throws FileNotFoundException Signals that the input filename does not exist.
     * @throws IOException Signals that an I/O exception has occured.
     */
    public static Tree readTree(PrintWriter out, String filename, boolean debug)
            throws TreeFormatException, FileNotFoundException, IOException {
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
