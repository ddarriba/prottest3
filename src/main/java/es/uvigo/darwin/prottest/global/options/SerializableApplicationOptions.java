	//  ProtTest project
	//
	//  Created by Federico Abascal on Thu Jun 10 2004.
	//
	package es.uvigo.darwin.prottest.global.options;

	import java.io.Serializable;
import java.util.Vector;

import pal.alignment.Alignment;
import pal.tree.Tree;

	public class SerializableApplicationOptions implements Serializable {

		private static final long serialVersionUID = 7758895101915536602L;

		/** The alignment, according to the alignment input file. */
		protected Alignment	alignment;
		
		/** The tree, according to the tree filename. */
		protected Tree tree;
		
		/** The number of categories. It is only useful if the distribution is gamma */
		public int numberOfCategories;
		
		/** The optimization strategy mode. */
		public int strategyMode;

	    /** The matrices of the models to optimize. */
	    private Vector<String> matrices;
	    
	    /** The distributions of the models to optimize. */
	    private Vector<Integer> distributions;
	    
	    /** Boolean value to consider or not different kind of amino-acid frequencies. */
	    private boolean plusF; 

	    public Alignment getAlignment() {
			return alignment;
		}

		public Tree getTree() {
			return tree;
		}

		public int getNumberOfCategories() {
			return numberOfCategories;
		}

		public int getStrategyMode() {
			return strategyMode;
		}

		public Vector<String> getMatrices() {
			return matrices;
		}

		public Vector<Integer> getDistributions() {
			return distributions;
		}

		public boolean isPlusF() {
			return plusF;
		}

		public SerializableApplicationOptions(ApplicationOptions options) {
	    	this.alignment = options.getAlignment();
	    	this.tree = options.getTree();
	    	this.numberOfCategories = options.ncat;
	    	this.strategyMode = options.strategyMode;
	    	this.matrices = options.getMatrices();
	    	this.distributions = options.getDistributions();
	    	this.plusF = options.isPlusF();
	    }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((alignment == null) ? 0 : alignment.hashCode());
			result = prime * result
					+ ((distributions == null) ? 0 : distributions.hashCode());
			result = prime * result
					+ ((matrices == null) ? 0 : matrices.hashCode());
			result = prime * result + numberOfCategories;
			result = prime * result + (plusF ? 1231 : 1237);
			result = prime * result + strategyMode;
			result = prime * result + ((tree == null) ? 0 : tree.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SerializableApplicationOptions other = (SerializableApplicationOptions) obj;
			if (alignment == null) {
				if (other.alignment != null)
					return false;
			} else if (!alignment.toString().equals(other.alignment.toString()))
				return false;
			if (distributions == null) {
				if (other.distributions != null)
					return false;
			} else if (!distributions.equals(other.distributions))
				return false;
			if (matrices == null) {
				if (other.matrices != null)
					return false;
			} else if (!matrices.equals(other.matrices))
				return false;
			if (numberOfCategories != other.numberOfCategories)
				return false;
			if (plusF != other.plusF)
				return false;
			if (strategyMode != other.strategyMode)
				return false;
			if (tree == null) {
				if (other.tree != null)
					return false;
			} 
//			else if (!tree.equals(other.tree))
//				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SerializableApplicationOptions [alignment=" + alignment
					+ ", distributions=" + distributions + ", matrices="
					+ matrices + ", numberOfCategories=" + numberOfCategories
					+ ", plusF=" + plusF + ", strategyMode=" + strategyMode
					+ ", tree=" + tree + "]";
		}
		
}
