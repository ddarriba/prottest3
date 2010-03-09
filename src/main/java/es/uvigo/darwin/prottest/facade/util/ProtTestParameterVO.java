package es.uvigo.darwin.prottest.facade.util;

import java.io.PrintWriter;
import java.util.Collection;

import es.uvigo.darwin.prottest.util.printer.ProtTestPrinter;

public class ProtTestParameterVO {

	private String alignmentFilePath;
	
	private String treeFilePath;
	
	private ProtTestPrinter printer;
	
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
	
	public ProtTestPrinter getPrinter() {
		return printer;
	}

	public ProtTestParameterVO(String alignmentFilePath, String treeFilePath, 
    		Collection<String> matrices, Collection<String> distributions, boolean plusF,
    		int ncat, int strategyMode) {
		this.printer = new ProtTestPrinter(
				new PrintWriter(System.out), 
				new PrintWriter(System.err));
    	this.alignmentFilePath = alignmentFilePath;
    	this.treeFilePath = treeFilePath;
    	this.matrices = matrices;
    	this.distributions = distributions;
    	this.plusF = plusF;
    	this.ncat = ncat;
    	this.strategyMode = strategyMode;
    }
	
	public ProtTestParameterVO(ProtTestPrinter printer, String alignmentFilePath, String treeFilePath, 
    		Collection<String> matrices, Collection<String> distributions, boolean plusF,
    		int ncat, int strategyMode) {
		this.printer = printer;
    	this.alignmentFilePath = alignmentFilePath;
    	this.treeFilePath = treeFilePath;
    	this.matrices = matrices;
    	this.distributions = distributions;
    	this.plusF = plusF;
    	this.ncat = ncat;
    	this.strategyMode = strategyMode;
    }
}
