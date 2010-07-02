package es.uvigo.darwin.prottest.model.state;

import java.io.Serializable;

/**
 * The Class ModelLkState defines the behavior of the model in relation to the
 * likelihood value.
 */
public abstract class ModelLkState implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2199513513876803640L;

	/**
	 * Gets the likelihood of the model.
	 * 
	 * @return the likelihood logarithm value.
	 */
	public abstract double getLk();
	
	/**
	 * Sets the likelihood of the model.
	 * 
	 * @param lk the likelihood logarithm value.
	 * 
	 * @return the new state of the model.
	 */
	public abstract ModelLkState setLk(double lk);
	
	/**
	 * Gets the alpha estimated value.
	 * 
	 * @return the alpha estimated value
	 */
	public abstract double getAlpha();

	/**
	 * Sets the alpha estimated value.
	 * 
	 * @param alpha the new alpha estimated value
         *
         * @return the new state
	 */
	public abstract ModelLkState setAlpha(double alpha);

	/**
	 * Gets the proportion of invariant sites.
	 * 
	 * @return the proportion of invariant sites
	 */
	public abstract double getInv();

	/**
	 * Sets the proportion of invariant sites.
	 * 
	 * @param inv the new proportion of invariant sites
         * 
         * @return the new state
	 */
	public abstract ModelLkState setInv(double inv);
}
