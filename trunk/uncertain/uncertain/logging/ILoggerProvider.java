/*
 * Created on 2009-3-10
 */
package uncertain.logging;

public interface ILoggerProvider {
    
    public ILogger getLogger( String topic );

}
