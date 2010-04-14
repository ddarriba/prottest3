//
//  StreamGobbler.java
//  vanilla-xcode
//
//  Created by Federico Abascal on Wed Sep 01 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package es.uvigo.darwin.prottest.exe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

    
//I took this class from Michael Daconta ( http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html )
public class StreamGobbler extends Thread {
    private InputStreamReader isr;
    private String type;
    private boolean printIt;
    private PrintWriter log;
    private PrintWriter errorWriter;
    
    
    public StreamGobbler(InputStreamReader isr, String type, boolean printIt, PrintWriter log, PrintWriter errorWriter) {
        this.isr     	 = isr;
        this.type    	 = type;
        this.printIt 	 = printIt;
        this.log     	 = log;
        this.errorWriter = errorWriter;
    }

    public void run() {
        try {
            //InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                if(printIt && line.startsWith(". Err"))
                    errorWriter.println(type + ">" + line);
                log.println(type + ">" + line);
            }
        } catch (IOException ioe) {
            
        }
    }
}

