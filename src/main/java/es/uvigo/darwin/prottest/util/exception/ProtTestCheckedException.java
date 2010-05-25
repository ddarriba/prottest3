/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.util.exception;

/**
 *
 * @author Diego Darriba
 */
public class ProtTestCheckedException extends Exception {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 20100525L;

	/**
	 * Instantiates a new prot test internal exception.
	 */
	public ProtTestCheckedException() {}
	
	/**
	 * Instantiates a new prot test internal exception.
	 * 
	 * @param description the description
	 */
	public ProtTestCheckedException(String description) {
		super(description);
	}
}
