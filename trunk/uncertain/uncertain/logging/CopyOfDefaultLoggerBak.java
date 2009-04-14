/*
 * Created on 2009-4-3
 */
package uncertain.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyOfDefaultLoggerBak implements ILogger {
    
    //static long counter = 0;
    
    Logger      mLogger;
    String      mTopic;
    
    private void initHandles(Logger logger){
        logger.setUseParentHandlers(false);
        Handler[] handlers = logger.getHandlers();
        for(int i=0; i<handlers.length; i++)
            logger.removeHandler(handlers[i]);
    }
    
    public CopyOfDefaultLoggerBak(){
        mLogger = Logger.getAnonymousLogger();
        initHandles(mLogger);
    }
    
    public CopyOfDefaultLoggerBak( String topic ){
        mLogger = Logger.getLogger(topic);
        mTopic = topic;
        initHandles(mLogger);
    }
    
    public CopyOfDefaultLoggerBak( Logger logger ){
        mLogger = logger;
    }
    
    public Logger getLoggerInstance(){
        return mLogger;
    }

    public void log(String message) {
        mLogger.log(Level.INFO, message);
    }

    public void log(String message, Object[] parameters) {
       mLogger.log(Level.INFO, message, parameters);
    }

    public void log(Level level, String message) {
       mLogger.log(level, message);
    }

    public void log(Level level, String message, Object[] parameters) {
        mLogger.log(level, message, parameters);
    }

    public void setLevel( Level level ){
        mLogger.setLevel(level);
    }
    
    
    public void warning( String message ){
        log(Level.WARNING, message);
    }
    
    public void severe( String message ){
        log(Level.SEVERE, message);    
    }
    
    public void info( String message ){
        log(Level.INFO, message);
    }
    
    public void log( Level level, String message, Throwable thrown ){
        mLogger.log(level, message, thrown);
    }
    
    public String toString(){
        return this.getClass().getName() +":" + mTopic;
    }
    
    
}
