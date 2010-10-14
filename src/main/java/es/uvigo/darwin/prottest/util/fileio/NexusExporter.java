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

import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import pal.tree.Tree;

/**
 *
 * @author Diego Darriba
 */
public class NexusExporter {

    private static final String HEADER = "#NEXUS";
    private File outFile;
    private PrintWriter outputWriter;

    private enum NexusBlock {

        TAXA {

            public void beginSection(PrintWriter outputWriter) {
                outputWriter.println("begin taxa;");
            }

            public void endSection(PrintWriter outputWriter) {
                outputWriter.println("end taxa;");
            }

            public String getSectionName() {
                return "taxa";
            }

            public void export(PrintWriter outputWriter, List taxa, Properties properties) {

            }
        },
        TREES {

            public void beginSection(PrintWriter outputWriter) {
                outputWriter.println("begin trees;");
            }

            public void endSection(PrintWriter outputWriter) {
                outputWriter.println("end trees;");
            }

            public String getSectionName() {
                return "trees";
            }

            public void export(PrintWriter outputWriter, List trees, Properties properties) {

                printBlankLine(outputWriter);
                for (Object o : trees) {
                    if (!(o instanceof Tree)) {
                        throw new TypeMismatchException(Tree.class, o.getClass());
                    }

                    Tree tree = (Tree) o;

                    boolean printCladeSupport = properties.getProperty(PRINT_CLADE_SUPPORT, "false").equalsIgnoreCase("true");
                    String treeName = (String) tree.getAttribute(tree.getRoot(), TreeUtils.TREE_NAME_ATTRIBUTE);
                    String treeNewick = TreeUtils.toNewick(tree, true, true, printCladeSupport);

                    outputWriter.print("    " + treeName + " = ");
                    outputWriter.println(treeNewick);
                }
                printBlankLine(outputWriter);

            }
        },
        CONSENSUS {

            public void beginSection(PrintWriter outputWriter) {
                TREES.beginSection(outputWriter);
            }

            public void endSection(PrintWriter outputWriter) {
                TREES.endSection(outputWriter);
            }

            public void export(PrintWriter outputWriter, List trees, Properties properties) {

                printBlankLine(outputWriter);
                String fullTreeComment = "Note: This tree contains information on the topology," +
                        " branch lengths (if present), and the probability " +
                        " of the partition indicated by the branch.";
                addComment(outputWriter, fullTreeComment, 0);

                properties.setProperty(PRINT_CLADE_SUPPORT, "true");
                TREES.export(outputWriter, trees, properties);

                String simpleTreeComment = "Note: This tree contains information only on the topology" +
                        " and branch lengths (mean of the posterior probability density).";
                addComment(outputWriter, simpleTreeComment, 0);

                properties.setProperty(PRINT_CLADE_SUPPORT, "false");
                TREES.export(outputWriter, trees, properties);
                printBlankLine(outputWriter);
            }
        },
        DEFAULT {

            public void beginSection(PrintWriter outputWriter) {
                throw new UndefinedBlockException();
            }

            public void endSection(PrintWriter outputWriter) {
                throw new UndefinedBlockException();
            }

            public void export(PrintWriter outputWriter, List objects, Properties properties) {
                throw new UndefinedBlockException();
            }
        };
        public static final String PRINT_CLADE_SUPPORT = "pcs";

        public void addComment(PrintWriter outputWriter, String comment, int lineWidth) {

            comment = "[" + comment + "] ";
            int commentLength = comment.length();

            if (lineWidth <= 0) {
                lineWidth = comment.length();
            }

            int column = 0;
            while (column < commentLength) {
                String toWrite;
                if (column + lineWidth + 1 < comment.length()) {
                    toWrite = comment.substring(column, column + lineWidth + 1);
                } else {
                    toWrite = comment.substring(column);
                }
                int toWriteLength = toWrite.lastIndexOf(" ");
                if (toWriteLength > 0 && toWriteLength < toWrite.length()) {
                    outputWriter.println(toWrite.substring(0, toWriteLength));
                } else {
                    toWriteLength = lineWidth;
                    outputWriter.println(toWrite);
                }
                column += toWriteLength;
            }

        }

        public void printBlankLine(PrintWriter outputWriter) {
            outputWriter.println(" ");
        }
        
        public abstract void beginSection(PrintWriter outputWriter);

        public abstract void endSection(PrintWriter outputWriter);

        public abstract void export(PrintWriter outputWriter, List objects, Properties properties);

        private static class TypeMismatchException extends ProtTestInternalException {

            public TypeMismatchException(Class classRequired, Class classObtained) {
                super("Required class " + classRequired + " but obtained " + classObtained);
            }
            
        }

        private static class UndefinedBlockException extends ProtTestInternalException {

            public UndefinedBlockException() {
                super("No Nexus block is defined");
            }
            
        }
    }

    public NexusExporter(File outFile) throws IOException {

        this.outFile = outFile;
        if (!outFile.exists()) {
            outFile.createNewFile();
        }

        this.outputWriter = new PrintWriter(outFile);

        initStructure(this.outputWriter);

    }

    public void printTreeBlock(List<Tree> trees) {
        NexusBlock treeBlock = NexusBlock.TREES;

        treeBlock.beginSection(outputWriter);
        treeBlock.export(outputWriter, trees, new Properties());
        treeBlock.endSection(outputWriter);

    }

    public void printConsensusBlock(Tree tree, String id) {
        NexusBlock treeBlock = NexusBlock.CONSENSUS;

        List<Tree> trees = new ArrayList<Tree>();
        trees.add(tree);

        if (id != null) {
            treeBlock.addComment(outputWriter, id, 0);
        }
        treeBlock.beginSection(outputWriter);
        treeBlock.export(outputWriter, trees, new Properties());
        treeBlock.endSection(outputWriter);

    }

    public void printComment(String msg, int colWidth) {
        NexusBlock.DEFAULT.addComment(outputWriter, msg, colWidth);
    }

    public void close() {
        outputWriter.close();
    }

    private void initStructure(PrintWriter outputWriter) {

        outputWriter.println(HEADER);
        outputWriter.println(" ");

    }
}
