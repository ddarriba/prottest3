/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package es.uvigo.darwin.prottest.util.exception;

/**
 * Generic exception when model optimization
 * 
 * @author Diego Darriba
 * @since 3.0
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

    /**
     * Exception which occurs while execution a third-party application
     * 
     * @author Diego Darriba
     * @since 3.0
     */
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

    /**
     * Exception which occurs when the current operating system is not supported
     * (only when running non-portable applications)
     * 
     * @author Diego Darriba
     * @since 3.0
     */
    public static class OSNotSupportedException extends ExternalExecutionException {

        /**
         * Instantiates a new oS not supported exception.
         */
        public OSNotSupportedException(String application) {
            super(application, "this operating system (" + System.getProperty("os.name") + ") is not supported.");
        }
    }

    /**
     * Exception while parsing the stats file provided by a third-party application
     * 
     * @author Diego Darriba
     * @since 3.0
     */
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

    /**
     * Exception while a required model matrix is not found
     * 
     * @author diego
     * @since 3.0
     */
    public static class ModelNotFoundException extends ExternalExecutionException {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 20090728L;

        /**
         * Instantiates a new model not found exception.
         * 
         * @param model the non existent model name
         */
        public ModelNotFoundException(String model) {
            super("cannot find " + model + " matrix description");
        }

        /**
         * Instantiates a new model not found exception.
         * 
         * @param model the non existent model name
         * @param description the description
         */
        public ModelNotFoundException(String model, String description) {
            super("cannot find " + model + " matrix description: " + description);
        }
    }

    /**
     * External execution exception localized in PhyML
     * 
     * @author diego
     * @since 3.0
     */
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
