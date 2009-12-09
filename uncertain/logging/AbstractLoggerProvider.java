/*
 * Created on 2009-11-19 ÏÂÎç02:32:28
 * Author: Zhou Fan
 */
package uncertain.logging;

import uncertain.ocm.IObjectRegistry;

public abstract class AbstractLoggerProvider implements ILoggerProvider {


    public ILoggerProvider joinTogether(ILoggerProvider existing_provider) {
        if(existing_provider==null) return this;
        if(existing_provider instanceof ILoggerProviderGroup){
            ((ILoggerProviderGroup)existing_provider).addLoggerProvider(this);
            return existing_provider;
        }
        else{
            LoggerProviderGroup group = new LoggerProviderGroup();
            group.addLoggerProvider(this);
            group.addLoggerProvider(existing_provider);
            return group;
        }          
    }

    public void registerTo(IObjectRegistry os) {        
        ILoggerProvider existing_provider = (ILoggerProvider)os.getInstanceOfType(ILoggerProvider.class);
        if(existing_provider==null){
            os.registerInstance(ILoggerProvider.class, this);
        }else{
            ILoggerProvider p = joinTogether(existing_provider);
            os.registerInstance(ILoggerProvider.class, p);
        }
    }

}