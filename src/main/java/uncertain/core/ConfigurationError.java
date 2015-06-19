/*
 * Created on 2005-7-26
 */
package uncertain.core;

import uncertain.composite.CompositeMap;

/**
 * Indicate a runtime error that configuration is incorrect
 * @author Zhou Fan
 * 
 */
public class ConfigurationError extends RuntimeException {
    
    //TODO Append location info
    public ConfigurationError( CompositeMap source, String message ){
        super(message);
    }
    
    public ConfigurationError( CompositeMap source, String message, Throwable cause ){
        super(message, cause);
    }

    /**
     * 
     */
    public ConfigurationError() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ConfigurationError(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ConfigurationError(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ConfigurationError(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
