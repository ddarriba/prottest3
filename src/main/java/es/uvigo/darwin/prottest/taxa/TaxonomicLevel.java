/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.taxa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: TaxonomicLevel.java 185 2006-01-23 23:03:18Z rambaut $
 */
public class TaxonomicLevel {

    /**
     * A private constructor. TaxonomicLevel objects can only be created by the static TaxonomicLevel.getTaxonomicLevel()
     * factory method.
     * @param name the name of the taxonomic level
     */
    private TaxonomicLevel(String name) {
        this.name = name;
    }

    /**
     * get the name of the taxonomic level
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * The name of this taxonomic level
     */
    private final String name;

    // Static factory methods

    /**
     * A static method that returns a TaxonomicLevel object with the given name. If this has
     * already been created then the same instance will be returned.
     * @param name the name of the taxonomic level
     * @return the taxonomic level object
     */
    public static TaxonomicLevel getTaxonomicLevel(String name) {
        TaxonomicLevel taxonomicLevel = (TaxonomicLevel)taxonomicLevels.get(name);

        if (taxonomicLevel == null) {
            taxonomicLevel = new TaxonomicLevel(name);
            taxonomicLevels.put(name, taxonomicLevel);
        }

        return taxonomicLevel;
    }

    /**
     * Returns a Set containing all the currently created taxonomic levels.
     * @return
     */
    public static Set getTaxonomicLevels() {
        return Collections.unmodifiableSet(taxonomicLevels.entrySet());
    }

    /**
     * A hash map containing name, object pairs.
     */
    private static Map taxonomicLevels = new HashMap();
}