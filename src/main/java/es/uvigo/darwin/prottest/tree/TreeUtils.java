package es.uvigo.darwin.prottest.tree;

import pal.tree.Node;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * Some utils to phylogenetic tree analysis.
 * Based on PAL library.
 */
public abstract class TreeUtils {

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
		if (numberOfInternalNodes != t2.getInternalNodeCount())
			throw new ProtTestInternalException("Different number of internal nodes: " +
					t1.getInternalNodeCount() + " vs " + t2.getInternalNodeCount());
		int numberOfExternalNodes = t1.getExternalNodeCount();
		if (numberOfExternalNodes != t2.getExternalNodeCount())
			throw new ProtTestInternalException("Different number of external nodes: " +
					t1.getInternalNodeCount() + " vs " + t2.getInternalNodeCount());
		for (int i = 0; i < numberOfInternalNodes; i++) {
			double bl1 = t1.getInternalNode(i).getBranchLength();
			double bl2 = t2.getInternalNode(i).getBranchLength();
			sum += (bl1-bl2)*(bl1-bl2);
		}
		for (int i = 0; i < numberOfExternalNodes; i++) {
			double bl1 = t1.getExternalNode(i).getBranchLength();
			double bl2 = t2.getExternalNode(i).getBranchLength();
			sum += (bl1-bl2)*(bl1-bl2);
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
		if (node.getNodeHeight() > 0.0)
			return node.getNodeHeight();
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
}
