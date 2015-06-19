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
        BaseRuntimeException {
    
    /**
     * @param code Code of this exception. see UncertainFramework exception i18n support.
     * @param args Arguments of exception message
     * @param cause Origin exception
     * @param config Source config that causes this exception
     */
    public ConfigurationFileException( String code, Object[] args,Throwable cause, CompositeMap config ){
        super(code,args,cause,config);
    }

    public ConfigurationFileException( String code, Object[] args, Throwable cause,  String source, Location location ){
        super(code,args,cause,source,location);
    }
    
    public ConfigurationFileException( String code, Object[] args, Throwable cause, ILocatable location ){
        super(code,args,cause,location!=null?location.getOriginSource():"", location!=null?location.getOriginLocation():null);
    }
    
    public ConfigurationFileException( String code, Object[] args,ILocatable location ){
        super(code,args,null,location);
    }


}
