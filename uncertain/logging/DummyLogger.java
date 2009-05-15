/*
 * Created on 2009-3-29
 */
package uncertain.logging;

import java.util.Iterator;
import java.util.logging.Level;

public class DummyLogger implements ILogger {
    
    static final ILogger DEFAULT_LOGGER = new DummyLogger();
    
    public static final ILogger getInstance(){
        return DEFAULT_LOGGER;
    }

    public void log( String message ){
        
    }
    
    public void log( String message, Object[] parameters ){
        
    }
    
    public void log( Level level, String message ){
        
    }
    
    public void log( Level level, String message, Object[] parameters ){
        
    }
    
    public void setLevel( Level level ){
        
    }
    
    public void log( Level level, String message, Throwable thr ){
        
    }
    
    public void warning( String message ){
        
    }
    
    public void severe( String message ){
            
    }
    
    public void info( String message ){
        
    }
    
    public void config( String message ){
        
    }
}
