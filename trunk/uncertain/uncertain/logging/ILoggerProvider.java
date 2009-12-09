/*
 * Created on 2009-3-10
 */
package uncertain.logging;

/**
 *  Provide logger instance by specifying topic
 */

public interface ILoggerProvider {
    
    /** Get a ILogger instance in specified topic */
    public ILogger getLogger( String topic );
    
    /** Get default logger for non-configured topic */
    // public ILogger getDefaultLogger();

}
