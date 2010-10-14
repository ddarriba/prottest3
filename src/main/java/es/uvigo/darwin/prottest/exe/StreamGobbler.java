/*
Copyright (C) 2004  Federico Abascal

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

