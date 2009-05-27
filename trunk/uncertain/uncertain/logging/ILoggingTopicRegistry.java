/*
 * Created on 2009-5-25
 */
package uncertain.logging;

/**
 * Maintains a list of all known logging topics
 * ILoggingTopicRegistry
 *
 */
public interface ILoggingTopicRegistry {
    
    public void registerLoggingTopic( String topic );
    
    public void registerLoggingTopic( LoggingTopic topic );
    
    public LoggingTopic[] getLoggingTopics();

}
