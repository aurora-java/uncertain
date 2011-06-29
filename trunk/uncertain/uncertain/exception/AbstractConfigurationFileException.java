/*
 * Created on 2005-7-26
 */
package uncertain.exception;

import java.io.File;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * Indicate a runtime error that there is error in configuration file
 * @author Zhou Fan
 * 
 */
public abstract class AbstractConfigurationFileException extends RuntimeException implements ILocatable {
    
    public static final String KEY_UNCERTAIN_EXCEPTION_SOURCE_FILE = "uncertain.exception.source_file";
    
    public static String getLocationMessage( String source, int line, int row ){
        return MessageFactory.getMessage(KEY_UNCERTAIN_EXCEPTION_SOURCE_FILE, new Object[]{source, new Integer(line), new Integer(row)} );
    }

    Location        location;
    String          sourceFile;

    /**
     * 
     */
    public AbstractConfigurationFileException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public AbstractConfigurationFileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public AbstractConfigurationFileException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public AbstractConfigurationFileException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public Location getOriginLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getOriginSource() {
        return sourceFile;
    }
    
    public void setSource(String sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    public void setCauseConfig( CompositeMap config ){
        location = config.getLocation();
        File file = config.getSourceFile();
        sourceFile = file == null? null: file.getAbsolutePath();
    }

    public String getMessage() {
        if(location!=null)
            return getLocationMessage(sourceFile,location.getStartLine(),location.getStartColumn()) + super.getMessage();
        else
            return super.getMessage();
    }

}
