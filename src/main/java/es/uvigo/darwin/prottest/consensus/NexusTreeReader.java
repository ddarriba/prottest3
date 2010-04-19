/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.consensus;

import es.uvigo.darwin.prottest.util.exception.ImportException;
import es.uvigo.darwin.prottest.util.fileio.NexusImporter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pal.tree.Tree;
import pal.tree.TreeUtils;

/**
 *
 * @author diego
 */
public class NexusTreeReader extends TreeReader {

    private NexusImporter imp;
    
    public NexusTreeReader(File treeFile) throws ImportException {
        try {
            imp = new NexusImporter(new FileReader(treeFile));
//            imp.startReadingTrees();
            List<Tree> importedTrees = imp.importTrees();
            for (Tree tree : importedTrees) {
                double weight = (Double) tree.getAttribute(tree.getRoot(), NexusImporter.WEIGHT_ATTRIBUTE);
                trees.add(new WeightedTree(tree, weight));
                cumWeight += weight;
                if (idGroup == null) {
                    idGroup = TreeUtils.getLeafIdGroup(tree);
                    numTaxa = idGroup.getIdCount();
                }
            }
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
