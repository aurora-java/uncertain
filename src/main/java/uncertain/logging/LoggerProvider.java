/*
 * Created on 2009-4-3
 */
package uncertain.logging;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;

import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;

public class LoggerProvider extends AbstractLoggerProvider implements ILogPathSettable, IContextListener {
    
    static final DefaultFormatter DEFAULT_FORMATTER = new DefaultFormatter(); 
    
    TopicManager        mTopicManager;
    Handler[]           mHandlers;
    //Map                 mTopicLoggerMap;
    Level               mDefaultLevel = Level.WARNING;
    Formatter           mFormatter = DEFAULT_FORMATTER;
    String              mName;
    String              mLogPath;
    // not configured topic -> logger
    Map                 mDefaultLoggerMap = new HashMap();
    
    public static LoggerProvider createInstance(){
        LoggerProvider provider = new LoggerProvider();
        provider.addHandles( new Handler[] { new BasicConsoleHandler()});
        return provider;
    }
    
    public static LoggerProvider createInstance( Level level, OutputStream log_output ){
        LoggerProvider provider = new LoggerProvider();
        provider.setDefaultLogLevel(level.toString());
        BasicStreamHandler handler = new BasicStreamHandler( log_output );
        provider.addHandle(handler);
        return provider;
    }
   
    public static LoggerProvider createInstance( String topic, Level level ){
        LoggerProvider provider = createInstance();
        provider.getTopicManager().setTopicLevel(topic, level);
        return provider;
    }
    
    public LoggerProvider(){
        mTopicManager = new TopicManager();
        //mTopicLoggerMap = new HashMap();
    }
    
    public LoggerProvider( TopicManager topic_manager ){
        mTopicManager = topic_manager;
        //mTopicLoggerMap = new HashMap();
    }
    
    public void addTopics( LoggingTopic[] topics ){
        mTopicManager.setTopicLevel(topics);
    }
    
    public void addHandles( Handler[] handles){
        mHandlers = handles;
        for(int i=0; i<mHandlers.length; i++){
            Handler h = mHandlers[i]; 
            h.setFormatter(mFormatter);
            if( h instanceof ILogPathSettable && mLogPath != null){
                ((ILogPathSettable)h).setLogPath(mLogPath);
            }
        }          
    }
    
    /** Add a single handler */
    public void addHandle( Handler handler ){
        addHandles( new Handler[]{handler} );
    }
    
    public Handler[] getHandlers(){
        return mHandlers;
    }
    
    protected ILogger getDefaultLogger( String topic ){
        ILogger logger = (ILogger)mDefaultLoggerMap.get(topic);
        if(logger==null){
            logger = createEmptyLogger( topic );
            logger.setLevel(mDefaultLevel);
            mDefaultLoggerMap.put(topic, logger);
        }
        return logger;
    }
    
    public ILogger getLogger( String topic ){
        if( mTopicManager.isLoggingEnabled(topic)){
            ILogger logger = createEmptyLogger( topic );
            logger.setLevel( mTopicManager.getTopicLevel(topic));
            return logger;
        }
        return getDefaultLogger(topic);
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
        mLogPath = logPath;
/*
        File file = new File(mLogPath);
        if(!file.exists()){
            throw new IllegalArgumentException("Log file path '"+mLogPath+"' does not exist");
        }
*/                
    }

    public String getDefaultLogLevel() {
        return mDefaultLevel.toString();
    }

    public void setDefaultLogLevel(String defaultLogLevel) {
        mDefaultLevel = Level.parse(defaultLogLevel);
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
}
