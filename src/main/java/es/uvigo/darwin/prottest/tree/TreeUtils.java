package es.uvigo.darwin.prottest.tree;

import pal.tree.Node;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;
import java.io.PrintWriter;
import java.io.StringWriter;
import pal.io.FormattedOutput;
import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.misc.SimpleIdGroup;
import pal.tree.AttributeNode;
import pal.tree.NodeUtils;

/**
 * Some utils to phylogenetic tree analysis.
 * Based on PAL library.
 */
public abstract class TreeUtils {

    public static final int DEFAULT_COLUMN_WIDTH = 70;
    public static final String TREE_WEIGHT_ATTRIBUTE = "weight";
    public static final String TREE_CLADE_SUPPORT_ATTRIBUTE = "support";
    public static final String TREE_NAME_ATTRIBUTE = "treeName";

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

    @Deprecated
    public static int printNH(PrintWriter out, Tree tree,
            boolean printLengths, boolean printInternalLabels,
            boolean printCladeSupport) {
        return printNH(out, tree, tree.getRoot(),
                printLengths, printInternalLabels,
                printCladeSupport,
                0, true, DEFAULT_COLUMN_WIDTH);
    }

    public static String toNewick(Tree tree, boolean printLengths,
            boolean printInternalLabels, boolean printCladeSupport) {

        StringWriter sw = new StringWriter();
        PrintWriter mp = new PrintWriter(sw);

        printNH(mp, tree, tree.getRoot(),
                printLengths, printInternalLabels, printCladeSupport,
                0, false, -1);

        sw.append(';');
        return sw.toString();

    }

    @Deprecated
    public static int printNH(PrintWriter out, Tree tree, Node node,
            boolean printLengths, boolean printInternalLabels,
            boolean printCladeSupport,
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

                column = printNH(out, tree, node.getChild(i),
                        printLengths, printInternalLabels,
                        printCladeSupport,
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

            if (printCladeSupport) {
                if (tree.getAttribute(node, TREE_CLADE_SUPPORT_ATTRIBUTE) != null) {
                    double support = (Double) tree.getAttribute(node, TREE_CLADE_SUPPORT_ATTRIBUTE);
                    column += ProtTestFormattedOutput.displayDecimal(out, support, 2);
                }
            }

            if (printLengths) {
                out.print(":");
                column++;

                if (breakLines) {
                    column = breakLine(out, column, colWidth);
                }

                column += ProtTestFormattedOutput.displayDecimal(out, node.getBranchLength(), 6);
            }
        }

        return column;
    }

    /**
     * get list of the identifiers of the external nodes
     *
     * @return leaf identifier group
     */
    public static final IdGroup getLeafIdGroup(Tree tree) {
        tree.createNodeList();

        IdGroup labelList =
                new SimpleIdGroup(tree.getExternalNodeCount());

        for (int i = 0; i < tree.getExternalNodeCount(); i++) {
            labelList.setIdentifier(i, tree.getExternalNode(i).getIdentifier());
        }

        return labelList;
    }

    private static int breakLine(PrintWriter out, int column, int colWidth) {
        if (column > colWidth) {
            out.println();
            column = 0;
        }

        return column;
    }

    // Print picture of current tree in ASCII
    public static void printASCII(Tree tree, PrintWriter out) {
        FormattedOutput format = FormattedOutput.getInstance();

        tree.createNodeList();

        int numExternalNodes = tree.getExternalNodeCount();
        int numInternalNodes = tree.getInternalNodeCount();
        int numBranches = numInternalNodes + numExternalNodes - 1;

        boolean[] umbrella = new boolean[numExternalNodes];
        int[] position = new int[numExternalNodes];

        int minLength = (Integer.toString(numBranches)).length() + 1;

        int MAXCOLUMN = 40;
        Node root = tree.getRoot();
        if (root.getNodeHeight() == 0.0) {
            NodeUtils.lengths2Heights(root);
        }
        double proportion = (double) MAXCOLUMN / root.getNodeHeight();

        for (int n = 0; n < numExternalNodes; n++) {
            umbrella[n] = false;
        }

        position[0] = 1;
        for (int i = root.getChildCount() - 1; i > -1; i--) {
            printNodeInASCII(out, root.getChild(i), 1, i, root.getChildCount(),
                    numExternalNodes, umbrella, position, proportion, minLength);
            if (i != 0) {
                putCharAtLevel(out, 0, '|', position);
                out.println();
            }
        }
    }

    private static void printNodeInASCII(PrintWriter out, Node node, int level, int m, int maxm,
            int numExternalNodes, boolean[] umbrella, int[] position, double proportion, int minLength) {
        position[level] = (int) (node.getBranchLength() * proportion);

        if (position[level] < minLength) {
            position[level] = minLength;
        }

        if (node.isLeaf()) // external branch
        {
            if (m == maxm - 1) {
                umbrella[level - 1] = true;
            }

            printlnNodeWithNumberAndLabel(out, node, level, numExternalNodes, umbrella, position);

            if (m == 0) {
                umbrella[level - 1] = false;
            }
        } else // internal branch
        {
            for (int n = node.getChildCount() - 1; n > -1; n--) {
                printNodeInASCII(out, node.getChild(n), level + 1, n, node.getChildCount(),
                        numExternalNodes, umbrella, position, proportion, minLength);

                if (m == maxm - 1 && n == node.getChildCount() / 2) {
                    umbrella[level - 1] = true;
                }

                if (n != 0) {
                    if (n == node.getChildCount() / 2) {
                        printlnNodeWithNumberAndLabel(out, node, level, numExternalNodes, umbrella, position);
                    } else {
                        for (int i = 0; i < level + 1; i++) {
                            if (umbrella[i]) {
                                putCharAtLevel(out, i, '|', position);
                            } else {
                                putCharAtLevel(out, i, ' ', position);
                            }
                        }
                        out.println();
                    }
                }

                if (m == 0 && n == node.getChildCount() / 2) {
                    umbrella[level - 1] = false;
                }
            }
        }
    }

    private static void printlnNodeWithNumberAndLabel(PrintWriter out, Node node, int level,
            int numExternalNodes, boolean[] umbrella, int[] position) {
        for (int i = 0; i < level - 1; i++) {
            if (umbrella[i]) {
                putCharAtLevel(out, i, '|', position);
            } else {
                putCharAtLevel(out, i, ' ', position);
            }
        }

        putCharAtLevel(out, level - 1, '+', position);

        int branchNumber;
        if (node.isLeaf()) {
            branchNumber = node.getNumber() + 1;
        } else {
            branchNumber = node.getNumber() + 1 + numExternalNodes;
        }

        String numberAsString = Integer.toString(branchNumber);

        int numDashs = position[level] - numberAsString.length();

//        String cladeSupport = "";
//        if (node instanceof AttributeNode && ((AttributeNode)node).getAttribute(TreeUtils.TREE_CLADE_SUPPORT_ATTRIBUTE) != null) {
//            double cladeSupportValue = (Double) ((AttributeNode)node).getAttribute(TreeUtils.TREE_CLADE_SUPPORT_ATTRIBUTE);
//            cladeSupport = ProtTestFormattedOutput.getDecimalString(cladeSupportValue, 1);
//        }
//
//        numDashs -= cladeSupport.length();
        
        for (int i = 0; i < numDashs ; i++) {
            out.print('-');
        }
        out.print(numberAsString);

        if (node.isLeaf()) {
            out.println(" " + node.getIdentifier());
        } else {
            if (!node.getIdentifier().equals(Identifier.ANONYMOUS)) {
                out.print("(" + node.getIdentifier() + ")");
            }
            out.println();
        }
    }

    private static void putCharAtLevel(PrintWriter out, int level, char c,
            int[] position) {
        int n = position[level] - 1;
        for (int i = 0; i < n; i++) {
            out.print(' ');
        }
        out.print(c);
    }

    public static void printBranchInfo(Tree tree, PrintWriter out) {

        //
        // CALL PRINTASCII FIRST !!!
        //

        // check if some SE values differ from the default zero

        int numExternalNodes = tree.getExternalNodeCount();
        int numInternalNodes = tree.getInternalNodeCount();
        int numBranches = numBranches = numInternalNodes + numExternalNodes - 1;

        boolean showSE = false;
        for (int i = 0; i < numExternalNodes && showSE == false; i++) {
            if (tree.getExternalNode(i).getBranchLengthSE() != 0.0) {
                showSE = true;
            }
            if (i < numInternalNodes - 1) {
                if (tree.getInternalNode(i).getBranchLengthSE() != 0.0) {
                    showSE = true;
                }
            }
        }

        ProtTestFormattedOutput.displayIntegerWhite(out, numExternalNodes);
        out.print("   Length    ");
        if (showSE) {
            out.print("S.E.      ");
        }
        out.print("Label     ");
        if (numInternalNodes > 1) {
            ProtTestFormattedOutput.displayIntegerWhite(out, numBranches);
            out.print("        Length    ");
            if (showSE) {
                out.print("S.E.      ");
            }
            out.print("Label");
        }
        out.println();

        for (int i = 0; i < numExternalNodes; i++) {
            ProtTestFormattedOutput.displayInteger(out, i + 1, numExternalNodes);
            out.print("   ");
            ProtTestFormattedOutput.displayDecimal(out, tree.getExternalNode(i).getBranchLength(), 5);
            out.print("   ");
            if (showSE) {
                ProtTestFormattedOutput.displayDecimal(out, tree.getExternalNode(i).getBranchLengthSE(), 5);
                out.print("   ");
            }
            ProtTestFormattedOutput.displayLabel(out, tree.getExternalNode(i).getIdentifier().getName(), 10);

            if (i < numInternalNodes - 1) {
                ProtTestFormattedOutput.multiplePrint(out, ' ', 5);
                ProtTestFormattedOutput.displayInteger(out, i + 1 + numExternalNodes, numBranches);
                out.print("   ");
                ProtTestFormattedOutput.displayDecimal(out, tree.getInternalNode(i).getBranchLength(), 5);
                out.print("   ");
                if (showSE) {
                    ProtTestFormattedOutput.displayDecimal(out, tree.getInternalNode(i).getBranchLengthSE(), 5);
                    out.print("   ");
                }
                ProtTestFormattedOutput.displayLabel(out, tree.getInternalNode(i).getIdentifier().getName(), 10);
            }

            out.println();
        }
    }

    // Print height information
    public static void heightInfo(Tree tree, PrintWriter out) {

        int numExternalNodes = tree.getExternalNodeCount();
        int numInternalNodes = tree.getInternalNodeCount();
        int numBranches = numExternalNodes + numInternalNodes - 1;

        if (tree.getRoot().getNodeHeight() == 0.0) {
            NodeUtils.lengths2Heights(tree.getRoot());
        }

        ProtTestFormattedOutput.displayIntegerWhite(out, numExternalNodes);
        out.print("   Height    ");
        ProtTestFormattedOutput.displayIntegerWhite(out, numBranches);
        out.print("        Height    ");

        out.println();

        for (int i = 0; i < numExternalNodes; i++) {
            ProtTestFormattedOutput.displayInteger(out, i + 1, numExternalNodes);
            out.print("   ");
            ProtTestFormattedOutput.displayDecimal(out, tree.getExternalNode(i).getNodeHeight(), 7);
            out.print("   ");

            if (i < numInternalNodes) {
                ProtTestFormattedOutput.multiplePrint(out, ' ', 5);

                if (i == numInternalNodes - 1) {
                    out.print("R");
                    ProtTestFormattedOutput.multiplePrint(out, ' ', Integer.toString(numBranches).length() - 1);
                } else {
                    ProtTestFormattedOutput.displayInteger(out, i + 1 + numExternalNodes, numBranches);
                }

                out.print("   ");
                ProtTestFormattedOutput.displayDecimal(out, tree.getInternalNode(i).getNodeHeight(), 7);
                out.print("   ");
            }

            out.println();
        }
    }
}
