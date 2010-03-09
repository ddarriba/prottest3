package es.uvigo.darwin.prottest.tree;

public class TreeEuclideanDistancesCache extends TreeDistancesCache {

	private static TreeEuclideanDistancesCache instance;
	
	private TreeEuclideanDistancesCache() {
		super(EUCLIDEAN);
	}
	
	public static TreeEuclideanDistancesCache getInstance() {
		
		if (instance == null)
			instance = new TreeEuclideanDistancesCache();
		return instance;
		
	}


}
