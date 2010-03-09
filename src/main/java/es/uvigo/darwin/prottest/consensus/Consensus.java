package es.uvigo.darwin.prottest.consensus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;

import jebl.util.FixedBitSet;
import pal.misc.IdGroup;
import pal.tree.Node;
import pal.tree.NodeFactory;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.model.SelectionModel;
import es.uvigo.darwin.prottest.tree.TreeUtils;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import es.uvigo.darwin.prottest.util.fileio.AlignmentReader;

/**
 * Phylogenetic consensus tree builder.
 */
public class Consensus {

	/** The Constant FIRST. */
	private static final int FIRST = 0;
	
	/** Name of attribute specifing amount of support for branch. */
    final static public String DEFAULT_SUPPORT_ATTRIBUTE_NAME = "Consensus support(%)";
	
    /** The Constant SUPPORT_AS_PERCENT. */
    final static public boolean SUPPORT_AS_PERCENT = true;
    
	/** The trees. */
	private List<WeightedTree> trees;
	
	/** The cum weight. */
	private double cumWeight = 0.0;
	
	/** The num taxa. */
	private int numTaxa;
	
	/** The id group. */
	private IdGroup idGroup;
	
	/**
	 * Gets the set of trees included in the consensus.
	 * 
	 * @return the trees
	 */
	public Collection<WeightedTree> getTrees() { return trees; }
	
	/**
	 * Adds a weighted tree to the set.
	 * 
	 * @param wTree the weighted tree
	 * 
	 * @return true, if successful
	 */
	public boolean addTree(WeightedTree wTree) {
		//check integrity
		if (wTree.tree == null || wTree.weight < 0.0)
			throw new ProtTestInternalException();
		//check compatibility
		if (trees.size() == 0) {
			trees.add(wTree);
			numTaxa = wTree.tree.getIdCount();
			idGroup = pal.tree.TreeUtils.getLeafIdGroup(wTree.tree);
		}
		else {
			if (wTree.tree.getIdCount() != numTaxa) {
				return false;
			}
			Tree pTree = trees.get(FIRST).tree;
			for (int i = 0; i < numTaxa; i++) {
				boolean found = false;
				for (int j = 0; j < numTaxa; j++) {
					if (wTree.tree.getIdentifier(i).equals(pTree.getIdentifier(j))) {
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
		cumWeight += wTree.weight;
		return true;
	}
	
	/**
	 * Instantiates a new consensus tree builder.
	 * 
	 * @param ic the information criterion to build the weighted trees
	 */
	public Consensus(InformationCriterion ic) {
		this.trees = new ArrayList<WeightedTree>();
		for (SelectionModel model : ic.getConfidenceModels()) {
			WeightedTree wTree = new WeightedTree(
					model.getModel().getTree(),
					model.getWeightValue());
			this.addTree(wTree);
		}
	}
	
	/**
	 * Instantiates a new unweighted consensus builder.
	 * 
	 * @param trees the trees
	 */
	public Consensus(List<Tree> trees) {
		this.trees = new ArrayList<WeightedTree>();
		for (Tree tree : trees)
			this.addTree(new UnweightedTree(tree));
	}
	
	/**
	 * Instantiates a new unweighted consensus builder.
	 * 
	 * @param treesFile the file with the set of trees in Newick format
	 * @throws IOException 
	 */
	public Consensus(File treesFile) 
		throws ProtTestInternalException, IOException {
		
		FileReader fr = new FileReader(treesFile);
		BufferedReader br = new BufferedReader(fr);
		
		//PushbackReader in = new PushbackReader(fr);

		AlignmentReader reader = new AlignmentReader();
		
		trees = new ArrayList<WeightedTree>();
		try {
			while (br.ready()) {
				String line = br.readLine();
				if (line == null || line.equals("")) {
					break;
				}

				line = line.replace('[', ';');
				line = line.replace(']', ';');
				String[] values = line.split(";");
				String weightStr = values[1];;
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
				this.addTree(new WeightedTree(tree, weight));
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
        s.add(wTree.weight, TreeUtils.safeNodeHeight(wTree.tree, node), node.getBranchLength());
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
        assert( split.size() > 1 );

        List<Node> detached = new ArrayList<Node>();

        for( int n : split ) {
        	detached.add(node.getChild(n));
        }

        Node saveRoot = tree.getRoot();

        List<Integer> toRemove = new ArrayList<Integer>();
        for (int i = 0; i < node.getChildCount(); i++) {
        	Node n = node.getChild(i);
            if( detached.contains(n) ) {
            	toRemove.add(0,i);
            }
        }
        for (int i : toRemove)
        	node.removeChild(i);

        Node dnode = NodeFactory.createNode(detached.toArray(new Node[0]));
        node.addChild(dnode);

        tree.setRoot(saveRoot);

        return dnode;
    }
	
	/**
	 * Calculates the weighted median.
	 * 
	 * @param values the weighted values
	 * @param cumWeight the sum of weights
	 * 
	 * @return the weighted median of the set
	 */
	public static double median(SortedSet<WeightLengthPair> values, double cumWeight) {
		double median = -1;
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
	
	/**
	 * Builds the consensus tree over a set of weighted trees.
	 * 
	 * @param supportThreshold the minimum support to consider a split into the consensus tree
	 * 
	 * @return the consensus tree
	 */
	public Tree buildTree(double supportThreshold) {
		
		if (trees.size() == 0)
        	throw new ProtTestInternalException("There are no trees to consense");
		
		if (supportThreshold < 0.5 || supportThreshold > 1.0)
			throw new ProtTestInternalException("Invalid threshold value: " + supportThreshold);
		// establish support
        Map<FixedBitSet, Support> support = new HashMap<FixedBitSet, Support>();
        int k = 0;
        for (WeightedTree wTree : trees) {
            rootedSupport(wTree, wTree.tree.getRoot(), support);

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
                if (diff > 0.0)
                	return 1;
                else if (diff < 0.0)
                	return -1;
                else
                	return 0;
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
            	cons.getRoot().setBranchLength(median(s.branchLengths, s.treesWeightWithClade));
                continue;
            }

            if (s.treesWeightWithClade == this.cumWeight && cladeSize == 1) {
                // leaf/external node
                final int nt = clade.nextOnBit(0);
                //TODO: Comprobar que nt corresponde a idGroup[nt];
                final Node leaf = cons.getExternalNode(nt); 
                leaf.setNodeHeight(s.sumBranches / trees.size());
                leaf.setBranchLength(median(s.branchLengths, s.treesWeightWithClade));
            } else {
                queue.add(se);
            }
        }
        
        System.out.println("");
        
        while (queue.peek() != null) {
            Map.Entry<FixedBitSet, Support> e = queue.poll();
            final Support s = e.getValue();

            final double psupport = (1.0 * s.treesWeightWithClade) / cumWeight;
            if (psupport < supportThreshold) {
                break;
            }

            final FixedBitSet cladeTips = e.getKey();

            boolean found = false;

            // locate the node containing the clade. going in reverse order insures the lowest one is hit first
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


                    if (! (found && split.size() < n.getChildCount())) {
                        found = false;
                        break;
                    }

                    if (split.size() == 0) {
                        System.err.println("Bug??");
                        assert(false);
                    }

                    final Node detached = detachChildren(cons, n, split);

                    final double height = s.sumBranches / s.nTreesWithClade;
                    detached.setNodeHeight(height);
                    detached.setBranchLength(median(s.branchLengths, s.treesWeightWithClade));

                    cons.setAttribute(detached, DEFAULT_SUPPORT_ATTRIBUTE_NAME, SUPPORT_AS_PERCENT ? 100 * psupport : psupport);

                    // insert just after parent, so before any descendants
                    internalNodes.add(nsub + 1, detached);
                    internalNodesTips.add(nsub + 1, new FixedBitSet(cladeTips));

                    PrintWriter out = new PrintWriter(System.out);
                    pal.tree.TreeUtils.printNH(new SimpleTree(detached), out, false, false);
                    out.flush();
                    System.out.println("Support: "+psupport+" " + detached.getIdentifier().getName());
                    break;
                }
            }
            System.out.println("");            
            
            if (psupport >= .5 && ! found) {
                System.err.println("Bug??");
                assert(false);
            }
	        
        }

        System.out.println("#################");
        
        TreeUtils.insureConsistency(cons, cons.getRoot());
        
        return cons;
        
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
        private SortedSet<WeightLengthPair> branchLengths;
        
        /** Sum of node heights of trees containing the clade. */
        private double sumBranches;
        
        /**
         * Instantiates a new support.
         */
        Support() {
            sumBranches = 0.0;
            treesWeightWithClade = 0.0;
            nTreesWithClade = 0;
            branchLengths = new TreeSet<WeightLengthPair>();
        }

        /**
         * Adds the.
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
			if (branchLength < o.branchLength)
				return -1;
			else if (branchLength > o.branchLength)
				return 1;
			return 0;
		}
    }
    
    /**
     * The Class WeightedTree.
     */
    static class WeightedTree {

        /** The tree. */
        private Tree tree;
        
        /** The weight. */
        private double weight;

        /**
         * Instantiates a new weighted tree.
         * 
         * @param tree the tree
         * @param weight the weight
         */
        WeightedTree(Tree tree, double weight) {
        	this.tree = tree;
        	this.weight = weight;
        }
        
    }
    
    /**
     * The Class UnweightedTree.
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
    
    public static void main(String[] args) {
    	
    	PrintWriter out = new PrintWriter(System.out);
    	
    	if (args.length != 2 || args[0].contains("help")) {
    		out.println("This class requires 2 arguments: [Tree set filename] and [Threshold value]");
    		out.println("The file format should be:");
    		out.println("    (Newick's tree)[Weight];");
    		out.println("    ...");
    		out.flush();
    		System.exit(-1);
    	}

    	String filename = args[0];
    	Double threshold = Double.parseDouble(args[1]);
    	File f = new File(filename);
		try {
			Consensus consensus = new Consensus(f);
			Tree consensusTree = consensus.buildTree(threshold);
			out.println("");
			pal.tree.TreeUtils.report(consensusTree, out);
			pal.tree.TreeUtils.printNH(consensusTree, out, true, true);
		} catch (FileNotFoundException e) {
			out.println("File not found: " + filename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		out.flush();
    }
    
//    // Main() for median method test
//    public static void main(String[] args) {
//    	Support s = new Support();
//    	double[] weights = {0.5, 0.1, 0.1, 0.2, 0.1};
//    	double[] lengths = {7, 4, 1, 3, 2};
//    	for (int i = 0; i<5; i++)
//    		s.add(weights[i], 0.0, lengths[i]);
//    	double median = median(s.branchLengths, 1.0);
//    	
//    	System.out.println("The median is " + median);
//    }    
	
}
