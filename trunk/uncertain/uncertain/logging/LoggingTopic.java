/*
 * Created on 2009-4-3
 */
package uncertain.logging;

import java.util.logging.Level;

public class LoggingTopic {
    
    String  mName;
    String  mLevel = "INFO";
    Level   mLevelObject = Level.INFO;
    
    public LoggingTopic(){
        
    }
    
    public LoggingTopic( String name, Level level ){
        setName(name);
        setLevelObject(level);
    }
    
    /**
     * @return the name of topic
     */
    public String getName() {
        return mName;
    }
    /**
     * @param name the name of topic to set
     */
    public void setName(String name) {
        this.mName = name;
    }
    
    /**
     * @return the topic's logging level
     */
    public String getLevel() {
        return mLevel;
    }
    
    /**
     * @param level the topic's logging level to set
     */
    public void setLevel(String level) {
        this.mLevel = level;
        mLevelObject = Level.parse(level);
    }
    
    public void setLevelObject(Level level){
        this.mLevelObject = level;
        this.mLevel = level.getName();
    }
    
    public Level getLevelObject(){
        return mLevelObject;
    }

}
