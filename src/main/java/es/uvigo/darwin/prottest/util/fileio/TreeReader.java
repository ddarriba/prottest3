package es.uvigo.darwin.prottest.util.fileio;

import es.uvigo.darwin.prottest.tree.WeightedTree;
import es.uvigo.darwin.prottest.consensus.*;
import java.util.ArrayList;
import java.util.List;
import pal.misc.IdGroup;

/**
 *
 * This class allows reading a set of trees from a file
 *
 * @author diego
 */
public abstract class TreeReader {

    protected int numTaxa;
    protected IdGroup idGroup;
    protected double cumWeight;
    protected List<WeightedTree> trees = new ArrayList<WeightedTree>();

    public int getNumTaxa(){ return numTaxa; }
    public IdGroup getIdGroup() { return idGroup; }
    public double getCumWeight() { return cumWeight; }
    public List<WeightedTree> getWeightedTreeList() { return trees; }
    
}
