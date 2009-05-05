/*
 * Created on 2009-4-7
 */
package uncertain.logging;

import uncertain.composite.CompositeMap;
import uncertain.event.RuntimeContext;

public class LoggingContext extends RuntimeContext {
    
    public static ILoggerProvider getLoggerProvider( CompositeMap context ){
        if(context==null) return DummyLoggingProvider.getInstance();
        ILoggerProvider p = (ILoggerProvider)RuntimeContext.getInstance(context).getInstanceOfType(ILoggerProvider.class);
        return p==null ? DummyLoggingProvider.getInstance(): p;
    }
    
    public static ILogger getLogger( CompositeMap context, String topic ){
        return getLoggerProvider(context).getLogger(topic);
    }
    
    public static ILogger getErrorLogger( CompositeMap context ){
        return getLogger(context, "error");
    }
    
    public static LoggingContext getLoggingContext( CompositeMap m ){
        LoggingContext lc = new LoggingContext();
        lc.initialize(m);
        return lc;
    }
    
    public ILoggerProvider getLoggerProvider(){
        return (ILoggerProvider)getInstanceOfType(ILoggerProvider.class);
    }
    
    public void setLoggerProvider( ILoggerProvider provider ) {
        setInstanceOfType(ILoggerProvider.class, provider);        
    }

}
