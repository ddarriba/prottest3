package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.util.List;

import pal.tree.Tree;
import es.uvigo.darwin.prottest.consensus.Consensus;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import java.io.StringWriter;

public class TreeFacadeImpl implements TreeFacade {

    public String toASCII(Tree tree) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TreeUtils.printASCII(tree, pw);
        pw.flush();
        return sw.toString();
    }

    public String branchInfo(Tree tree) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TreeUtils.printBranchInfo(tree, pw);
        pw.flush();
        return sw.toString();
    }

    public String heightInfo(Tree tree) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TreeUtils.heightInfo(tree, pw);
        pw.flush();
        return sw.toString();
    }

    public String toNewick(Tree tree, boolean printLengths,
            boolean printInternalLabels, boolean printCladeSupport) {
        return TreeUtils.toNewick(tree, printLengths, printInternalLabels, printCladeSupport);
    }

    public Tree createConsensusTree(List<Tree> treeColection,
            double threshold) {

        if (threshold < 0.5 || threshold > 1.0) {
            throw new ProtTestInternalException("Invalid threshold value: " + threshold);
        }
        Consensus consensus = new Consensus(treeColection, threshold);
        Tree cons = consensus.getConsensusTree();
        return cons;
    }

    public Tree createConsensusTree(InformationCriterion ic, double threshold) {
        Consensus consensus = createConsensus(ic, threshold);
        Tree cons = consensus.getConsensusTree();
        return cons;
    }
    
    public Consensus createConsensus(InformationCriterion ic, double threshold) {
        if (threshold < 0.5 || threshold > 1.0) {
            throw new ProtTestInternalException("Invalid threshold value: " + threshold);
        }
        Consensus consensus = new Consensus(ic, threshold);
        return consensus;
    }
}
