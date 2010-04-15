package es.uvigo.darwin.prottest.consensus;

import java.util.List;
import pal.misc.IdGroup;

/**
 *
 * This interface allows reading a set of trees from a file
 *
 * @author diego
 */
public interface TreeReader {

    public int getNumTaxa();
    public IdGroup getIdGroup();
    public double getCumWeight();
    public List<WeightedTree> getWeightedTreeList();
    
}
