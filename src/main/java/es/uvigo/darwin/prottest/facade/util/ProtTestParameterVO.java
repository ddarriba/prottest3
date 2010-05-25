package es.uvigo.darwin.prottest.facade.util;

import java.util.Collection;
import pal.alignment.Alignment;

public class ProtTestParameterVO {

    private Alignment alignment;
    private String alignmentFilePath;
    private String treeFilePath;
    /** The matrices of the models to optimize. */
    private Collection<String> matrices;
    /** The distributions of the models to optimize. */
    private Collection<String> distributions;
    /** The number of transition categories. It is only useful if the distribution is gamma */
    public int ncat;
    /** Boolean value to consider or not different kind of amino-acid frequencies. */
    private boolean plusF;
    private int strategyMode;

    public String getAlignmentFilePath() {
        return alignmentFilePath;
    }

    public Alignment getAlignment() {
        return alignment;
    }
    
    public String getTreeFilePath() {
        return treeFilePath;
    }

    public Collection<String> getMatrices() {
        return matrices;
    }

    public Collection<String> getDistributions() {
        return distributions;
    }

    public int getNcat() {
        return ncat;
    }

    public boolean isPlusF() {
        return plusF;
    }

    public int getStrategyMode() {
        return strategyMode;
    }

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
