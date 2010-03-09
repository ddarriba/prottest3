/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xprottest.analysis;

import pal.tree.Tree;

/**
 *
 * @author diego
 */
public class TreeWrapper {

    private String id;
    private Tree tree;
    
    public Tree getTree() {
        return tree;
    }
    
    public TreeWrapper (String id, Tree tree) {
        this.id = id;
        this.tree = tree;
    }
    
    @Override
    public String toString() {
        return id;
    }
}
