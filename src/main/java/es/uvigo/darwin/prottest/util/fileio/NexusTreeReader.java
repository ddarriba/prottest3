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

import es.uvigo.darwin.prottest.consensus.*;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.tree.WeightedTree;
import es.uvigo.darwin.prottest.util.exception.ImportException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pal.tree.Tree;

/**
 *
 * @author Diego Darriba
 */
public class NexusTreeReader extends TreeReader {

    private NexusImporter imp;
    
    public String getNexusId() {
        return imp.getNexusId();
    }
    
    public NexusTreeReader(File treeFile) throws ImportException {
        
        try {
            FileReader fileReader = new FileReader(treeFile);
            imp = new NexusImporter(fileReader);
            List<Tree> importedTrees = imp.importTrees();
            for (Tree tree : importedTrees) {
                double weight = (Double) tree.getAttribute(tree.getRoot(), TreeUtils.TREE_WEIGHT_ATTRIBUTE);
                trees.add(new WeightedTree(tree, weight));
                cumWeight += weight;
                if (idGroup == null) {
                    idGroup = TreeUtils.getLeafIdGroup(tree);
                    numTaxa = idGroup.getIdCount();
                }
            }
            fileReader.close();
        } catch (IOException ex) {
            Logger.getLogger(NexusTreeReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]) throws ImportException {
        String filename = args[0];
        File file = new File(filename);
        NexusTreeReader ntr = new NexusTreeReader(file);
    }

}
