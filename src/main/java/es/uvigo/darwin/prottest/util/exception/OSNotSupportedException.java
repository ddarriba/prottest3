/**
 * 
 */
package es.uvigo.darwin.prottest.util.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class OSNotSupportedException.
 * 
 * @author Diego Darriba López
 */
public class OSNotSupportedException extends ProtTestInternalException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 20090728L;

	/**
	 * Instantiates a new oS not supported exception.
	 */
	public OSNotSupportedException() {
		super("Error: this operating system (" 
			+ System.getProperty("os.name") 
			+ ") is not supported by ProtTest.");
	}
	
	/**
	 * Instantiates a new oS not supported exception.
	 * 
	 * @param description the description
	 */
	public OSNotSupportedException(String description) {
		super(description);
	}

}
