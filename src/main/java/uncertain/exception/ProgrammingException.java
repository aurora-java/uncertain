/*
 * Created on 2011-7-10 上午12:59:22
 * $Id$
 */
package uncertain.exception;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * Indicates that there is error in program, and this exception should be inspected by programmer.
 * ProgrammingException
 */
public class ProgrammingException extends BaseRuntimeException {

    public ProgrammingException(String code, Object[] args, Throwable cause) {
        super(code, args, cause);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(String code, Object[] args, Throwable cause,
            CompositeMap config) {
        super(code, args, cause, config);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(String code, Object[] args, Throwable cause,
            String source, Location location) {
        super(code, args, cause, source, location);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(String code, Object[] args, Throwable cause,
            ILocatable location) {
        super(code, args, cause, location);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingException(String code, Object[] args, ILocatable location) {
        super(code, args, location);
        // TODO Auto-generated constructor stub
    }

}
