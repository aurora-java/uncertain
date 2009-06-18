/**
 * Maintains logging topic enable flag 
 * Created on 2009-3-30
 */
package uncertain.logging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TopicManager implements ILoggingTopicRegistry {
    
    Map     mTopicLevelMap;    

    public TopicManager(){
        mTopicLevelMap = new HashMap();
    }
    
    public void setTopicLevel( String topic, Level level ){
        mTopicLevelMap.put(topic, level );
    }
    
    public void setTopicLevel( LoggingTopic topic ){
        setTopicLevel(topic.getName(), topic.getLevelObject());
    }
    
    public void setTopicLevel( LoggingTopic[] array ){
        for(int i=0; i<array.length; i++)
            setTopicLevel(array[i]);
    }
    
    public Level getTopicLevel( String topic ){
        Level l = (Level)mTopicLevelMap.get(topic);
        if(l==null)
            return Level.OFF;
        return l;
    }
    
    public boolean isLoggingEnabled( String topic ){
        return getTopicLevel( topic ).intValue()<Level.OFF.intValue();
    }
    
    public LoggingTopic[] getLoggingTopics(){
        Collection lst = mTopicLevelMap.values();
        LoggingTopic[] array = new  LoggingTopic[lst.size()];
        System.arraycopy(lst.toArray(), 0, array, 0, array.length);
        return array;
    }
    
    public void registerLoggingTopic( LoggingTopic topic ){
        setTopicLevel(topic);
    }
    
    public void registerLoggingTopic( String topic ){
        LoggingTopic t = new LoggingTopic();
        t.setName(topic);
        t.setLevelObject(Level.WARNING);
        registerLoggingTopic( t );
    }
    
}
