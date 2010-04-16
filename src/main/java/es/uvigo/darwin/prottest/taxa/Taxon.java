/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.taxa;

import es.uvigo.darwin.prottest.util.attributable.AttributableHelper;
import es.uvigo.darwin.prottest.util.attributable.Attributable;
import es.uvigo.darwin.prottest.util.fileio.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrew Rambaut
 * @author Alexei Drummond
 *
 * @version $Id: Taxon.java 569 2006-12-12 18:48:36Z twobeers $
 */
public final class Taxon implements Attributable, Comparable {

    /**
     * A private constructor. Taxon objects can only be created by the static Taxon.getTaxon()
     * factory method.
     * @param name the name of the taxon
     */
    private Taxon(String name) {
        this(name, null);
    }

    /**
     * A private constructor. Taxon objects can only be created by the static Taxon.getTaxon()
     * factory method.
     * @param name the name of the taxon
     */
    private Taxon(String name, TaxonomicLevel taxonomicLevel) {
        this.name = name;
        this.taxonomicLevel = taxonomicLevel;
    }

    /**
     * get the name of the taxon
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * get the taxonomic level of the taxon
     * @return the taxonomic level
     */
    public TaxonomicLevel getTaxonomicLevel() {
        return taxonomicLevel;
    }

	// Attributable IMPLEMENTATION

	public void setAttribute(String name, Object value) {
		if (helper == null) {
			helper = new AttributableHelper();
		}
		helper.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		if (helper == null) {
			return null;
		}
		return helper.getAttribute(name);
	}

    public void removeAttribute(String name) {
        if( helper != null ) {
            helper.removeAttribute(name);
        }
    }

    public Set<String> getAttributeNames() {
        if (helper == null) {
            return Collections.emptySet();
        }
        return helper.getAttributeNames();
    }

	public Map<String, Object> getAttributeMap() {
		if (helper == null) {
			return Collections.emptyMap();
		}
		return helper.getAttributeMap();
	}

	private AttributableHelper helper = null;

    // Static factory methods

    /**
     * @return a Set containing all the currently created Taxon objects.
     */
    public static Set<Taxon> getAllTaxa() {
        return Collections.unmodifiableSet(new HashSet<Taxon>(taxa.values()));
    }

    /**
     * A static method that returns a Taxon object with the given name. If this has
     * already been created then the same instance will be returned.
     *
     * Due to problems with the singleton model of taxa, this factory method now
     * creates a new instance.
     *
     * @param name
     * @return the taxon
     */
    public static Taxon getTaxon(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Illegal null string for taxon name");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Illegal empty string for taxon name");
        }

        Taxon taxon = taxa.get(name);

        if (taxon == null) {
            taxon = new Taxon(name);
            taxa.put(name, taxon);
        }

        return taxon;
    }

	// private members


    /**
     * The name of this taxon.
     */
    private final String name;

    /**
     * A hash map containing taxon name, object pairs.
     */
    private static Map<String, Taxon> taxa = new HashMap<String, Taxon>();

    /**
     * the taxonomic level of this taxon.
     */
    private final TaxonomicLevel taxonomicLevel;

    public String toString() {
        return name;
    }

	public int compareTo(Object o) {
		return name.compareTo(((Taxon)o).getName());
	}


    public boolean equals(Taxon t) {
	    return name.equals(t.getName());
    }

    public int hashCode() {
        return name.hashCode();
    }
}