/**
 * 
 */
package es.uvigo.darwin.prottest.util.exception;

/**
 * The Class AlignmentParseException.
 * 
 * @author Diego Darriba López
 */
public class AlignmentParseException extends ProtTestCheckedException {

    private static final String MESSAGE = "Alignment parse exception";
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 20090728L;

    /**
     * Instantiates a new alignment format exception.
     */
    public AlignmentParseException() {
        super(MESSAGE);
    }

    /**
     * Instantiates a new alignment format exception.
     * 
     * @param description the description
     */
    public AlignmentParseException(String description) {
        super(MESSAGE + ": " + description);
    }
}
