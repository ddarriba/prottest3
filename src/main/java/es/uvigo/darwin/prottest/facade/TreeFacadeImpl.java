package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import pal.tree.Tree;
import es.uvigo.darwin.prottest.consensus.Consensus;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

public class TreeFacadeImpl implements TreeFacade {

	public void displayASCIITree(Tree tree,
			PrintWriter outputWriter) {

		pal.tree.TreeUtils.report(tree, outputWriter);
	}
	
        @Deprecated
	public void displayNewickTree(Tree tree,
			PrintWriter outputWriter) {
		
		TreeUtils.printNH(outputWriter, tree, true, true, true);
	}

	public void displayConsensusTree(List<Model> modelList,
			double threshold, PrintWriter outputWriter) {
		List<Tree> trees;
		//build tree list
		trees = new ArrayList<Tree>(modelList.size());
		for (Model model : modelList)
			trees.add(model.getTree());
		Tree consensus = createConsensusTree(trees, threshold);
		outputWriter.println("");
		displayNewickTree(consensus, outputWriter);
		outputWriter.println("");
		displayASCIITree(consensus, outputWriter);
		outputWriter.println("");
	}
	
	public void displayWeightedConsensusTree(InformationCriterion ic,
			double threshold, PrintWriter outputWriter) {
		Tree consensus = createConsensusTree(ic, threshold);
		outputWriter.println("");
		displayNewickTree(consensus, outputWriter);
		outputWriter.println("");
		displayASCIITree(consensus, outputWriter);
		outputWriter.println("");
	}
	
	public Tree createConsensusTree(List<Tree> treeColection,
			double threshold) {
		
		if (threshold < 0.5 || threshold > 1.0)
			throw new ProtTestInternalException("Invalid threshold value: " + threshold);
		Consensus consensus = new Consensus(treeColection, threshold);
		Tree cons = consensus.getConsensusTree();
		return cons;
	}
	
	public Tree createConsensusTree(InformationCriterion ic, double threshold) {
		if (threshold < 0.5 || threshold > 1.0)
			throw new ProtTestInternalException("Invalid threshold value: " + threshold);
		Consensus consensus = new Consensus(ic, threshold);
		Tree cons = consensus.getConsensusTree();
		return cons;
	}

}
