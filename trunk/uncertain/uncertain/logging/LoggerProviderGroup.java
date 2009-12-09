/*
 * Created on 2009-4-3
 */
package uncertain.logging;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LoggerProviderGroup extends AbstractLoggerProvider implements ILoggerProviderGroup {    
    
    Set         mProviderSet;
    String      mName;
    
    public LoggerProviderGroup(){
        mProviderSet = new HashSet();
    }
    
    public void addLoggerProvider( ILoggerProvider provider ){
        mProviderSet.add(provider);
    }

    public ILogger getLogger(String topic) {
        LoggerList logger_group = null;
        for(Iterator it = mProviderSet.iterator(); it.hasNext(); ){
            ILoggerProvider provider = (ILoggerProvider)it.next();
            ILogger logger = provider.getLogger(topic);
            if( logger != null && logger != DummyLogger.DEFAULT_LOGGER){                
                if( logger_group==null)
                    logger_group = new LoggerList();
                logger_group.addLogger(logger);
            }
        }
        if(logger_group==null)
            return DummyLogger.DEFAULT_LOGGER;
        else{
            if(logger_group.size()==1)
                return logger_group.getFirst();
            else
                return logger_group;
        }
    }
    
    public Collection getLoggerProviders(){
        return mProviderSet;
    }
/*    
    String      mName;
     
    public String getName(){
        return mName;
    }
    
    public void setName( String name ){
        mName = name;
    }
*/
}
