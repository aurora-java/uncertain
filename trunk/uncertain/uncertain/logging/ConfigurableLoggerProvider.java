/*
 * Created on 2009-4-3
 */
package uncertain.logging;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import uncertain.ocm.IConfigureListener;

public class ConfigurableLoggerProvider implements ILoggerProvider  {
    
    static final DefaultFormatter DEFAULT_FORMATTER = new DefaultFormatter(); 
    
    TopicManager        mTopicManager;
    Handler[]           mHandlers;
    Map                 mTopicLoggerMap;
    Level               mDefaultLevel;
    Formatter           mFormatter = DEFAULT_FORMATTER;
    String              mName;
    String              mLogPath;
    
    public ConfigurableLoggerProvider(){
        mTopicManager = new TopicManager();
        mTopicLoggerMap = new HashMap();
    }
    
    public ConfigurableLoggerProvider( TopicManager topic_manager ){
        mTopicManager = topic_manager;
        mTopicLoggerMap = new HashMap();
    }
    
    public void addTopics( LoggingTopic[] topics ){
        mTopicManager.setTopicLevel(topics);
    }
    
    public void addHandles( Handler[] handles){
        mHandlers = handles;
        for(int i=0; i<mHandlers.length; i++){
            Handler h = mHandlers[i]; 
            h.setFormatter(mFormatter);
            if( h instanceof IFileBasedLogger && mLogPath != null){
                ((IFileBasedLogger)h).setBasePath(mLogPath);
            }
        }          
    }
    
    public Handler[] getHandlers(){
        return mHandlers;
    }
    
    public ILogger getLogger( String topic ){
        if( mTopicManager.isLoggingEnabled(topic)){
            ILogger logger = createEmptyLogger( topic );
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
    
    public void setTopicManager( TopicManager m ){
        mTopicManager = m;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * @return the logPath
     */
    public String getLogPath() {
        return mLogPath;
    }

    /**
     * @param logPath the logPath to set
     */
    public void setLogPath(String logPath) {
        //System.out.println("logpath="+logPath);
        mLogPath = logPath;
        File file = new File(mLogPath);
        if(!file.exists()){
            throw new IllegalArgumentException("Log file path '"+mLogPath+"' does not exist");
        }        
        //System.out.println("["+mName+"] Setting log path to "+ this.mLogPath);
    }

}
