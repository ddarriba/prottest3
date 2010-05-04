package es.uvigo.darwin.prottest.util.logging;

import es.uvigo.darwin.prottest.util.WriterOutputStream;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class ProtTestLogger {

    public static final String DEFAULT_LOGGER_NAME = "default";
    
    private static HashMap<String, ProtTestLogger> loggers;
    
    static {

        loggers = new HashMap<String, ProtTestLogger>();
        ProtTestLogger defaultLogger = new ProtTestLogger(DEFAULT_LOGGER_NAME);
        loggers.put(DEFAULT_LOGGER_NAME, defaultLogger);
        
    }
    
    private String loggerName;
    private Level loggerLevel;
    private List<Handler> handlers;
    private Handler stdHandler;
    
    public static ProtTestLogger getLogger(String loggerName) {
        if (!loggers.containsKey(loggerName)) {
            loggers.put(loggerName, new ProtTestLogger(loggerName));
        }
        return loggers.get(loggerName);
    }
    
    public ProtTestLogger(String loggerName) {
        this.loggerName = loggerName;
        if (loggers.containsKey(loggerName))
            throw new ProtTestInternalException(
                    "ProtTestLogger with name " + loggerName + " already exists. " +
                    " Use getLogger() instead");
        this.loggerLevel = Level.ALL;
        
        // Add standard output handler
        this.handlers = new ArrayList<Handler> ();
        stdHandler = new StreamHandler(System.out, new ProtTestLogFormatter());
        stdHandler.setLevel(Level.OFF);
    }
    
    /**
     * Check if a message of the given level would actually be logged
     * by this logger.  This check is based on the Loggers effective level,
     * which may be inherited from its parent.
     *
     * @param	level	a message logging level
     * @return	true if the given message level is currently being logged.
     */
    public boolean isLoggable(Level level) {
	if (level.intValue() < loggerLevel.intValue()
                || loggerLevel.intValue() == Level.OFF.intValue()) {
	    return false;
	}
	return true;
    }
    
    public void addHandler(Handler handler) {
        if (handler == null) {
	    throw new NullPointerException();
	}
        this.handlers.add(handler);
    }
    
    public void addHandler(OutputStream out) {
        if (out == null) {
	    throw new NullPointerException();
	}
        Handler handler = new StreamHandler(out, 
                new ProtTestLogFormatter());
        this.handlers.add(handler);
    }
    
    public void addHandler(Writer out) {
        if (out == null) {
	    throw new NullPointerException();
	}
        Handler handler = new StreamHandler(
                new WriterOutputStream(out),
                new ProtTestLogFormatter());
        this.handlers.add(handler);
    }
    
    public boolean removeHandler(Handler handler) {
        return this.handlers.remove(handler);
    }
    
    public Handler[] getHandlers() {
        return handlers.toArray(new Handler[0]);
    }
    
    public void setStdHandlerLevel(Level newLevel) {
        if (newLevel == null) {
	    throw new NullPointerException();
	}
        stdHandler.setLevel(newLevel);
    }
    
    public Level getStdHandlerLevel() {
        return stdHandler.getLevel();
    }
    
    public void setLevel(Level newLevel) {
        if (newLevel == null) {
	    throw new NullPointerException();
	}
        loggerLevel = newLevel;
    }
    
    public Level getLevel() {
        return loggerLevel;
    }
    
    public void info(String text) {
        log(Level.INFO, text);
    }
    
    public void warning(String text) {
        log(Level.WARNING, text);
    }
    
    public void config(String text) {
        log(Level.CONFIG, text);
    }
    
    public void severe(String text) {
        log(Level.SEVERE, text);
    }
    
    public void fine(String text) {
        log(Level.FINE, text);
    }
    
    public void finer(String text) {
        log(Level.FINER, text);
    }
    
    public void finest(String text) {
        log(Level.FINEST, text);
    }
    
    public void log(Level level, String text) {
        log(new LogRecord(level, text));
    }
    
    public void log(LogRecord lr) {
        
        if (lr.getLevel().intValue() > loggerLevel.intValue() 
                && loggerLevel.intValue() != Level.OFF.intValue()) {
            
            handlers.get(0).publish(lr);
            
        }

    }
    
}
