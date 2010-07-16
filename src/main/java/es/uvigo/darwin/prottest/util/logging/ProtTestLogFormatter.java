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
        
        if (lr.getLevel().intValue() < Level.FINE.intValue()) {
            text.append(lr.getLevel().getName() + " : ");
        }
        if (lr.getLevel().intValue() == Level.WARNING.intValue()) {
            text.append("WARNING: ");
        }
        text.append(lr.getMessage());
        
        return text.toString();
        
    }

}
