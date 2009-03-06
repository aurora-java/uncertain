/*
 * Created on 2005-7-31
 */
package uncertain.init;

import java.util.logging.Level;
import java.util.logging.Logger;

import uncertain.core.UncertainEngine;

/**
 * LoggingConfig
 * @author Zhou Fan
 * 
 */
public class LoggingConfig {
    
    Logger				logger;
    String				levelName;
    Level				loggingLevel;
    UncertainEngine		engine;

    /**
     * Default constructor
     */
    public LoggingConfig(UncertainEngine engine) {
        this.engine = engine;
        logger = Logger.getLogger(UncertainEngine.UNCERTAIN_LOGGING_SPACE);
    }
    
    public void setLevel(String level){
        levelName = level;
        loggingLevel = Level.parse(levelName);
        logger.setLevel(loggingLevel);
    }
    
    public Logger getLogger(){
        return logger;
    }

}
