/*
 * Created on 2009-3-3
 */
package uncertain.logging;

import java.util.logging.Level;

public interface ILogger {
    
    public void log( String message );
    
    public void log( String message, Object[] parameters );
    
    public void log( Level level, String message );
    
    public void log( Level level, String message, Throwable thrown );
    
    public void log( Level level, String message, Object[] parameters );
    
    public void config( String message );
    
    public void info( String message );
    
    public void warning( String message );
    
    public void severe( String message );
    
    public void setLevel( Level level );
    
}
