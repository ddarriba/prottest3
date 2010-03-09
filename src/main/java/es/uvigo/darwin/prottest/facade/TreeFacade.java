package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.util.List;

import pal.tree.Tree;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.selection.InformationCriterion;

public interface TreeFacade {

	//	**********************************************************
	//		  TREE SERVICES
	//**********************************************************
	
	/**
	* Displays Newick tree according to a Tree instance.
	* 
	* @param tree the tree to display
	* @param outputWriter the output writer where the output will be written
	*/
	public void displayNewickTree(Tree tree,
	        PrintWriter outputWriter);
	
	/**
	* Display ASCII tree according to a Tree instance.
	* 
	* @param tree the tree to display
	* @param outputWriter the output writer where the output will be written
	*/
	public void displayASCIITree(Tree tree,
	        PrintWriter outputWriter);
	
	/**
	* Build and display an unweighted consensus tree over the trees of each model,
	* in Newick and ASCII format.
	* 
	* @param modelList the models with the trees to build consensus
	* @param threshold the minimum clade support
	* @param outputWriter the output writer where the output will be written
	*/
	public void displayConsensusTree(List<Model> modelList,
	        double threshold, PrintWriter outputWriter);
	
	/**
	* Build and display a weighted consensus tree over the trees of each model,
	* in Newick and ASCII format.
	* 
	* @param ic the weighted models with the trees to build consensus
	* @param threshold the minimum clade support
	* @param outputWriter the output writer where the output will be written
	*/
	public void displayWeightedConsensusTree(InformationCriterion ic,
	        double threshold, PrintWriter outputWriter);
	
	/**
	* Create a consensus tree from a list of trees.
	* 
	* @param treeCollection the trees to build consensus
	* @param threshold the minimum clade support
	*/
	public Tree createConsensusTree(List<Tree> treeCollection, double threshold);
	
	/**
	* Create a weighted consensus tree from a list of trees.
	* 
	* @param ic the weighted models with the trees to build consensus
	* @param threshold the minimum clade support
	*/
	public Tree createConsensusTree(InformationCriterion ic, double threshold);
}
