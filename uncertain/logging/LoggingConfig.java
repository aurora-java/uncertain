/*
 * Created on 2009-4-7
 */
package uncertain.logging;

import java.util.Collection;
import java.util.Collections;

import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;
import uncertain.ocm.IObjectRegistry;

public class LoggingConfig implements ILoggerProvider, IContextListener {

    /** Internal registry file */
    public static final String LOGGING_REGISTRY_PATH = "uncertain.logging.DefaultRegistry";
    
    LoggerProviderGroup         mLoggerProviderGroup;
    IObjectRegistry             mObjectRegistry;
    
    public LoggingConfig(){
        mLoggerProviderGroup = new LoggerProviderGroup();
    }
    
    public LoggingConfig( IObjectRegistry reg ){
        this();
        mObjectRegistry = reg;
    }

    public ILogger getLogger(String topic) {
        return mLoggerProviderGroup.getLogger(topic);
    }
    
    public void addProviders( ILoggerProvider[] providers){
        for(int i=0; i<providers.length; i++)
            mLoggerProviderGroup.addLoggerProvider(providers[i]);
    }
    
    public ILoggerProvider joinTogether( ILoggerProvider existing_provider){
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
    
    public void initializeContext( RuntimeContext context ){
        ILoggerProvider lp  =  (ILoggerProvider)context.getInstanceOfType(ILoggerProvider.class);
        if(lp!=null){
            ILoggerProvider p = joinTogether(lp);
            context.setInstanceOfType(ILoggerProvider.class, p);
        }else
            context.setInstanceOfType(ILoggerProvider.class, this);
    }
    
   public void onContextCreate( RuntimeContext context ){
       initializeContext( context);
   }
    
    public void onContextDestroy( RuntimeContext context ){
        
    }
    
    public void onInitialize(){
        registerTo(mObjectRegistry);
    }
    
    public void registerTo(IObjectRegistry os ){        
        ILoggerProvider existing_provider = (ILoggerProvider)os.getInstanceOfType(ILoggerProvider.class);
        if(existing_provider==null){
            os.registerInstance(ILoggerProvider.class, this);
        }else{
            ILoggerProvider p = joinTogether(existing_provider);
            os.registerInstance(ILoggerProvider.class, p);
        }
    }
    
    public Collection getLoggerProviders(){
        return Collections.unmodifiableCollection(mLoggerProviderGroup.mProviderSet); 
    }

}
