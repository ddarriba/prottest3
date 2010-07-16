package es.uvigo.darwin.prottest.facade.util;

import java.util.Collection;
import pal.alignment.Alignment;

/**
 * A value object containing all parameters of a ProtTest-HPC execution.
 * 
 * @author Diego Darriba López
 * 
 * @since 3.0
 */
public class ProtTestParameterVO {

    /** The alignment */
    private Alignment alignment;
    /** The path of the file which contains the alignment */
    private String alignmentFilePath;
    /** The path of the file which contains the starting topology, if exists */
    private String treeFilePath;
    /** The matrices of the models to optimize. */
    private Collection<String> matrices;
    /** The distributions of the models to optimize. */
    private Collection<String> distributions;
    /** The number of transition categories. It is only useful if the distribution is gamma */
    public int ncat;
    /** Boolean value to consider or not the empirical amino-acid frequencies. */
    private boolean plusF;
    /** The starting topology strategy mode */
    private int strategyMode;

    /** 
     * Gets the path of the alignment file.
     * 
     * @return the path of the alignment file
     */
    public String getAlignmentFilePath() {
        return alignmentFilePath;
    }

    /**
     * Gets the alignment.
     * 
     * @return the alignment
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Gets the path of the starting user topology file, if it exists.
     *  
     * @return the path of the starting user topology file
     */
    public String getTreeFilePath() {
        return treeFilePath;
    }

    /**
     * Gets the set of substitution matrix names.
     * 
     * @return a collection of the substitution matrix names
     */
    public Collection<String> getMatrices() {
        return matrices;
    }

    /**
     * Gets the set of distributions in consideration.
     * 
     * @return a collection of the distribution manes.
     */
    public Collection<String> getDistributions() {
        return distributions;
    }

    /**
     * Gets the number of rate categories, if the distribution +G is considered.
     * 
     * @return the number of rate categories
     */
    public int getNcat() {
        return ncat;
    }

    /**
     * Gets the state of the empirical frequencies consideration.
     * 
     * @return true, if empirical frequencies are considered
     */
    public boolean isPlusF() {
        return plusF;
    }

    /**
     * Gets the starting topology strategy mode.
     * @return the starting topology strategy mode
     */
    public int getStrategyMode() {
        return strategyMode;
    }

    /**
     * Instantiates a new ProtTestParameterVO.
     * 
     * @param alignmentFilePath the path of the file which contains the alignment
     * @param alignment the alignment
     * @param treeFilePath the path of the file which contains the starting topology, if exists
     * @param matrices the matrices of the models to optimize
     * @param distributions the distributions of the models to optimize
     * @param plusF boolean value to consider or not the empirical amino-acid frequencies
     * @param ncat the number of transition categories. It is only useful if the distribution is gamma
     * @param strategyMode the starting topology strategy mode
     */
    public ProtTestParameterVO(String alignmentFilePath, Alignment alignment, String treeFilePath,
            Collection<String> matrices, Collection<String> distributions, boolean plusF,
            int ncat, int strategyMode) {
        this.alignmentFilePath = alignmentFilePath;
        this.alignment = alignment;
        this.treeFilePath = treeFilePath;
        this.matrices = matrices;
        this.distributions = distributions;
        this.plusF = plusF;
        this.ncat = ncat;
        this.strategyMode = strategyMode;
    }
}
