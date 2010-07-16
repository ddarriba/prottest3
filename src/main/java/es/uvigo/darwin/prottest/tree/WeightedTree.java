/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.darwin.prottest.tree;

import pal.tree.Tree;

/**
 *  This class represents a weighted PAL Tree
 * 
 * @author diego
 */
public class WeightedTree {

    /** The tree. */
    private Tree tree;
    /** The weight. */
    private double weight;

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Instantiates a new weighted tree.
     *
     * @param tree the tree
     * @param weight the weight
     */
    public WeightedTree(Tree tree, double weight) {
        this.tree = tree;
        this.weight = weight;
    }
}
