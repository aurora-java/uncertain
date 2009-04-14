/*
 * Define interface that can add multiple ILoggerProvider instances
 * Created on 2009-4-7
 */
package uncertain.logging;

public interface ILoggerProviderGroup {
    
    public void addLoggerProvider( ILoggerProvider another );

}
