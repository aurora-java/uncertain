/*
 * Created on 2011-7-19 下午10:16:03
 * $Id$
 */
package uncertain.logging;

/**
 * Provides per-object level logging config.
 */
public interface IPerObjectLoggingConfig {
    
    /** get/set trace flag of specified object */
    public boolean getTraceFlag( String object_name );
    
    public void setTraceFlag( String object_name, boolean flag );
    
    /** get/set ILoggerProvider for specified object */
    public ILoggerProvider getLoggerProvider( String object_name );
    
    public void setLoggerProvider( String object_name, ILoggerProvider provider );
    
    public void clearSettings();
    

}
