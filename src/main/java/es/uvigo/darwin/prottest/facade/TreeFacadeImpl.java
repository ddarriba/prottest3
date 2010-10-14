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
package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.util.List;

import pal.tree.Tree;
import es.uvigo.darwin.prottest.consensus.Consensus;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import java.io.StringWriter;

/**
 * Generic implementation of the Tree Facade services
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
 */
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
