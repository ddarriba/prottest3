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
package es.uvigo.darwin.prottest.exe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pal.tree.ReadTree;
import pal.tree.TreeParseException;
import es.uvigo.darwin.prottest.exe.util.TemporaryFileManager;
import static es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals.*;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.AminoAcidModel;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.OSNotSupportedException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.PhyMLExecutionException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.StatsFileFormatException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.ModelNotFoundException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import java.util.Arrays;

/**
 * The Class PhyMLAminoAcidRunEstimator. It optimizes Amino-Acid
 * model parameters using PhyML 3.0.
 * 
 * @author Federico Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public class PhyMLv3AminoAcidRunEstimator extends AminoAcidRunEstimator {

    /** The PhyML implemented matrices. */
    public static String[] IMPLEMENTED_MATRICES = {
        "JTT",
        "LG",
        "DCMut",
        "MtREV",
        "MtMam",
        "MtArt",
        "Dayhoff",
        "WAG",
        "RtREV",
        "CpREV",
        "Blosum62",
        "VT",
        "HIVb",
        "HIVw"
    };
    /** Suffix for temporary statistic files. */
    private static final String STATS_FILE_SUFFIX = "_phyml_stats.txt";
    /** Suffix for temporary tree files. */
    private static final String TREE_FILE_SUFFIX = "_phyml_tree.txt";
    /** Alignment filename. */
    private String workAlignment;
    /** Custom model substitution matrix file**/
    private File modelFile;

    /**
     * Instantiates a new optimizer for amino-acid models
     * using PhyML.
     * 
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     */
    public PhyMLv3AminoAcidRunEstimator(ApplicationOptions options, Model model) {
        this(options, model, 1);
    }

    /**
     * Instantiates a new optimizer for amino-acid models
     * using PhyML.
     * 
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     * @param numberOfThreads the number of threads to use in the optimization
     */
    public PhyMLv3AminoAcidRunEstimator(ApplicationOptions options, Model model, int numberOfThreads) {
        super(options, model, numberOfThreads);
        this.numberOfCategories = options.ncat;

        try {
            this.model = (AminoAcidModel) model;
            // check if there is any matrix file
            modelFile = new File("models" + File.separator + model.getMatrix());

        } catch (ClassCastException cce) {
            throw new ProtTestInternalException("Wrong model type");
        }
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.exe.RunEstimator#optimizeModel(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
     */
    @Override
    public boolean runEstimator()
            throws ModelOptimizationException {
        //let's call Phyml with the proper command line

        this.workAlignment = TemporaryFileManager.getInstance().getAlignmentFilename(Thread.currentThread());

        String inv = "0.0";
        if (model.isInv()) {
            inv = "e";
        }

        String rateCathegories = "1";

        String alpha = "";
        if (model.isGamma()) {
            rateCathegories = "" + model.getNumberOfTransitionCategories();//options.ncat;
            alpha = "e";
        }
        String tr = "BIONJ";

        String F = "d";
        if (model.isPlusF()) {
            F = "e";
        }
        String topo = "lr";
        switch (options.strategyMode) {
            case OPTIMIZE_BIONJ:
                tr = "BIONJ";
                topo = "l";
                break;
            case OPTIMIZE_FIXED_BIONJ:
                if (TemporaryFileManager.getInstance().getTreeFilename(Thread.currentThread()) != null) {
                    tr = TemporaryFileManager.getInstance().getTreeFilename(Thread.currentThread());
                    topo = "l";
                } else {
                    topo = "r";
                }
                break;
            case OPTIMIZE_ML:
                topo = "tl";
                break;
            case OPTIMIZE_USER:
                tr = TemporaryFileManager.getInstance().getTreeFilename(Thread.currentThread());
                topo = "l";
        }
        try {
            Runtime runtime = Runtime.getRuntime();

            String str[] = new String[29];
            for (int i = 0; i < str.length; i++) {
                str[i] = "";
            }

            java.io.File currentDir = new java.io.File("");
            File phymlBin = new File(currentDir.getAbsolutePath() + "/bin/phyml");
            String phymlBinName;
            if (phymlBin.exists() && phymlBin.canExecute()) {
                phymlBinName = phymlBin.getAbsolutePath();
            } else {
                phymlBinName = currentDir.getAbsolutePath() + "/bin/" + getPhymlVersion();
            }

            if (phymlBinName != null) {

                //     phyml -i seq_file_name -d aa ¿-q? -f d/e (d para -F y e para +F) -v 0/e (para -I o +I) -a e (para estimar alpha)
                //         -c 0/4/8 (num rate categories) -u user_tree_file (opcional)
                //         -o tlr/lr (dependiendo de si optimizamos la topología o no)
                //         -m WAG (default) | JTT | MtREV | Dayhoff | DCMut | RtREV | CpREV | VT | Blosum62 | MtMam | MtArt | HIVw |  HIVb | custom

                str[0] = phymlBinName;

                // input alignment
                str[4] = "-i";
                str[5] = workAlignment;

                // number of rate categories
                str[6] = "-c";
                str[7] = rateCathegories;

                // the model
                str[8] = "-m";
                if (!Arrays.asList(IMPLEMENTED_MATRICES).contains(model.getMatrix())) {
                    // check matrix file
                    if (!modelFile.exists()) {
                        throw new ModelNotFoundException(model.getMatrix());
                    }
                    str[9] = "custom";
                    str[27] = "--aa_rate_file";
                    str[28] = modelFile.getAbsolutePath();
                } else {
                    str[9] = model.getMatrix();
                }

                // proportion of invariable sites
                str[10] = "-v";
                str[11] = inv;

                // value of the gamma shape parameter (if gamma distribution)
                if (!alpha.equals("")) {
                    str[12] = "-a";
                    str[13] = alpha;
                }

                // topology optimization
                str[14] = "-o";
                str[15] = topo;

                // amino-acid frequencies
                str[16] = "-f";
                str[17] = F;

                // starting tree file
                if (!tr.equals("BIONJ")) {
                    str[18] = "-u";
                    str[19] = tr;
                }

                // data type
                str[20] = "-d";
                str[21] = "aa";

                // bootstrapping
                str[22] = "-b";
                str[23] = "0";

                if (APPLICATION_PROPERTIES.getProperty("phyml_thread_scheduling", "false").equalsIgnoreCase("true")) {
                    str[24] = "--num_threads";
                    str[25] = String.valueOf(numberOfThreads);
                }
                str[26] = "--no_memory_check";
                
                model.setCommandLine(str);
                proc = runtime.exec(str);
                proc.getOutputStream().write(modelFile.getPath().getBytes());
                ExternalExecutionManager.getInstance().addProcess(proc);

            } else {
                OSNotSupportedException e =
                        new OSNotSupportedException("PhyML");
                throw e;
            }
            proc.getOutputStream().close();

            StreamGobbler errorGobbler = new PhymlStreamGobbler(new InputStreamReader(proc.getErrorStream()), "Phyml-Error", true, RunEstimator.class);
            StreamGobbler outputGobbler = new PhymlStreamGobbler(new InputStreamReader(proc.getInputStream()), "Phyml-Output", true, RunEstimator.class);
            errorGobbler.start();
            outputGobbler.start();
            int exitVal = proc.waitFor();
            ExternalExecutionManager.getInstance().removeProcess(proc);

            pfine("Phyml's command-line: ");
            for (int i = 0; i < str.length; i++) {
                pfine(str[i] + " ");
            }
            pfineln("");

            if (exitVal != 0) {
                errorln("Phyml's exit value: " + exitVal + " (there was probably some error)");
                error("Phyml's command-line: ");
                for (int i = 0; i < str.length; i++) {
                    error(str[i] + " ");
                }
                errorln("");

                errorln("Please, take a look at the Phyml's log below:");
                String line;
                try {
                    FileReader input = new FileReader(TemporaryFileManager.getInstance().getLogFilename(Thread.currentThread()));
                    BufferedReader br = new BufferedReader(input);
                    while ((line = br.readLine()) != null) {
                        errorln(line);
                    }
                } catch (IOException e) {
                    errorln("Unable to read the log file: " + TemporaryFileManager.getInstance().getLogFilename(Thread.currentThread()));
                }
            }
        } catch (InterruptedException e) {
            throw new PhyMLExecutionException("Interrupted execution: " + e.getMessage());
        } catch (IOException e) {
            throw new PhyMLExecutionException("I/O error: " + e.getMessage());
        }


        if (!(readStatsFile() && readTreeFile())) {
            return false;
        }

        return true;
    }

    /**
     * Read the temporary statistics file.
     * 
     * @return true, if successful
     */
    private boolean readStatsFile()
            throws ModelOptimizationException {

        String line;

        try {
            FileReader input = new FileReader(workAlignment + STATS_FILE_SUFFIX);
            BufferedReader br = new BufferedReader(input);
            while ((line = br.readLine()) != null) {
                pfinerln("[DEBUG] PHYML: " + line);

                if (line.length() > 0) {
                    if (line.startsWith(". Model of amino acids substitution")) {
                        String matrixName = Utilities.lastToken(line);
                        
                        //TODO: This line exists due to a bug in the phyml latest versions
                        //      where the HIVw matrix is shown as HIVb in the stats file
                        if (!model.getMatrix().equals("HIVw"))
                        if (!(model.getMatrix().equals(matrixName) || (modelFile.exists() &&
                                matrixName.equals("Custom")))) {
                            String errorMsg = "Matrix names doesn't match";
                            errorln("PHYML: " + line);
                            errorln("Last token: " + Utilities.lastToken(line));
                            errorln("It should be: " + model.getMatrix());
                            errorln(errorMsg);
                            throw new ModelNotFoundException(model.getMatrix());
                        }
                    } else if (line.startsWith(". Log-likelihood")) {
                        model.setLk(Double.parseDouble(Utilities.lastToken(line)));
                    } else if (line.startsWith(". Discrete gamma model")) {
                        if (Utilities.lastToken(line).equals("Yes")) {
                            line = br.readLine();

                            pfinerln("[DEBUG] PHYML: " + line);

                            if (model.getNumberOfTransitionCategories() != Integer.parseInt(Utilities.lastToken(line))) {
                                String errorMsg = "There were errors in the number of transition categories: " + model.getNumberOfTransitionCategories() + " vs " + Integer.parseInt(Utilities.lastToken(line));
                                errorln(errorMsg);

                                throw new StatsFileFormatException("PhyML", errorMsg);
                            //prottest.setCurrentModel(-2);
                            }
                            line = br.readLine();

                            pfinerln("[DEBUG] PHYML: " + line);

                            model.setAlpha(Double.parseDouble(Utilities.lastToken(line)));
                        }
                    } else if (line.startsWith(". Proportion of invariant:")) {
                        model.setInv(Double.parseDouble(Utilities.lastToken(line)));
                    } else if (line.startsWith(". Time used")) {
                        time = Utilities.lastToken(line);
                    }
                }
            }
        } catch (IOException e) {
            throw new StatsFileFormatException("PhyML", e.getMessage());
        }
        return true;
    }

    /**
     * Read the temporary tree file.
     * 
     * @return true, if successful
     * 
     * @throws TreeFormatException the tree format exception
     */
    private boolean readTreeFile()
            throws TreeFormatException {

        try {
            model.setTree(new ReadTree(workAlignment + TREE_FILE_SUFFIX));
        } catch (TreeParseException e) {
            String errorMsg = "ProtTest: wrong tree format, exiting...";
            throw new TreeFormatException(errorMsg);
        } catch (IOException e) {
            String errorMsg = "Error: File not found (IO error), exiting...";
            throw new TreeFormatException(errorMsg);
        }
        return true;
    }

    /**
     * Delete temporary files.
     * 
     * @return true, if successful
     */
    @Override
    protected boolean deleteTemporaryFiles() {
        File f;
        f = new File(workAlignment + STATS_FILE_SUFFIX);
        f.delete();
        f = new File(workAlignment + TREE_FILE_SUFFIX);
        f.delete();
        f = new File(TemporaryFileManager.getInstance().getLogFilename(Thread.currentThread()));
        f.delete();
        return true;
    }

    /**
     * Gets the PhyML executable name for the current Operating System.
     * 
     * @return the PhyML executable name
     */
    private String getPhymlVersion() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            String oa = System.getProperty("os.arch");
            if (oa.startsWith("ppc")) {
                return "phyml-prottest-macppc";
            } else {
                return "phyml-prottest-macintel";
            }
        } else if (os.startsWith("Linux")) {
            return "phyml-prottest-linux";
        } else if (os.startsWith("Window")) {
            return "phyml-prottest-windows.exe";
        } else {
            return null;
        }
    }
}
