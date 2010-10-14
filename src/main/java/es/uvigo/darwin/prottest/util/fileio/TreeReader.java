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

import es.uvigo.darwin.prottest.tree.WeightedTree;
import java.util.ArrayList;
import java.util.List;
import pal.misc.IdGroup;

/**
 *
 * This class allows reading a set of trees from a file
 *
 * @author Diego Darriba
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
