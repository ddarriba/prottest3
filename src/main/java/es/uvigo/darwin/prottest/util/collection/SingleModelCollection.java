package es.uvigo.darwin.prottest.util.collection;

import es.uvigo.darwin.prottest.model.Model;

/**
 * A single implementation of ModelCollection.
 */
public class SingleModelCollection extends ModelCollection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 20090804L;

	/**
	 * Instantiates a new single model collection.
	 */
	public SingleModelCollection() {
		super();
	}
	
	/**
	 * Instantiates a new single model collection.
	 * 
	 * @param models the models
	 */
	public SingleModelCollection(Model[] models) {
		super(models);
	}

}
