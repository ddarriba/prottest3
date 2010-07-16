//
//  StreamGobbler.java
//  vanilla-xcode
//
//  Created by Federico Abascal on Wed Sep 01 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
package es.uvigo.darwin.prottest.exe;

import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Allows the asynchronous logging of an input stream.
 * 
 * @author Federico Abascal
 */
public class StreamGobbler extends Thread {

    private InputStreamReader isr;
    private String type;
    private boolean printIt;
    private Class caller;

    /**
     * Instantiates a new StreamGobbler
     * 
     * @param isr the input stream reader to evaluate
     * @param type the prefix of the output lines
     * @param printIt true, if the errors should be logged
     * @param caller the caller
     */
    public StreamGobbler(InputStreamReader isr, String type, boolean printIt, Class caller) {
        this.isr = isr;
        this.type = type;
        this.printIt = printIt;
        this.caller = caller;
    }

    @Override
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            //InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (printIt && line.startsWith(". Err")) {
                    ProtTestLogger.severeln(type + ">" + line, caller);
                }
                ProtTestLogger.fineln(type + ">" + line, caller);
            }
        } catch (IOException ioe) {

        }
    }
}

