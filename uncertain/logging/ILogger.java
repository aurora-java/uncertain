/*
 * Created on 2009-3-3
 */
package uncertain.logging;

import java.util.Map;

public interface ILogger {
    
    public void log( Map record );
    
    public void fireLogEvent( String event_name, Object[] params );
    
    public void logException( String msg, Throwable thr );

}
