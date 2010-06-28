/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.darwin.prottest.util.exception;

/**
 *
 * @author diego
 */
public class ModelOptimizationException extends ProtTestCheckedException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 20090728L;

    /**
     * Instantiates a new external execution exception.
     */
    public ModelOptimizationException() {
        super("Model optimization exception");
    }

    /**
     * Instantiates a new external execution exception.
     * 
     * @param description the description
     */
    public ModelOptimizationException(String description) {
        super("Model optimization exception: " + description);
    }

    public static class ExternalExecutionException extends ModelOptimizationException {

        public ExternalExecutionException() {
            super();
        }

        public ExternalExecutionException(String application) {
            super("there was an error while executing " + application);
        }

        public ExternalExecutionException(String application, String message) {
            super("there was an error while executing " + application + ": " + message);
        }
    }

    public static class OSNotSupportedException extends ExternalExecutionException {

        /**
         * Instantiates a new oS not supported exception.
         */
        public OSNotSupportedException(String application) {
            super(application, "this operating system (" + System.getProperty("os.name") + ") is not supported.");
        }
    }

    public static class StatsFileFormatException extends ExternalExecutionException {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 20090728L;

        /**
         * Instantiates a new stats file format exception.
         * 
         * @param application the external model optimizer
         */
        public StatsFileFormatException(String application) {
            super("there was an error parsing " + application + " stats file");
        }

        /**
         * Instantiates a new stats file format exception.
         * 
         * @param application the external model optimizer
         * @param description the description
         */
        public StatsFileFormatException(String application, String description) {
            super("there was an error parsing " + application + " stats file: " + description);
        }
    }

    public static class ModelNotFoundException extends ExternalExecutionException {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 20090728L;

        /**
         * Instantiates a new stats file format exception.
         * 
         * @param application the external model optimizer
         */
        public ModelNotFoundException(String model) {
            super("cannot find " + model + " matrix description");
        }

        /**
         * Instantiates a new stats file format exception.
         * 
         * @param application the external model optimizer
         * @param description the description
         */
        public ModelNotFoundException(String model, String description) {
            super("cannot find " + model + " matrix description: " + description);
        }
    }
    
    public static class PhyMLExecutionException extends ExternalExecutionException {

        private static final String applicationName = "PhyML";

        public PhyMLExecutionException() {
            super(applicationName);
        }

        public PhyMLExecutionException(String message) {
            super(applicationName, message);
        }
    }
}
