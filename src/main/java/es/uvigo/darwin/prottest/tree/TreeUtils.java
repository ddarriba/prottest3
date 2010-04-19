package es.uvigo.darwin.prottest.tree;

import pal.tree.Node;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;
import java.io.PrintWriter;

/**
 * Some utils to phylogenetic tree analysis.
 * Based on PAL library.
 */
public abstract class TreeUtils {

    public static final int DEFAULT_COLUMN_WIDTH = 70;

    /**
     * Calculates the euclidean tree distance between two trees.
     * 
     * @param t1 the first tree
     * @param t2 the second tree
     * 
     * @return the double
     */
    public static double euclideanTreeDistance(Tree t1, Tree t2) {
        double sum = 0.0;
        int numberOfInternalNodes = t1.getInternalNodeCount();
        if (numberOfInternalNodes != t2.getInternalNodeCount()) {
            throw new ProtTestInternalException("Different number of internal nodes: " +
                    t1.getInternalNodeCount() + " vs " + t2.getInternalNodeCount());
        }
        int numberOfExternalNodes = t1.getExternalNodeCount();
        if (numberOfExternalNodes != t2.getExternalNodeCount()) {
            throw new ProtTestInternalException("Different number of external nodes: " +
                    t1.getInternalNodeCount() + " vs " + t2.getInternalNodeCount());
        }
        for (int i = 0; i < numberOfInternalNodes; i++) {
            double bl1 = t1.getInternalNode(i).getBranchLength();
            double bl2 = t2.getInternalNode(i).getBranchLength();
            sum += (bl1 - bl2) * (bl1 - bl2);
        }
        for (int i = 0; i < numberOfExternalNodes; i++) {
            double bl1 = t1.getExternalNode(i).getBranchLength();
            double bl2 = t2.getExternalNode(i).getBranchLength();
            sum += (bl1 - bl2) * (bl1 - bl2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Calculates the Robinson-Foulds tree distance between two trees.
     * 
     * @param t1 the first tree
     * @param t2 the second tree
     * 
     * @return the double
     */
    public static double robinsonFouldsTreeDistance(Tree t1, Tree t2) {
        return pal.tree.TreeUtils.getRobinsonFouldsDistance(t1, t2);
    }

    // 
    /**
     * Calculates the number of branches from node to most remote tip.
     * 
     * @param node the starting node
     * 
     * @return the node distance
     */
    public static int nodeDistance(final Node node) {
        if (node.isLeaf()) {
            return 0;
        }

        int d = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            Node n = node.getChild(i);
            d = Math.max(d, nodeDistance(n));
        }
        return d + 1;
    }

    /**
     * Calculates the safe node height.
     * 
     * @param tree the tree
     * @param node the node
     * 
     * @return the height of the node
     */
    public static double safeNodeHeight(final Tree tree, final Node node) {
        if (node.getNodeHeight() > 0.0) {
            return node.getNodeHeight();
        }
        return TreeUtils.nodeDistance(node);
    }

    /**
     * Make sure subtree below node has consistent heights, i.e. node height is higher than it's descendants
     * 
     * @param tree the tree
     * @param node the node
     * 
     * @return height of node
     */
    public static double insureConsistency(Tree tree, Node node) {
        double height = TreeUtils.safeNodeHeight(tree, node);
        if (node.isLeaf()) {
            return height;
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                Node n = node.getChild(i);
                final double childHeight = insureConsistency(tree, n);
                height = Math.max(height, childHeight);
            }
        }

        node.setNodeHeight(height);
        return height;
    }

    public static int printNH(PrintWriter out, Tree tree,
            boolean printLengths, boolean printInternalLabels) {
        return printNH(out, tree.getRoot(),
                printLengths, printInternalLabels,
                0, true, DEFAULT_COLUMN_WIDTH);
    }
    
    public static int printNH(PrintWriter out, Node node,
            boolean printLengths, boolean printInternalLabels) {
        return printNH(out, node,
                printLengths, printInternalLabels,
                0, true, DEFAULT_COLUMN_WIDTH);
    }

    public static int printNH(PrintWriter out, Node node,
            boolean printLengths, boolean printInternalLabels,
            int column, boolean breakLines) {

        return printNH(out, node,
                printLengths, printInternalLabels,
                column, breakLines, DEFAULT_COLUMN_WIDTH);

    }

    public static int printNH(PrintWriter out, Node node,
            boolean printLengths, boolean printInternalLabels,
            int column, boolean breakLines, int colWidth) {

        if (breakLines) {
            column = breakLine(out, column, colWidth);
        }

        if (!node.isLeaf()) {
            out.print("(");
            column++;

            for (int i = 0; i < node.getChildCount(); i++) {
                if (i != 0) {
                    out.print(",");
                    column++;
                }

                column = printNH(out, node.getChild(i),
                        printLengths, printInternalLabels,
                        column, breakLines, colWidth);
            }

            out.print(")");
            column++;
        }

        if (!node.isRoot()) {
            if (node.isLeaf() || printInternalLabels) {
                if (breakLines) {
                    column = breakLine(out, column, colWidth);
                }

                String id = node.getIdentifier().toString();
                out.print(id);
                column += id.length();
            }

            if (printLengths) {
                out.print(":");
                column++;

                if (breakLines) {
                    column = breakLine(out, column, colWidth);
                }

                column += ProtTestFormattedOutput.displayDecimal(out, node.getBranchLength(), 7);
            }
        }

        return column;
    }

    private static int breakLine(PrintWriter out, int column, int colWidth) {
        if (column > colWidth) {
            out.println();
            column = 0;
        }

        return column;
    }
}
