/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.util.exception;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: ImportException.java 609 2007-01-08 00:40:49Z pepster $
 */
public class ImportException extends Exception {
	public ImportException() { super(); }
	public ImportException(String message) { super(message); }
    public String userMessage() { return getMessage(); }

    public static class DuplicateFieldException extends ImportException {
		public DuplicateFieldException() { super(); }
		public DuplicateFieldException(String message) { super(message); }
	}

	public static class BadFormatException extends ImportException {
		public BadFormatException() { super(); }
		public BadFormatException(String message) { super(message); }
	}

	public static class UnparsableDataException extends ImportException {
		public UnparsableDataException() { super(); }
		public UnparsableDataException(String message) { super(message); }
	}

	public static class MissingFieldException extends ImportException {
		public MissingFieldException() { super(); }
		public MissingFieldException(String message) { super(message); }
        public String userMessage() { return "Unsupported value for field " + getMessage(); }
	}

	public static class ShortSequenceException extends ImportException {
		public ShortSequenceException() { super(); }
		public ShortSequenceException(String message) { super(message); }
        public String userMessage() { return "Sequence is too short: " + getMessage(); }
    }

	public static class TooFewTaxaException extends ImportException {
		public TooFewTaxaException() { super(); }
		public TooFewTaxaException(String message) { super(message); }
        public String userMessage() { return "Number of taxa is less than expected: " +
                (getMessage() != null ? getMessage() : ""); }
    }

    public static class DuplicateTaxaException extends ImportException {
		public DuplicateTaxaException() { super(); }
		public DuplicateTaxaException(String message) { super(message); }
	}

    public static class UnknownTaxonException extends ImportException {
		public UnknownTaxonException() { super(); }
		public UnknownTaxonException(String message) { super(message); }
	}

}

