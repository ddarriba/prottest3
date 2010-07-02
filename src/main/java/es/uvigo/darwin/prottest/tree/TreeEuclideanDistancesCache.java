package es.uvigo.darwin.prottest.tree;

/**
 * This class is a distances cache implementing the euclidean distance
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
 */
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
