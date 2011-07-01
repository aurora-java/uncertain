/*
 * Created on 2011-6-24 下午02:34:54
 * $Id$
 */
package uncertain.exception;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 *  This exception is used widely in framework to indicate there is something wrong in 
 *  configuration file. The message must conform to UncertainFramework exception 
 *  message convention 
 */
public class ConfigurationFileException extends
        AbstractConfigurationFileException {
    
    /**
     * @param code Code of this exception. see UncertainFramework exception i18n support.
     * @param args Arguments of exception message
     * @param cause Origin exception
     * @param config Source config that causes this exception
     */
    public ConfigurationFileException( String code, Object[] args,Throwable cause, CompositeMap config ){
        super( MessageFactory.getMessage(code, args), cause);
        super.setCauseConfig(config);
    }

    public ConfigurationFileException( String code, Object[] args, Throwable cause,  String source, Location location ){
        super( MessageFactory.getMessage(code, args), cause);
        super.setSource(source);
        super.setLocation(location);
    }
    
    public ConfigurationFileException( String code, Object[] args, Throwable cause, ILocatable location ){
        this(code,args,cause,location.getOriginSource(), location.getOriginLocation());
    }
    
    public ConfigurationFileException( String code, Object[] args,ILocatable location ){
        this(code,args,null,location);
    }

}
