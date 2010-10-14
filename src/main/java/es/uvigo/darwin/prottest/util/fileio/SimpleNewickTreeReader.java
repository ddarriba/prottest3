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

import java.io.File;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PushbackReader;
import java.io.PrintWriter;

import pal.tree.Tree;

import java.io.IOException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import es.uvigo.darwin.prottest.tree.WeightedTree;

/**
 *
 * @author Diego Darriba
 */
public class SimpleNewickTreeReader extends TreeReader {

    /** The first element. */
    private static final int FIRST = 0;

    public SimpleNewickTreeReader(File treeFile)
            throws ProtTestInternalException, IOException {

        FileReader fr = new FileReader(treeFile);
        BufferedReader br = new BufferedReader(fr);

        //PushbackReader in = new PushbackReader(fr);

        try {
            while (br.ready()) {
                String line = br.readLine();
                if (line == null || line.equals("")) {
                    break;
                }

                line = line.replace('[', ';');
                line = line.replace(']', ';');
                String[] values = line.split(";");
                String weightStr = values[1];
                double weight;
                weight = Double.parseDouble(weightStr);

                File tempFile = File.createTempFile("consensus", "tmp");
                tempFile.deleteOnExit();
                FileWriter fw = new FileWriter(tempFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(values[0]);
                bw.append(';');
                bw.close();
                fw.close();

                PushbackReader in = new PushbackReader(new FileReader(tempFile));

                PrintWriter out = new PrintWriter(System.out);
                Tree tree = AlignmentReader.readTree(out, in, false);
                out.flush();

                addTree(trees, new WeightedTree(tree, weight));
            }
        } catch (TreeFormatException tfe) {
            throw new ProtTestInternalException("Bad file format. Cannot parse tree");
        } catch (NumberFormatException nfe) {
            throw new ProtTestInternalException("Bad file format. Expecting double value");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds a weighted tree to the set.
     *
     * @param wTree the weighted tree
     *
     * @return true, if successful
     */
    private boolean addTree(List<WeightedTree> trees, WeightedTree wTree) {
        //check integrity
        if (wTree.getTree() == null || wTree.getWeight() < 0.0) {
            throw new ProtTestInternalException();
        }
        //check compatibility
        if (trees.size() == 0) {
            trees.add(wTree);
            numTaxa = wTree.getTree().getIdCount();
            idGroup = pal.tree.TreeUtils.getLeafIdGroup(wTree.getTree());
        } else {
            if (wTree.getTree().getIdCount() != numTaxa) {
                return false;
            }
            Tree pTree = trees.get(FIRST).getTree();
            for (int i = 0; i < numTaxa; i++) {
                boolean found = false;
                for (int j = 0; j < numTaxa; j++) {
                    if (wTree.getTree().getIdentifier(i).equals(pTree.getIdentifier(j))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("NOT COMPATIBLE TREES");
                    return false;
                }
            }
            trees.add(wTree);
        }
        cumWeight += wTree.getWeight();
        return true;
    }
}
