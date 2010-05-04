/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uvigo.darwin.prottest.util.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author diego
 */
public class ProtTestLogFormatter extends Formatter {

    @Override
    public String format(LogRecord lr) {
        
        StringBuffer text = new StringBuffer();
        
        if (lr.getLevel() != Level.INFO) {
            text.append(lr.getLevel().getName() + " : ");
        }
        text.append(lr.getMessage());
        
        return text.toString();
        
    }

}
