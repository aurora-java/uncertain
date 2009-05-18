/*
 * Created on 2009-4-7
 */
package uncertain.logging;

import uncertain.composite.CompositeMap;
import uncertain.event.RuntimeContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.logging.DummyLoggerProvider;

public class LoggingContext extends RuntimeContext {
    
    public static ILoggerProvider getLoggerProvider( CompositeMap context ){
        if(context==null) return DummyLoggerProvider.getInstance();
        ILoggerProvider p = (ILoggerProvider)RuntimeContext.getInstance(context).getInstanceOfType(ILoggerProvider.class);
        return p==null ? DummyLoggerProvider.getInstance(): p;
    }
    
    public static ILoggerProvider getLoggerProvider( IObjectRegistry reg ){
        ILoggerProvider p = (ILoggerProvider)reg.getInstanceOfType(ILoggerProvider.class);
        return p==null?DummyLoggerProvider.getInstance():p;
    }
    
    public static ILogger  getLogger( String topic, IObjectRegistry reg ){
        ILoggerProvider p = (ILoggerProvider)reg.getInstanceOfType(ILoggerProvider.class);
        return p==null ? DummyLogger.getInstance(): p.getLogger(topic);
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
