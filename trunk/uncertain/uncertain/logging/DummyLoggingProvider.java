/*
 * Created on 2009-4-2
 */
package uncertain.logging;

public class DummyLoggingProvider implements ILoggerProvider {
    
    static final DummyLoggingProvider DEFAULT_INSTANCE = new  DummyLoggingProvider();
    
    public static DummyLoggingProvider getInstance(){
        return DEFAULT_INSTANCE;
    }

    public ILogger getLogger(String topic) {        
        return DummyLogger.DEFAULT_LOGGER;
    }
    /*
    public String getName(){
        return "dummy";
    }
    */
}
