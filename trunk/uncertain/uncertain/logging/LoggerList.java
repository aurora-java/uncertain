/* Chain a list of logger together and dispatch logging message to each logger
 * Created on 2009-4-2
 */
package uncertain.logging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class LoggerList implements ILogger {
    
    Set    mLoggerSet;
    
    public LoggerList(){
        mLoggerSet = new HashSet();
    }
    
    public LoggerList( List loggers ){
        mLoggerSet = new HashSet();
        mLoggerSet.addAll(loggers);
    }
    
    public void addLogger( ILogger logger ){
        mLoggerSet.add(logger);
    }

    public void log(String message) {
        for(Iterator it=mLoggerSet.iterator(); it.hasNext(); ){
            ILogger logger = (ILogger)it.next();
            logger.log(message);
        }        
    }

    public void log(String message, Object[] parameters) {
        for(Iterator it=mLoggerSet.iterator(); it.hasNext(); ){
            ILogger logger = (ILogger)it.next();
            logger.log(message, parameters);
        }        
    }

    public void log(Level level, String message) {
        for(Iterator it=mLoggerSet.iterator(); it.hasNext(); ){
            ILogger logger = (ILogger)it.next();
            logger.log(level, message);
        }
    }

    public void log(Level level, String message, Object[] parameters) {
        for(Iterator it=mLoggerSet.iterator(); it.hasNext(); ){
            ILogger logger = (ILogger)it.next();
            logger.log(level, message, parameters);
        }
    }
    
    public void setLevel( Level level ){
        for(Iterator it=mLoggerSet.iterator(); it.hasNext(); ){
            ILogger logger = (ILogger)it.next();
            logger.setLevel(level);
        }
    }
    
    public void log( Level level, String message, Throwable thrown ){
        for(Iterator it=mLoggerSet.iterator(); it.hasNext(); ){
            ILogger logger = (ILogger)it.next();
            logger.log(level, message, thrown);
        }
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
    
    public void config( String message ){
        log(Level.CONFIG, message);    
    }      
    
    public int size(){
        return mLoggerSet.size();
    }
    
    public ILogger getFirst(){
        Iterator it = mLoggerSet.iterator();
        if(it.hasNext()){
            return (ILogger)it.next();
        }else
            return null;
    }

}
