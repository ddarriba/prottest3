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

import static es.uvigo.darwin.prottest.global.RaxmlAminoAcidApplicationGlobals.*;
import es.uvigo.darwin.prottest.exe.util.TemporaryFileManager;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.AminoAcidModel;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.ModelNotFoundException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.OSNotSupportedException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.PhyMLExecutionException;
import es.uvigo.darwin.prottest.util.exception.ModelOptimizationException.StatsFileFormatException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pal.tree.ReadTree;
import pal.tree.TreeParseException;


/**
 * The Class RAxMLAminoAcidRunEstimator. It optimizes Amino-Acid
 * model parameters using RAxML.
 *
 * This implementation fits with RAxML 7.0.4.
 */
public class RaxMLAminoAcidRunEstimator extends AminoAcidRunEstimator {

    public static final String PARSIMONY_TREE_PREFIX = "RAxML_parsimonyTree.";
    public static final String FINAL_TREE_PREFIX = "RAxML_result.";
    public static final String STATS_PREFIX = "RAxML_info.";
    public static final String LOG_PREFIX = "RAxML_log.";

    /** Alignment filename. */
    private String workAlignment;
    /** Custom model substitution matrix file**/
    private File modelFile;

    private String outputAppend;

    /**
     * Instantiates a new optimizer for amino-acid models
     * using RaxML.
     *
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     */
    public RaxMLAminoAcidRunEstimator(ApplicationOptions options, Model model) {
        this(options, model, 1);
    }

     /**
     * Instantiates a new optimizer for amino-acid models
     * using RaxML.
     *
     * @param options the application options instance
     * @param model the amino-acid model to optimize
     * @param numberOfThreads the number of threads to use in the optimization
     */
    public RaxMLAminoAcidRunEstimator(ApplicationOptions options, Model model, int numberOfThreads) {
        super(options, model, numberOfThreads);

                this.numberOfCategories = options.ncat;

        try {
            this.model = (AminoAcidModel) model;
            // check if there is any matrix file
            modelFile = new File("models" + File.separator + model.getMatrix());

        } catch (ClassCastException cce) {
            throw new ProtTestInternalException("Wrong model type");
        }
                
        this.outputAppend = model.getModelName().replace('+', 'P');
        System.out.println(" MODEL, OUTPUT " + model.getModelName() + " " + outputAppend);

    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.exe.RunEstimator#optimizeModel(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
     */
    @Override
    public boolean runEstimator() throws ModelOptimizationException {

        //let's call Phyml with the proper command line

        this.workAlignment = TemporaryFileManager.getInstance().getAlignmentFilename(Thread.currentThread());

        
        switch (options.strategyMode) {
            case OPTIMIZE_BIONJ:
                break;
            case OPTIMIZE_FIXED_BIONJ:
                if (TemporaryFileManager.getInstance().getTreeFilename(Thread.currentThread()) != null) {
                } else {
                }
                break;
            case OPTIMIZE_ML:
                break;
            case OPTIMIZE_USER:
        }
        try {
            Runtime runtime = Runtime.getRuntime();

            String str[] = new String[26];
            for (int i = 0; i < str.length; i++) {
                str[i] = "";
            }

            String raxmlBinName = "raxmlHPC";

            if (raxmlBinName != null) {

                //     raxml -s seq_file_name -n output -m model d/e (d para -F y e para +F) -v 0/e (para -I o +I) -a e (para estimar alpha)
                //         -c 0/4/8 (num rate categories) -u user_tree_file (opcional)
                //         -o tlr/lr (dependiendo de si optimizamos la topología o no)
                //         -m WAG (default) | JTT | MtREV | Dayhoff | DCMut | RtREV | CpREV | VT | Blosum62 | MtMam | MtArt | HIVw |  HIVb | custom

                str[0] = raxmlBinName;

                // input alignment
                str[1] = "-s";
                str[2] = workAlignment;
                File f = new File(workAlignment);

                str[5] = "-n";
                str[6] = outputAppend;

                StringBuilder modelName = new StringBuilder("PROT");
                if (model.isGamma())
                    modelName.append("GAMMA");
                else {
                    modelName.append("MIX");
                }
                if (model.isInv())
                    modelName.append("I");

                modelName.append(model.getMatrix().toUpperCase());
                
                if (model.isPlusF())
                    modelName.append("F");

                // the model
                str[3] = "-m";
//                if (!Arrays.asList(RAXML_MATRICES).contains(model.getMatrix())) {
//                    // check matrix file
//                    if (!modelFile.exists()) {
//                        throw new ModelNotFoundException(model.getMatrix());
//                    }
//                    str[9] = "custom";
//                } else {
                    str[4] =  modelName.toString();
//                }

//                if (APPLICATION_PROPERTIES.getProperty("phyml_thread_scheduling", "false").equalsIgnoreCase("true")) {
//                    str[24] = "-t";
//                    str[25] = String.valueOf(numberOfThreads);
//                }
                System.out.print("COMMAND: ");
                for (String tok : str)
                    System.out.print(tok + " ");
                System.out.println(" ");
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

            StreamGobbler errorGobbler = new RaxMLStreamGobbler(new InputStreamReader(proc.getErrorStream()), "RAxML-Error", true, RunEstimator.class);
            StreamGobbler outputGobbler = new RaxMLStreamGobbler(new InputStreamReader(proc.getInputStream()), "RAxML-Output", true, RunEstimator.class);
            errorGobbler.start();
            outputGobbler.start();
            int exitVal = proc.waitFor();
            ExternalExecutionManager.getInstance().removeProcess(proc);

            pfine("RAxML command-line: ");
            for (int i = 0; i < str.length; i++) {
                pfine(str[i] + " ");
            }
            pfineln("");

            if (exitVal != 0) {
                errorln("RAxML exit value: " + exitVal + " (there was probably some error)");
                error("RAxML command-line: ");
                for (int i = 0; i < str.length; i++) {
                    error(str[i] + " ");
                }
                errorln("");

                errorln("Please, take a look at the RAxML log below:");
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
     * Delete temporary files.
     *
     * @return true, if successful
     */
    @Override
    protected boolean deleteTemporaryFiles() {
        return true;
        //throw new UnsupportedOperationException("RAxML is not supported yet. Sorry.");
    }

    /**
     * Read the temporary statistics file.
     *
     * @return true, if successful
     */
    private boolean readStatsFile()
            throws ModelOptimizationException {

        //Output files:
        //
        // Extension given by the -n argument
        //
        // RAxML_parsimonyTree.*    Parsimony input tree
        // RAxML_result.*       Final tree
        // RAxML_info.*         Results
        // RAxML_log.*          Log
        String line;

        String alpha, inv, logLK;
        alpha = inv = logLK = null;
        try {
            FileReader input = new FileReader(STATS_PREFIX + outputAppend);
            BufferedReader br = new BufferedReader(input);
            while ((line = br.readLine()) != null) {
                pfinerln("[DEBUG] RAXML: " + line);

                if (line.length() > 0) {
                    if (line.startsWith("Substitution Matrix:")) {
                        String matrixName = Utilities.lastToken(line);
                        if (!model.getMatrix().equalsIgnoreCase(matrixName)) {
                            String errorMsg = "Matrix names doesn't match";
                            errorln("RAXML: " + line);
                            errorln("Last token: " + Utilities.lastToken(line));
                            errorln("It should be: " + model.getMatrix());
                            errorln(errorMsg);
                            throw new ModelNotFoundException(model.getMatrix());
                        }
                    } else if (line.startsWith("Likelihood   :")) {
                        logLK = Utilities.lastToken(line);
                    } else if (line.startsWith("Inference[0]")) {
                        alpha = Utilities.nextToken(line, "alpha[0]:");
                        inv = Utilities.nextToken(line, "invar[0]:");

                        pfinerln("[DEBUG] RAXML: " + line);

                    } else if (line.startsWith("Overall Time")) {
                        time = Utilities.lastToken(line);
                    }
                }
            }

            model.setLk(Double.parseDouble(logLK));
            if (alpha != null) {
                model.setAlpha(Double.parseDouble(alpha));
            }
            if (inv != null) {
                model.setInv(Double.parseDouble(inv));
            }
            
        } catch (IOException e) {
            throw new StatsFileFormatException("RAxML", e.getMessage());
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
            model.setTree(new ReadTree(FINAL_TREE_PREFIX + outputAppend));
        } catch (TreeParseException e) {
            String errorMsg = "ProtTest: wrong tree format, exiting...";
            throw new TreeFormatException(errorMsg);
        } catch (IOException e) {
            String errorMsg = "Error: File not found (IO error), exiting...";
            throw new TreeFormatException(errorMsg);
        }
        return true;
    }
}
