package es.uvigo.darwin.prottest.util.collection;

import es.uvigo.darwin.prottest.model.Model;
import pal.alignment.Alignment;

/**
 * A single implementation of ModelCollection.
 */
public class SingleModelCollection extends ModelCollection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 20090804L;

	/**
	 * Instantiates a new single model collection.
	 */
	public SingleModelCollection(Alignment alignment) {
		super(alignment);
	}
	
	/**
	 * Instantiates a new single model collection.
	 * 
	 * @param models the models
	 */
	public SingleModelCollection(Model[] models, Alignment alignment) {
		super(models, alignment);
	}

}
