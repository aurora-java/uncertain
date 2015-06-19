/*
 * Created on 2011-6-15 下午03:19:45
 * $Id$
 */
package uncertain.exception;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;


public class GeneralException extends BaseRuntimeException {

    /**
     * @param code
     * @param args
     * @param cause
     */
    public GeneralException(String code, Object[] args, Throwable cause) {
        super(code, args, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param code
     * @param args
     * @param location
     */
    public GeneralException(String code, Object[] args, ILocatable location) {
        super(code, args, location);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param code
     * @param args
     * @param cause
     * @param config
     */
    public GeneralException(String code, Object[] args, Throwable cause,
            CompositeMap config) {
        super(code, args, cause, config);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param code
     * @param args
     * @param cause
     * @param location
     */
    public GeneralException(String code, Object[] args, Throwable cause,
            ILocatable location) {
        super(code, args, cause, location);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param code
     * @param args
     * @param cause
     * @param source
     * @param location
     */
    public GeneralException(String code, Object[] args, Throwable cause,
            String source, Location location) {
        super(code, args, cause, source, location);
        // TODO Auto-generated constructor stub
    }




}
