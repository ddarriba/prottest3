/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.consensus;

import java.io.File;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PushbackReader;
import java.io.PrintWriter;

import pal.tree.Tree;
import pal.misc.IdGroup;

import java.io.IOException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import es.uvigo.darwin.prottest.util.fileio.AlignmentReader;
import java.util.ArrayList;

/**
 *
 * @author diego
 */
public class SimpleNewickTreeReader implements TreeReader {

    /** The first element. */
    private static final int FIRST = 0;

    private int numTaxa;
    private IdGroup idGroup;
    private double cumWeight;
    private List<WeightedTree> trees = new ArrayList<WeightedTree>();

    public int getNumTaxa(){ return numTaxa; }
    public IdGroup getIdGroup() { return idGroup; }
    public double getCumWeight() { return cumWeight; }
    public List<WeightedTree> getWeightedTreeList() { return trees; }

    public SimpleNewickTreeReader(File treeFile)
        throws ProtTestInternalException, IOException {

        FileReader fr = new FileReader(treeFile);
        BufferedReader br = new BufferedReader(fr);

        //PushbackReader in = new PushbackReader(fr);

        AlignmentReader reader = new AlignmentReader();

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
                        Tree tree = reader.readTree(out, in, false);
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
		if (wTree.getTree() == null || wTree.getWeight() < 0.0)
			throw new ProtTestInternalException();
		//check compatibility
		if (trees.size() == 0) {
			trees.add(wTree);
			numTaxa = wTree.getTree().getIdCount();
			idGroup = pal.tree.TreeUtils.getLeafIdGroup(wTree.getTree());
		}
		else {
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
