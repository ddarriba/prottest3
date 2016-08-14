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
package es.uvigo.darwin.prottest.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import es.uvigo.darwin.prottest.ProtTest;

/**
 * The global application settings and parameter names.
 */
public abstract class ApplicationGlobals implements ProtTestConstants {

    /** The application APPLICATION_PROPERTIES. */
    public static final Properties APPLICATION_PROPERTIES;
    public static final String ENV_PATH;
    public static final String JAR_PATH = ProtTest.class.getProtectionDomain()
                                                  .getCodeSource()
                                                  .getLocation()
                                                  .getFile()
                                                  .replace("%20", " ");
    public static final String PATH;
    public static final String DEFAULT_SNAPSHOT_DIR = "snapshot/";
    
    static {
      ENV_PATH = System.getenv("PROTTEST_PATH");
      if (ENV_PATH != null)
        PATH = ENV_PATH;
      else {
        if (ProtTest.MPJ_RUN)
          PATH = System.getProperty("user.dir");
        else
          PATH = JAR_PATH.replaceFirst(new File(JAR_PATH).getName(), "");
      }
      APPLICATION_PROPERTIES = new Properties();
      try {
        FileInputStream prop = new FileInputStream(
            PATH + File.separator + "prottest.properties");
        APPLICATION_PROPERTIES.load(prop);
      } catch (IOException e) {
        System.err.println("Properties file (" + PATH + File.separator
            + "prottest.properties) cannot be resolved");
        System.exit(EXIT_NO_PROPERTIES);
      }
    }

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
