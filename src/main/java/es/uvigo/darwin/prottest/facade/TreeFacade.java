package es.uvigo.darwin.prottest.facade;

import es.uvigo.darwin.prottest.consensus.Consensus;
import java.util.List;

import pal.tree.Tree;
import es.uvigo.darwin.prottest.selection.InformationCriterion;

public interface TreeFacade {

    //	**********************************************************
    //		  TREE SERVICES
    //**********************************************************

    /**
     * Gets ASCII representation of a Tree instance.
     * 
     * @param tree the tree
     */
    public String toASCII(Tree tree);

    /**
     * Gets branch information of a Tree instance.
     * 
     * @param tree the tree
     */
    public String branchInfo(Tree tree);

    /**
     * Gets height information of a Tree instance.
     * 
     * @param tree the tree
     */
    public String heightInfo(Tree tree);

    /**
     * Gets newick representation of a Tree instance.
     * 
     * @param tree the tree
     * @param printLengths if branch lengths should be included
     * @param printInternalLabels if internal labels should be included
     * @param printCladeSupport if clade support should be included
     */
    public String toNewick(Tree tree, boolean printLengths,
            boolean printInternalLabels, boolean printCladeSupport);

    /**
     * Create a consensus tree from a list of trees.
     * 
     * @param treeCollection the trees to build consensus
     * @param threshold the minimum clade support
     */
    public Tree createConsensusTree(List<Tree> treeCollection, double threshold);

    /**
     * Create a weighted consensus tree from an information criterion.
     * 
     * @param ic the weighted models with the trees to build consensus
     * @param threshold the minimum clade support
     */
    public Tree createConsensusTree(InformationCriterion ic, double threshold);
    
    /**
     * Create a weighted consensus instance from an information criterion.
     * 
     * @param ic the weighted models with the trees to build consensus
     * @param threshold the minimum clade support
     */
    public Consensus createConsensus(InformationCriterion ic, double threshold);
    
}
