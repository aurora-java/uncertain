/*
 * Created on 2011-7-02
 */
package uncertain.exception;

import java.io.File;

import uncertain.composite.CompositeMap;
import uncertain.proc.trace.IWithProcedureStackTrace;
import uncertain.proc.trace.TraceElement;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * This abstract exception is same as BaseException, except that this class
 * is derived from RuntimeException 
 * @author Zhou Fan
 * 
 */
public abstract class BaseRuntimeException extends RuntimeException implements ILocatable,
        ICodedException, IWithProcedureStackTrace {

    protected Location location;
    protected String sourceFile;
    protected String code;
    protected TraceElement traceElement;
    
    public BaseRuntimeException( String code, Object[] args, Throwable cause ){
        this(MessageFactory.getMessage(code, args), cause);
        setCode(code);
    }

    protected BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseRuntimeException(String message) {
        super(message);
    }

    protected BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(String code, Object[] args, Throwable cause,
            CompositeMap config) {
        this(MessageFactory.getMessage(code, args), cause);
        setCauseConfig(config);
        setCode(code);
    }

    public BaseRuntimeException(String code, Object[] args, Throwable cause,
            String source, Location location) {
        this(MessageFactory.getMessage(code, args), cause);
        setSource(source);
        setOriginLocation(location);
        setCode(code);
    }

    public BaseRuntimeException(String code, Object[] args, Throwable cause,
            ILocatable location) {
        this(code, args, cause, location==null?null:location.getOriginSource(), location==null?null:location
                .getOriginLocation());
    }

    public BaseRuntimeException(String code, Object[] args, ILocatable location) {
        this(code, args, null, location);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Location getOriginLocation() {
        return location;
    }

    public void setOriginLocation(Location location) {
        this.location = location;
    }

    public String getOriginSource() {
        return sourceFile;
    }

    public void setSource(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setCauseConfig(CompositeMap config) {
        location = config.getLocation();
        File file = config.getSourceFile();
        sourceFile = file == null ? null : file.getAbsolutePath();
    }

    public TraceElement getTraceElement() {
        return traceElement;
    }

    public void setTraceElement(TraceElement traceElement) {
        this.traceElement = traceElement;
    }    
    
    /**
     * Pass origin message to MessageFactory.getExceptionMessage()
     * to append more infomation
     */
    public String getMessage() {
        String msg = super.getMessage();
        return MessageFactory.getExceptionMessage(this, msg);
    }

}
