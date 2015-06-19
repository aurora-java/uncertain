/*
 * Created on 2009-4-2
 */
package uncertain.logging;

public class DummyLoggerProvider implements ILoggerProvider {
    
    static final DummyLoggerProvider DEFAULT_INSTANCE = new  DummyLoggerProvider();
    
    public static DummyLoggerProvider getInstance(){
        return DEFAULT_INSTANCE;
    }

    public ILogger getLogger(String topic) {        
        return DummyLogger.DEFAULT_LOGGER;
    }
    
    public ILogger getDefaultLogger(){
        return DummyLogger.DEFAULT_LOGGER;
    }
    /*
    public String getName(){
        return "dummy";
    }
    */
}
