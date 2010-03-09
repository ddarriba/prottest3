package es.uvigo.darwin.prottest.tree;

import java.util.Hashtable;

import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

import pal.tree.Tree;

public class TreeDistancesCache {

	public static final int EUCLIDEAN = 1;
	public static final int ROBINSON_FOULDS = 2;
	
	private Hashtable<TreePair, Double> distances;
	private int distanceType;
	
	public int getDistanceType() {
		return distanceType;
	}
	
	public TreeDistancesCache(int distanceType) {

		if (distanceType != EUCLIDEAN && distanceType != ROBINSON_FOULDS)
			throw new ProtTestInternalException("Unsupported distance type");
		this.distanceType = distanceType;
		distances = new Hashtable<TreePair, Double>();
		
	}
	
	public double getDistance(Tree t1, Tree t2) {
		double distance = 0.0;
		TreePair tp = new TreePair(t1, t2);
		if (distances.containsKey(tp))
			distance = distances.get(tp);
		else {
			switch (distanceType) {
			case EUCLIDEAN:
				distance = TreeUtils.euclideanTreeDistance(t1, t2);
				break;
			case ROBINSON_FOULDS:
				distance = TreeUtils.robinsonFouldsTreeDistance(t1, t2);
				break;
			}
			distances.put(tp, distance);
		}
		return distance;
	}
	
	private class TreePair {
		
		private Tree t1, t2;
		
		public TreePair(Tree t1, Tree t2) {
			this.t1 = t1;
			this.t2 = t2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
			result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
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
			TreePair other = (TreePair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (t1 == null) {
				if (other.t1 != null)
					return false;
			} else if (!t1.equals(other.t1))
				return false;
			if (t2 == null) {
				if (other.t2 != null)
					return false;
			} else if (!t2.equals(other.t2))
				return false;
			return true;
		}

		private TreeDistancesCache getOuterType() {
			return TreeDistancesCache.this;
		}
		
	}
}
