/**
 * Extends JDK logger as an adaptor to ILogger interface
 * Created on 2009-4-3
 */
package uncertain.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class DefaultLogger extends Logger implements ILogger {
    
    public DefaultLogger( String name ){
        super(name, null);
        setLevel(Level.INFO);
        setUseParentHandlers(false);
    }

    public void log( String message ){
        super.log( Level.INFO, message);
    }
    
    public void log( String message, Object[] parameters ){
        super.log(getLevel(), message, parameters );
    }
    
    public String toString(){
        return this.getClass().getName() +":" + getName();
    }
    
}
