/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.exe;

import es.uvigo.darwin.prottest.exe.util.TemporaryFileManager;
import es.uvigo.darwin.prottest.util.exception.ExternalExecutionException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 *
 * @author diego
 */
public class ExternalExecutor {

    private static final int STATE_NULL = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_EXECUTED = 2;
    
    private static final int OUTPUT_STANDARD = 1;
    private static final int OUTPUT_TEMPORARY = 2;
    
    private String command;
    private Process proc = null;
    private OutputStream logOutput;
    
    private int internal_state = STATE_NULL;
    
    public ExternalExecutor(String command, int outLogFormat) {
        try {
            this.command = command;

            switch (outLogFormat) {
                case OUTPUT_STANDARD:
                    logOutput = System.out;
                    break;
                case OUTPUT_TEMPORARY:
                    logOutput = new FileOutputStream(TemporaryFileManager.getInstance().
                            getLogFilename(Thread.currentThread()));
            }
            this.internal_state = STATE_INITIALIZED;
        } catch (FileNotFoundException ex) {
            throw new ExternalExecutionException("I/O error: " + ex.getMessage());
        }
    }
    
    public void run() throws ProtTestInternalException{

        if (!checkState(STATE_INITIALIZED) || checkState(STATE_EXECUTED)) {
            throw new ProtTestInternalException("Invalid executor internal state");
        }
        
        try {
            Runtime runtime = Runtime.getRuntime();
            proc = runtime.exec(command);

            ExternalExecutionManager.getInstance().addProcess(proc);
        } catch (IOException ex) {
            throw new ProtTestInternalException(ex.getMessage());
        }

        StreamGobbler errorGobbler = new StreamGobbler(new InputStreamReader(proc.getErrorStream()), "Phyml-Error", true, this.getClass());
        StreamGobbler outputGobbler = new StreamGobbler(new InputStreamReader(proc.getInputStream()), "Phyml-Output", true, this.getClass());
        errorGobbler.start();
        outputGobbler.start();
        
        try {    
            int exitVal = proc.waitFor();
        } catch (InterruptedException ex) {
            throw new ExternalExecutionException("Interrupted execution: " + ex.getMessage());
        }
    }
    
    private boolean checkState(int state) {
        return internal_state >= state;
    }
}
