/*
 * Define interface that can add multiple ILoggerProvider instances
 * Created on 2009-4-7
 */
package uncertain.logging;

import java.util.Collection;

import uncertain.ocm.IObjectRegistry;

public interface ILoggerProviderGroup {
    
    public void addLoggerProvider( ILoggerProvider another );
    
    public Collection getLoggerProviders();
    
    public void registerTo( IObjectRegistry reg );
    
    public ILoggerProvider joinTogether( ILoggerProvider existing_provider);    

}
