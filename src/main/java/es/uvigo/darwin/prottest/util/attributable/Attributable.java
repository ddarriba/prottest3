package es.uvigo.darwin.prottest.util.attributable;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

/**
 * Interface for associating attributeNames with an object.
 *
 * @version $Id: Attributable.java 575 2006-12-14 15:49:14Z twobeers $
 *
 * @author Andrew Rambaut
 */
public interface Attributable {

    /**
     * Sets an named attribute for this object.
     * @param name the name of the attribute.
     * @param value the new value of the attribute.
     */
    void setAttribute(String name, Object value);

    /**
     * @return an object representing the named attributed for this object.
     * @param name the name of the attribute of interest, or null if the attribute doesn't exist.
     */
    Object getAttribute(String name);

    /**
     * @param name name of attribute to remove
     */
    void removeAttribute(String name);

    /**
     * @return an array of the attributeNames that this object has.
     */
    Set<String> getAttributeNames();

    /**
     * Gets the entire attribute map.
     * @return an unmodifiable map
     */
    Map<String, Object> getAttributeMap();

    public static class Utils {
        Set<String> getAttributeNames(Collection<Attributable> attributables) {
            Set<String> names = new HashSet<String>();

            for (Attributable attributable : attributables) {
                names.addAll(attributable.getAttributeNames());
            }

            return names;
        }
    }
}