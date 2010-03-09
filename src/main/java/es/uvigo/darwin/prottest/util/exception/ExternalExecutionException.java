package es.uvigo.darwin.prottest.util.exception;

public class ExternalExecutionException extends ProtTestInternalException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 20090728L;

	/**
	 * Instantiates a new external execution exception.
	 */
	public ExternalExecutionException() {}
	
	/**
	 * Instantiates a new external execution exception.
	 * 
	 * @param description the description
	 */
	public ExternalExecutionException(String description) {
		super(description);
	}
}
