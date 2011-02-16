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
package es.uvigo.darwin.prottest.facade;

import es.uvigo.darwin.prottest.consensus.Consensus;
import java.util.List;

import pal.tree.Tree;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.tree.WeightedTree;

/**
 * Declaration of services related to phylogenetic tree management.
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
 */
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
     * @param treeCollection the trees and its weights to build consensus
     * @param threshold the minimum clade support
     */
    public Tree createConsensusTree(List<WeightedTree> treeCollection, double threshold);

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
