package es.uvigo.darwin.prottest.global;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * The global application settings and parameter names.
 */
public abstract class ApplicationGlobals implements ProtTestConstants {

    /** The application APPLICATION_PROPERTIES. */
    public static Properties APPLICATION_PROPERTIES;
    
    static {
        APPLICATION_PROPERTIES = new Properties();
        try {
            FileInputStream prop = new FileInputStream("prottest.properties");
            APPLICATION_PROPERTIES.load(prop);
        } catch (IOException e) {
            System.err.println("Properties file (prottest.properties) cannot be resolved");
            System.exit(EXIT_NO_PROPERTIES);
        }

    }
    public static final String DEFAULT_SNAPSHOT_DIR = "snapshot/";

    /**
     * Gets the supported matrices.
     * 
     * @return the supported matrices
     */
    public abstract List<String> getSupportedMatrices();

    /**
     * Gets the model name from matrix and distribution names. This
     * method is useful specially when a custom matrix is set, so
     * his internal name is not representative for the user.
     * 
     * @param matrix the matrix name
     * @param frequenciesDistribution the frequencies distribution name
     * 
     * @return the model name
     */
    public abstract String getModelName(String matrix, int frequenciesDistribution);
}
