package es.uvigo.darwin.prottest.consensus;

import es.uvigo.darwin.prottest.tree.WeightedTree;
import es.uvigo.darwin.prottest.util.fileio.SimpleNewickTreeReader;
import es.uvigo.darwin.prottest.util.fileio.NexusTreeReader;
import es.uvigo.darwin.prottest.util.fileio.TreeReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.NodeFactory;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.FixedBitSet;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.exception.ImportException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.fileio.NexusExporter;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;
import es.uvigo.darwin.prottest.util.printer.ProtTestPrinter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import pal.misc.Identifier;

/**
 * Phylogenetic consensus tree builder.
 * 
 * @author Diego Darriba
 * @since 3.0
 */
public class Consensus {

    /** Display branch suport as percent. */
    final static public boolean SUPPORT_AS_PERCENT = false;
    /** Calculate branch lengths as weighted average. */
    final static public int BRANCH_LENGTHS_AVERAGE = 1;
    /** Calculate branch lengths as weighted median. */
    final static public int BRANCH_LENGTHS_MEDIAN = 2;
    /** Default branch lengths algorithm */
    private static final BranchDistances DEFAULT_BRANCH_DISTANCES =
            BranchDistances.WeightedAverage;
    /** The Constant FIRST (just for source code visibility). */
    private static final int FIRST = 0;
    /** The weighted trees in consensus. */
    private List<WeightedTree> trees;
    /** The cummulative weight. */
    private double cumWeight = 0.0;
    /** The number of taxa. */
    private int numTaxa;
    /** The common id group of the tree set. */
    private IdGroup idGroup;
    /** The set of clade supports. */
    private Map<FixedBitSet, Support> support =
            new HashMap<FixedBitSet, Support>();
    /** The set of clade supports to get from outside this class. */
    private Map<FixedBitSet, Double> cladeSupport;
    /** The inner consensus tree. */
    private Tree consensusTree;
    /** The splits included in consensus tree */
    private List<FixedBitSet> splitsInConsensus = new ArrayList<FixedBitSet>();
    /** The splits not included in consensus tree */
    private List<FixedBitSet> splitsOutFromConsensus = new ArrayList<FixedBitSet>();

    /**
     * Gets the clade support, with Support instances
     * 
     * @return the map of the support for each bitSet
     */
    private Map<FixedBitSet, Support> getSupport() {
        return support;
    }

    /**
     * Gets the double precision clade support
     * 
     * @return the map of the support for each bitSet
     */
    public Map<FixedBitSet, Double> getCladeSupport() {

        if (cladeSupport == null) {
            cladeSupport = new HashMap<FixedBitSet, Double>(support.size());
            FixedBitSet[] keys = support.keySet().toArray(new FixedBitSet[0]);
            Arrays.sort(keys);

            for (FixedBitSet fbs : keys) {
                cladeSupport.put(fbs, support.get(fbs).treesWeightWithClade / cumWeight);
            }
        }
        return cladeSupport;
    }

    /**
     * Gets the Id Group of the set of trees
     * 
     * @return the id group
     */
    public IdGroup getIdGroup() {
        return idGroup;
    }

    /**
     * Gets the consensus tree
     * 
     * @return the consensus tree
     */
    public Tree getConsensusTree() {
        return consensusTree;
    }

    /**
     * Gets the set of trees included in the consensus.
     * 
     * @return the trees
     */
    public Collection<WeightedTree> getTrees() {
        return trees;
    }

    /**
     * Adds a weighted tree to the set.
     * 
     * @param wTree the weighted tree
     * 
     * @return true, if successful
     */
    private boolean addTree(WeightedTree wTree) {
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

    /**
     * Instantiates a new consensus tree builder.
     * 
     * @param ic the information criterion to build the weighted trees
     * @param supportThreshold the minimum support for a clade
     */
    public Consensus(InformationCriterion ic, double supportThreshold) {
        this(ic, supportThreshold, 0);
    }

    /**
     * Instantiates a new consensus tree builder.
     * 
     * @param ic the information criterion to build the weighted trees
     * @param supportThreshold the minimum support for a clade
     * @param branchDistances the method to get the consensus branch lengths
     */
    public Consensus(InformationCriterion ic, double supportThreshold, int branchDistances) {
        this.trees = new ArrayList<WeightedTree>();
        for (SelectionModel model : ic.getConfidenceModels()) {
            WeightedTree wTree = new WeightedTree(
                    model.getModel().getTree(),
                    model.getWeightValue());
            this.addTree(wTree);
        }
        consensusTree = buildTree(supportThreshold, getBranchDistances(branchDistances));
    }

    /**
     * Instantiates a new unweighted consensus builder.
     * 
     * @param trees the trees
     * @param supportThreshold the minimum support for a clade
     */
    public Consensus(List<Tree> trees, double supportThreshold) {
        this(trees, supportThreshold, 0);
    }

    /**
     * Instantiates a new unweighted consensus builder.
     * 
     * @param trees the trees
     * @param supportThreshold the minimum support for a clade
     * @param branchDistances the method to get the consensus branch lengths
     */
    public Consensus(List<Tree> trees, double supportThreshold, int branchDistances) {
        this.trees = new ArrayList<WeightedTree>();
        for (Tree tree : trees) {
            this.addTree(new UnweightedTree(tree));
        }

        consensusTree = buildTree(supportThreshold, getBranchDistances(branchDistances));
    }

    /**
     * Instantiates a new unweighted consensus builder.
     * 
     * @param treesFile the file with the set of trees in Newick format
     * @param supportThreshold the minimum support for a clade
     * @throws IOException 
     */
    public Consensus(File treesFile, double supportThreshold)
            throws ProtTestInternalException, IOException {

        this(treesFile, supportThreshold, 0);

    }

    /**
     * Instantiates a new unweighted consensus builder.
     * 
     * @param treesFile the file with the set of trees in Newick format
     * @param supportThreshold the minimum support for a clade
     * @param branchDistances the method to get the consensus branch lengths
     * @throws IOException 
     */
    public Consensus(File treesFile, double supportThreshold, int branchDistances)
            throws ProtTestInternalException, IOException {

        TreeReader treeReader;

        try {
            treeReader = new NexusTreeReader(treesFile);
        } catch (ImportException ex) {
            treeReader = new SimpleNewickTreeReader(treesFile);
        }

        this.trees = treeReader.getWeightedTreeList();
        this.cumWeight = treeReader.getCumWeight();
        this.numTaxa = treeReader.getNumTaxa();
        this.idGroup = treeReader.getIdGroup();

        consensusTree = buildTree(supportThreshold, getBranchDistances(branchDistances));

        String fileName = treesFile.getName();
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        File resultDir = new File("results");
        if (!resultDir.exists()) {
            resultDir.mkdir();
        }
        File outFile = File.createTempFile(fileName, ".con", resultDir);

        NexusExporter nw = new NexusExporter(outFile);
        nw.printConsensusBlock(consensusTree, ((NexusTreeReader) treeReader).getNexusId());
        nw.close();

    }

    /**
     * Calculates rooted support.
     * 
     * @param wTree the weighted tree instance
     * @param node the node
     * @param support the support
     * 
     * @return the fixed bit set
     */
    private FixedBitSet rootedSupport(WeightedTree wTree, Node node, Map<FixedBitSet, Support> support) {
        FixedBitSet clade = new FixedBitSet(numTaxa);
        if (node.isLeaf()) {
            clade.set(idGroup.whichIdNumber(node.getIdentifier().getName()));
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                Node n = node.getChild(i);
                FixedBitSet childClade = rootedSupport(wTree, n, support);
                clade.union(childClade);
            }
        }

        Support s = support.get(clade);
        if (s == null) {
            s = new Support();
            support.put(clade, s);
        }
        s.add(wTree.getWeight(), TreeUtils.safeNodeHeight(wTree.getTree(), node), node.getBranchLength());
        return clade;
    }

    /**
     * Detach the children of a tree.
     * 
     * @param tree the tree
     * @param node the node to detach
     * @param split the split
     * 
     * @return the node
     */
    public Node detachChildren(Tree tree, Node node, List<Integer> split) {
        assert (split.size() > 1);

        List<Node> detached = new ArrayList<Node>();

        for (int n : split) {
            detached.add(node.getChild(n));
        }

        Node saveRoot = tree.getRoot();

        List<Integer> toRemove = new ArrayList<Integer>();
        for (int i = 0; i < node.getChildCount(); i++) {
            Node n = node.getChild(i);
            if (detached.contains(n)) {
                toRemove.add(0, i);
            }
        }
        for (int i : toRemove) {
            node.removeChild(i);
        }

        Node dnode = NodeFactory.createNode(detached.toArray(new Node[0]));
        node.addChild(dnode);

        tree.setRoot(saveRoot);

        return dnode;
    }

    /**
     * Builds the consensus tree over a set of weighted trees.
     * 
     * @param supportThreshold the minimum support to consider a split into the consensus tree
     * 
     * @return the consensus tree
     */
    private Tree buildTree(double supportThreshold, BranchDistances branchDistances) {

        if (trees.size() == 0) {
            throw new ProtTestInternalException("There are no trees to consense");
        }

        if (supportThreshold < 0.5 || supportThreshold > 1.0) {
            throw new ProtTestInternalException("Invalid threshold value: " + supportThreshold);
        }
        // establish support
        support = new HashMap<FixedBitSet, Support>();
        int k = 0;
        for (WeightedTree wTree : trees) {
            rootedSupport(wTree, wTree.getTree().getRoot(), support);

            ++k;

        }

        Tree cons = new SimpleTree();

        // Contains all internal nodes in the tree so far, ordered so descendants
        // appear later than ancestors
        List<Node> internalNodes = new ArrayList<Node>(numTaxa);

        // For each internal node, a bit-set with the complete set of tips for it's clade
        List<FixedBitSet> internalNodesTips = new ArrayList<FixedBitSet>(numTaxa);
        assert idGroup.getIdCount() == numTaxa;

        // establish a tree with one root having all tips as descendants
        internalNodesTips.add(new FixedBitSet(numTaxa));
        FixedBitSet rooNode = internalNodesTips.get(0);
        Node[] nodes = new Node[numTaxa];
        for (int nt = 0; nt < numTaxa; ++nt) {
            nodes[nt] = NodeFactory.createNode(idGroup.getIdentifier(nt));
            rooNode.set(nt);
        }

        Node rootNode = NodeFactory.createNode(nodes);
        internalNodes.add(rootNode);
        cons.setRoot(rootNode);
        // sorts support from largest to smallest
        final Comparator<Map.Entry<FixedBitSet, Support>> comparator = new Comparator<Map.Entry<FixedBitSet, Support>>() {

            public int compare(Map.Entry<FixedBitSet, Support> o1, Map.Entry<FixedBitSet, Support> o2) {
                double diff = o2.getValue().treesWeightWithClade - o1.getValue().treesWeightWithClade;
                if (diff > 0.0) {
                    return 1;
                } else if (diff < 0.0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        // add everything to queue
        PriorityQueue<Map.Entry<FixedBitSet, Support>> queue =
                new PriorityQueue<Map.Entry<FixedBitSet, Support>>(support.size(), comparator);

        for (Map.Entry<FixedBitSet, Support> se : support.entrySet()) {
            Support s = se.getValue();
            FixedBitSet clade = se.getKey();
            final int cladeSize = clade.cardinality();
            if (cladeSize == numTaxa) {
                // root
                cons.getRoot().setNodeHeight(s.sumBranches / trees.size());
                cons.getRoot().setBranchLength(branchDistances.build(s.branchLengths));
                continue;
            }

            if (Math.abs(s.treesWeightWithClade - this.cumWeight) < 1e-5 && cladeSize == 1) {
                // leaf/external node
                final int nt = clade.nextOnBit(FIRST);
                final Node leaf = cons.getExternalNode(nt);
                leaf.setNodeHeight(s.sumBranches / trees.size());
                leaf.setBranchLength(branchDistances.build(s.branchLengths));
            } else {
                queue.add(se);
            }
        }

        while (queue.peek() != null) {
            Map.Entry<FixedBitSet, Support> e = queue.poll();
            final Support s = e.getValue();

            final double psupport = (1.0 * s.treesWeightWithClade) / cumWeight;
            if (psupport < supportThreshold) {
                break;
            }

            final FixedBitSet cladeTips = e.getKey();

            boolean found = false;

            /* locate the node containing the clade. going in reverse order 
            ensures the lowest one is hit first */
            for (int nsub = internalNodesTips.size() - 1; nsub >= 0; --nsub) {

                FixedBitSet allNodeTips = internalNodesTips.get(nsub);

                // size of intersection between tips & split
                final int nSplit = allNodeTips.intersectCardinality(cladeTips);

                if (nSplit == cladeTips.cardinality()) {
                    // node contains all of clade

                    // Locate node descendants containing the split
                    found = true;
                    List<Integer> split = new ArrayList<Integer>();

                    Node n = internalNodes.get(nsub);
                    int l = 0;

                    for (int j = 0; j < n.getChildCount(); j++) {
                        Node ch = n.getChild(j);

                        if (ch.isLeaf()) {
                            if (cladeTips.contains(idGroup.whichIdNumber(ch.getIdentifier().getName()))) {
                                split.add(l);
                            }
                        } else {
                            // internal
                            final int o = internalNodes.indexOf(ch);
                            final int i = internalNodesTips.get(o).intersectCardinality(cladeTips);
                            if (i == internalNodesTips.get(o).cardinality()) {
                                split.add(l);
                            } else if (i > 0) {
                                // Non compatible
                                found = false;
                                break;
                            }
                        }
                        ++l;
                    }


                    if (!(found && split.size() < n.getChildCount())) {
                        found = false;
                        break;
                    }

                    if (split.size() == 0) {
                        System.err.println("Bug??");
                        assert (false);
                    }

                    final Node detached = detachChildren(cons, n, split);

                    final double height = s.sumBranches / s.nTreesWithClade;
                    detached.setNodeHeight(height);
                    detached.setBranchLength(branchDistances.build(s.branchLengths));

                    cons.setAttribute(detached, TreeUtils.TREE_CLADE_SUPPORT_ATTRIBUTE, SUPPORT_AS_PERCENT ? 100 * psupport : psupport);

                    // insert just after parent, so before any descendants
                    internalNodes.add(nsub + 1, detached);
                    internalNodesTips.add(nsub + 1, new FixedBitSet(cladeTips));

                    break;
                }
            }
        }

        TreeUtils.insureConsistency(cons, cons.getRoot());

        String thresholdAsPercent = String.valueOf(supportThreshold * 100);
        cons.setAttribute(cons.getRoot(), TreeUtils.TREE_NAME_ATTRIBUTE,
                "cons_" + thresholdAsPercent + "_majRule");

        Set<FixedBitSet> keySet = getSupport().keySet();
        FixedBitSet[] keys = keySet.toArray(new FixedBitSet[0]);
        Arrays.sort(keys);

        for (FixedBitSet fbs : keys) {
            if (fbs.cardinality() > 1) {
                double psupport = (1.0 * getSupport().get(fbs).getTreesWeightWithClade()) / cumWeight;
                if (psupport < supportThreshold) {
                    splitsOutFromConsensus.add(fbs);
                } else {
                    splitsInConsensus.add(fbs);
                }
            }
        }

        return cons;

    }

    /**
     * Enum to calculate the branch lengths
     */
    private enum BranchDistances {

        WeightedAverage {

            /**
             * Calculates the weighted average.
             * 
             * @param values the weighted values
             * @param cumWeight the sum of weights
             * 
             * @return the weighted average of the set
             */
            public double build(List<WeightLengthPair> values) {
                double avg = 0.0;
                double cumWeight = 0.0;
                for (WeightLengthPair pair : values) {
                    avg += pair.branchLength * pair.weight;
                    cumWeight += pair.weight;
                }
                avg /= cumWeight;
                return avg;
            }
        },
        WeightedMedian {

            /**
             * Calculates the weighted median.
             * 
             * @param values the weighted values
             * @param cumWeight the sum of weights
             * 
             * @return the weighted median of the set
             */
            public double build(List<WeightLengthPair> values) {
                Collections.sort(values);
                double median = -1;
                double cumWeight = 0.0;
                for (WeightLengthPair pair : values) {
                    cumWeight += pair.weight;
                }
                double halfWeight = cumWeight / 2.0;
                double cumValue = 0.0;
                for (WeightLengthPair pair : values) {
                    cumValue += pair.weight;
                    if (cumValue >= halfWeight) {
                        median = pair.branchLength;
                        break;
                    }
                }
                return median;
            }
        };

        public abstract double build(List<WeightLengthPair> values);
    }

    /**
     * One clade support.
     */
    static final class Support {

        /** number of trees containing the clade. */
        private int nTreesWithClade;
        /** The trees weight with clade. */
        private double treesWeightWithClade;
        /** The branch lengths. */
        private ArrayList<WeightLengthPair> branchLengths;
        /** Sum of node heights of trees containing the clade. */
        private double sumBranches;

        public double getTreesWeightWithClade() {
            return treesWeightWithClade;
        }

        /**
         * Instantiates a new support.
         */
        Support() {
            sumBranches = 0.0;
            treesWeightWithClade = 0.0;
            nTreesWithClade = 0;
            branchLengths = new ArrayList<WeightLengthPair>();
        }

        /**
         * Adds the branch to the map of branch lengths.
         * 
         * @param weight the weight
         * @param height the height
         * @param branchLength the branch length
         */
        public final void add(double weight, double height, double branchLength) {
            sumBranches += height;
            branchLengths.add(new WeightLengthPair(weight, branchLength));
            treesWeightWithClade += weight;
            ++nTreesWithClade;

            double testW = 0.0;
            for (WeightLengthPair wlp : branchLengths) {
                testW += wlp.weight;
            }
        }
    }

    static class WeightLengthPair implements Comparable<WeightLengthPair> {

        private double weight;
        private double branchLength;

        WeightLengthPair(double weight, double branchLength) {
            this.weight = weight;
            this.branchLength = branchLength;
        }

        @Override
        public int compareTo(WeightLengthPair o) {
            if (branchLength < o.branchLength) {
                return -1;
            } else if (branchLength > o.branchLength) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * A extension of Weighted tree but every tree
     * has the same weight.
     */
    static class UnweightedTree extends WeightedTree {

        /**
         * Instantiates a new unweighted tree.
         * 
         * @param tree the tree
         */
        UnweightedTree(Tree tree) {
            super(tree, 1.0);
        }
    }

    /**
     * The main method. This method allows testing of consensus tree
     * building. It requires at least 2 arguments.
     * 
     * @param args Tree set filename (Newick's), threshold, [avg | median]
     */
    public static void main(String[] args) {

        PrintWriter out = new PrintWriter(System.out);

        if (args.length < 2 || args[0].contains("help")) {
            out.println("This class requires at least 2 arguments: Tree set filename, Threshold value" + " and optionally the branch length calculation method [avg (default), median]");
            out.println("The file format should be:");
            out.println("    · (Newick's tree)[Weight];");
            out.println("    · Nexus format tree set");
            out.flush();
            System.exit(-1);
        }

        String filename = args[0];
        Double threshold = Double.parseDouble(args[1]);
        File f = new File(filename);
        try {
            Consensus consensus;
            if (args.length >= 3) {
                if (args[2].equalsIgnoreCase("median")) {
                    consensus = new Consensus(f, threshold, BRANCH_LENGTHS_MEDIAN);
                } else if (args[2].equalsIgnoreCase("avg")) {
                    consensus = new Consensus(f, threshold, BRANCH_LENGTHS_AVERAGE);
                } else {
                    consensus = new Consensus(f, threshold);
                }
            } else {
                consensus = new Consensus(f, threshold);
            }
            Tree consensusTree = consensus.getConsensusTree();
            out.println("");

            Set<FixedBitSet> keySet = consensus.getSupport().keySet();
            FixedBitSet[] keys = keySet.toArray(new FixedBitSet[0]);
            List<FixedBitSet> splitsInConsensus = new ArrayList<FixedBitSet>();
            List<FixedBitSet> splitsOutFromConsensus = new ArrayList<FixedBitSet>();

            Arrays.sort(keys);

            for (FixedBitSet fbs : keys) {
                if (fbs.cardinality() > 1) {
                    double psupport = (1.0 * consensus.getSupport().get(fbs).getTreesWeightWithClade()) / consensus.cumWeight;
                    if (psupport < threshold) {
                        splitsOutFromConsensus.add(fbs);
                    } else {
                        splitsInConsensus.add(fbs);
                    }
                }
            }

            out.println("# # # # # # # # # # # # # # # #");
            out.println(" ");
            out.println("Species in order:");
            out.println(" ");

            for (int i = 0; i < consensus.getIdGroup().getIdCount(); i++) {
                Identifier id = consensus.getIdGroup().getIdentifier(i);
                out.println("    " + (i + 1) + ". " + id.getName());
            }
            out.println(" ");
            out.println("# # # # # # # # # # # # # # # #");
            out.println(" ");
            out.println("Sets included in the consensus tree");
            out.println(" ");
            out.print("    ");

            int numTaxa = consensus.getIdGroup().getIdCount();
            for (int i = 0; i < consensus.getIdGroup().getIdCount(); i++) {
                out.print(String.valueOf(i + 1).charAt(0));
            }
            out.println(" ");
            if (numTaxa >= 10) {
                ProtTestFormattedOutput.space(4 + 9, ' ');
                for (int i = 9; i < consensus.getIdGroup().getIdCount(); i++) {
                    out.print(String.valueOf(i + 1).charAt(1));
                }
            }
            out.println(" ");
            for (FixedBitSet fbs : splitsInConsensus) {
                out.println("    " + fbs.splitRepresentation() + " ( " + Utilities.round(consensus.getSupport().get(fbs).getTreesWeightWithClade(), 3) + " )");
            }
            out.println(" ");
            out.println("Sets NOT included in consensus tree");
            out.println(" ");
            out.print("    ");
            for (int i = 0; i < consensus.getIdGroup().getIdCount(); i++) {
                out.print(i + 1);
            }
            out.println(" ");
            for (FixedBitSet fbs : splitsOutFromConsensus) {
                out.println("    " + fbs.splitRepresentation() + " ( " + Utilities.round(consensus.getSupport().get(fbs).getTreesWeightWithClade(), 3) + " )");
            }
            out.println(" ");
            out.println("# # # # # # # # # # # # # # # #");

            TreeUtils.printASCII(consensusTree, out);
            out.println(" ");
            TreeUtils.printBranchInfo(consensusTree, out);
            out.println(" ");
            TreeUtils.heightInfo(consensusTree, out);
            out.println(" ");
            out.println("# # # # # # # # # # # # # # # #");
            out.println(" ");
            out.println(TreeUtils.toNewick(consensusTree, true, true, true));
            out.println(" ");
            out.println("# # # # # # # # # # # # # # # #");
            out.println(" ");
        } catch (FileNotFoundException e) {
            out.println("File not found: " + filename);
        } catch (IOException e1) {
            out.println("IO Error: " + e1.getMessage());
        }
        out.flush();
    }

    public String getTaxaHeader() {
        StringBuilder taxaHeader = new StringBuilder();
        for (int i = 0; i < numTaxa; i++) {
            taxaHeader.append(String.valueOf(i + 1).charAt(0));
        }
        if (numTaxa >= 10) {
            taxaHeader.append('\n');
            taxaHeader.append(ProtTestFormattedOutput.space(4 + 9, ' '));
            for (int i = 9; i < numTaxa; i++) {
                taxaHeader.append(String.valueOf(i + 1).charAt(1));
            }
        }
        if (numTaxa >= 100) {
            taxaHeader.append('\n');
            taxaHeader.append(ProtTestFormattedOutput.space(4 + 99, ' '));
            for (int i = 99; i < numTaxa; i++) {
                taxaHeader.append(String.valueOf(i + 1).charAt(3));
            }
        }

        return taxaHeader.toString();
    }

    public String getSetsIncluded() {
        StringBuilder setsIncluded = new StringBuilder();
        setsIncluded.append("    ");
        setsIncluded.append(getTaxaHeader());

        setsIncluded.append('\n');
        for (FixedBitSet fbs : splitsInConsensus) {
            setsIncluded.append("    ")
                    .append(fbs.splitRepresentation())
                    .append(" ( ")
                    .append(Utilities.round(getCladeSupport().get(fbs), 5))
                    .append(" )")
                    .append('\n');
        }
        return setsIncluded.toString();
    }

    public String getSetsNotIncluded() {
        StringBuilder setsIncluded = new StringBuilder();
        setsIncluded.append("    ");
        setsIncluded.append(getTaxaHeader());

        setsIncluded.append('\n');
        for (FixedBitSet fbs : splitsOutFromConsensus) {
            setsIncluded.append("    ")
                    .append(fbs.splitRepresentation())
                    .append(" ( ")
                    .append(Utilities.round(getCladeSupport().get(fbs), 5))
                    .append(" )")
                    .append('\n');
        }
        return setsIncluded.toString();
    }

    private BranchDistances getBranchDistances(int value) {
        BranchDistances bd;
        switch (value) {
            case BRANCH_LENGTHS_AVERAGE:
                bd = BranchDistances.WeightedAverage;
                break;
            case BRANCH_LENGTHS_MEDIAN:
                bd = BranchDistances.WeightedMedian;
                break;
            default:
                // Weighted average
                bd = DEFAULT_BRANCH_DISTANCES;
        }
        return bd;
    }
}
