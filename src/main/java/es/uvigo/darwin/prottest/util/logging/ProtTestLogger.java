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
    public static final Level DEFAULT_LEVEL = Level.INFO;
    
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
    
    public static ProtTestLogger getLogger(String loggerName, boolean force) {
        if (!loggers.containsKey(loggerName)) {
            if (force)
                loggers.put(loggerName, new ProtTestLogger(loggerName));
            else
                return null;
        }
        return loggers.get(loggerName);
    }
    
    public static boolean exists(Class classLogger) {
        return getLogger(classLogger.getName(), false) != null;
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
        handlers.add(stdHandler);
    }
    
    public static ProtTestLogger getDefaultLogger() {
        return getLogger(DEFAULT_LOGGER_NAME, true);
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
    
    public void addHandler(Handler[] newHandlers) {
        for (Handler handler : newHandlers) {
            if (handler == null) {
                throw new NullPointerException();
            }
            if (!handlers.contains(handler))
                this.handlers.add(handler);
        }
    }
    
    public Handler addHandler(OutputStream out) {
        return addHandler(out, DEFAULT_LEVEL);
    }
    
    public Handler addHandler(OutputStream out, Level level) {
        if (out == null) {
	    throw new NullPointerException();
	}
        Handler handler = new StreamHandler(out, 
                new ProtTestLogFormatter());
        handler.setLevel(level);
        this.handlers.add(handler);
        return handler;
    }
    
    public Handler addHandler(Writer out) {
        return addHandler(out, DEFAULT_LEVEL);
    }
    
    public Handler addHandler(Writer out, Level level) {
        if (out == null) {
	    throw new NullPointerException();
	}
        Handler handler = new StreamHandler(
                new WriterOutputStream(out),
                new ProtTestLogFormatter());
        handler.setLevel(level);
        this.handlers.add(handler);
        return handler;
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
    
    public void infoln(String text) {
        log(Level.INFO, text + "\n");
        flush();
    }
    
    public void warningln(String text) {
        log(Level.WARNING, text + "\n");
        flush();
    }
    
    public void configln(String text) {
        log(Level.CONFIG, text + "\n");
        flush();
    }
    
    public void severeln(String text) {
        log(Level.SEVERE, text + "\n");
        flush();
    }
    
    public void fineln(String text) {
        log(Level.FINE, text + "\n");
        flush();
    }
    
    public void finerln(String text) {
        log(Level.FINER, text + "\n");
        flush();
    }
    
    public void finestln(String text) {
        log(Level.FINEST, text + "\n");
        flush();
    }
    
    public static void info(String text, Class loggingClass) {
        log(Level.INFO, text, loggingClass);
    }
    
    public static void warning(String text, Class loggingClass) {
        log(Level.WARNING, text, loggingClass);
    }
    
    public static void config(String text, Class loggingClass) {
        log(Level.CONFIG, text, loggingClass);
    }
    
    public static void severe(String text, Class loggingClass) {
        log(Level.SEVERE, text, loggingClass);
    }
    
    public static void fine(String text, Class loggingClass) {
        log(Level.FINE, text, loggingClass);
    }
    
    public static void finer(String text, Class loggingClass) {
        log(Level.FINER, text, loggingClass);
    }
    
    public static void finest(String text, Class loggingClass) {
        log(Level.FINEST, text, loggingClass);
    }
    
    public static void infoln(String text, Class loggingClass) {
        log(Level.INFO, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void warningln(String text, Class loggingClass) {
        log(Level.WARNING, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void configln(String text, Class loggingClass) {
        log(Level.CONFIG, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void severeln(String text, Class loggingClass) {
        log(Level.SEVERE, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void fineln(String text, Class loggingClass) {
        log(Level.FINE, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void finerln(String text, Class loggingClass) {
        log(Level.FINER, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void finestln(String text, Class loggingClass) {
        log(Level.FINEST, text + "\n", loggingClass);
        flush(loggingClass);
    }
    
    public static void log(Level level, String text, Class loggingClass) {
        // default logging + class logging
        getDefaultLogger().log(level, text);
        if (exists(loggingClass))
            getLogger(loggingClass.getName(), false).log(level, text);
    }
    
    public void log(Level level, String text) {
        log(new LogRecord(level, text));
    }
    
    public void log(LogRecord lr) {
        
        if (lr.getLevel().intValue() > loggerLevel.intValue() 
                && loggerLevel.intValue() != Level.OFF.intValue()) {
    
            for (Handler handler : handlers)
                handler.publish(lr);
            
        }

    }
    
    public void flush() {
        for (Handler handler : handlers)
            handler.flush();
    }
    
    public static void flush(Class loggingClass) {
        getDefaultLogger().flush();
        if (exists(loggingClass))
            getLogger(loggingClass.getName(), false).flush();
    }
}
