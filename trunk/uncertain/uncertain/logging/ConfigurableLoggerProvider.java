/*
 * Created on 2009-4-3
 */
package uncertain.logging;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;

public class ConfigurableLoggerProvider implements ILoggerProvider {
    
    static final DefaultFormatter DEFAULT_FORMATTER = new DefaultFormatter(); 
    
    TopicManager        mTopicManager;
    Handler[]           mHandlers;
    Map                 mTopicLoggerMap;
    //ILogger             mDefaultLogger;
    Level               mDefaultLevel;
    Formatter           mFormatter = DEFAULT_FORMATTER;
    
    public ConfigurableLoggerProvider(){
        mTopicManager = new TopicManager();
        mTopicLoggerMap = new HashMap();
    }
    
    public void addTopics( LoggingTopic[] topics ){
        mTopicManager.setTopicLevel(topics);
    }
    
    public void addHandles( Handler[] handles){
        for(int i=0; i<handles.length; i++)
            handles[i].setFormatter(mFormatter);
        mHandlers = handles;
    }
    
    public ILogger getLogger( String topic ){
        if( mTopicManager.isLoggingEnabled(topic)){
            ILogger logger = createEmptyLogger( topic );
            //System.out.println("Setting level:"+logger+" -> " + mTopicManager.getTopicLevel(topic).getName());
            logger.setLevel( mTopicManager.getTopicLevel(topic));
            return logger;
        }
        return DummyLogger.DEFAULT_LOGGER;
    }
    
    public void setDefaultLevel( String level ){
        mDefaultLevel = Level.parse(level);
    }
    
    public String getDefaultLevel(){
        return mDefaultLevel.getName();
    }   
    
    
    protected ILogger createEmptyLogger(String topic){
        DefaultLogger logger = new DefaultLogger(topic);
        if(mHandlers!=null)
            for(int i=0; i<mHandlers.length; i++){
                logger.addHandler(mHandlers[i]);
            }
        return logger;
    }
    
    public TopicManager getTopicManager(){
        return mTopicManager;
    }

}
