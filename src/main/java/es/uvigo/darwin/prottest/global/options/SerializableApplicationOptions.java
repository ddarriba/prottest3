package es.uvigo.darwin.prottest.global.options;

import java.io.Serializable;
import java.util.Vector;

/**
 * This class is a serializable version of {@link ApplicationOptions ApplicationOptions}.
 * It has all important attributes in a concrete state of the application, and
 * reduces alignment ant topology to a hash, making this object much lighter
 * than the Application Options instance.
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
 */
public class SerializableApplicationOptions implements Serializable {

    private static final long serialVersionUID = 7758895101915536602L;
    /** The alignment, according to the alignment input file. */
    protected int alignment;
    /** The tree, according to the tree filename. */
    protected int tree;
    /** The number of categories. It is only useful if the distribution is gamma */
    public int numberOfCategories;
    /** The optimization strategy mode. */
    public int strategyMode;
    /** The matrices of the models to optimize. */
    private Vector<String> matrices;
    /** The distributions of the models to optimize. */
    private Vector<Integer> distributions;
    /** Boolean value to consider or not different kind of amino-acid frequencies. */
    private boolean plusF;

    /**
     * Gets the alignment hash.
     * 
     * @return the alignment hash
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Gets the phylogenetic tree hash.
     * 
     * @return the tree hash
     */
    public int getTree() {
        return tree;
    }

    /**
     * Gets the number of rate categories.
     * 
     * @return the number of rate categories
     */
    public int getNumberOfCategories() {
        return numberOfCategories;
    }

    /**
     * Gets the starting topology strategy mode.
     * 
     * @return the starting topology strategy mode
     */
    public int getStrategyMode() {
        return strategyMode;
    }

    /**
     * Gets the collection of matrix names.
     * 
     * @return the collection of matrix names
     */
    public Vector<String> getMatrices() {
        return matrices;
    }

    /**
     * Gets the collection of distribution identifiers.
     * 
     * @return the collection of distribution identifiers
     */
    public Vector<Integer> getDistributions() {
        return distributions;
    }

    /**
     * Gets the state of empirical frequencies consideration.
     * 
     * @return true, if empirical frequencies are considered
     */
    public boolean isPlusF() {
        return plusF;
    }

    /**
     * Instantiates a new serializable application options.
     * 
     * @param options the actual application options instance
     */
    public SerializableApplicationOptions(ApplicationOptions options) {
        this.alignment = options.getAlignment().toString().hashCode();
        if (options.getTree() != null) {
            this.tree = options.getTree().toString().hashCode();
        }
        this.numberOfCategories = options.ncat;
        this.strategyMode = options.strategyMode;
        this.matrices = options.getMatrices();
        this.distributions = options.getDistributions();
        this.plusF = options.isPlusF();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + alignment;
        result = prime * result + ((distributions == null) ? 0 : distributions.hashCode());
        result = prime * result + ((matrices == null) ? 0 : matrices.hashCode());
        result = prime * result + numberOfCategories;
        result = prime * result + (plusF ? 1231 : 1237);
        result = prime * result + strategyMode;
        result = prime * result + tree;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SerializableApplicationOptions other = (SerializableApplicationOptions) obj;
        if (alignment != other.alignment) {
            return false;
        }
        if (distributions == null) {
            if (other.distributions != null) {
                return false;
            }
        } else if (!distributions.equals(other.distributions)) {
            return false;
        }
        if (matrices == null) {
            if (other.matrices != null) {
                return false;
            }
        } else if (!matrices.equals(other.matrices)) {
            return false;
        }
        if (numberOfCategories != other.numberOfCategories) {
            return false;
        }
        if (plusF != other.plusF) {
            return false;
        }
        if (strategyMode != other.strategyMode) {
            return false;
        }
        if (tree != other.tree) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "SerializableApplicationOptions [alignment=" + alignment + ", distributions=" + distributions + ", matrices=" + matrices + ", numberOfCategories=" + numberOfCategories + ", plusF=" + plusF + ", strategyMode=" + strategyMode + ", tree=" + tree + "]";
    }
}
