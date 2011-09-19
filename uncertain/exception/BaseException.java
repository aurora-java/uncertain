/*
 * Created on 2011-6-25
 */
package uncertain.exception;

import java.io.File;

import uncertain.composite.CompositeMap;
import uncertain.proc.trace.IWithProcedureStackTrace;
import uncertain.proc.trace.TraceElement;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * This abstract exception implements ILocatable, ICodedException and can be
 * used as base class of user defined exception under uncertain framework
 * 
 * @author Zhou Fan
 * 
 */
public abstract class BaseException extends Exception implements ILocatable,
        ICodedException, IWithProcedureStackTrace {

    protected Location location;
    protected String sourceFile;
    protected String code;
    protected TraceElement traceElement;

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(Throwable cause) {
        super(cause);
    }
    
    public BaseException( String code, Object[] args, Throwable cause ){
        this(MessageFactory.getMessage(code, args), cause);
        setCode(code);
    }

    public BaseException(String code, Object[] args, Throwable cause,
            CompositeMap config) {
        this(MessageFactory.getMessage(code, args), cause);
        setCauseConfig(config);
        setCode(code);
    }

    public BaseException(String code, Object[] args, Throwable cause,
            String source, Location location) {
        this(MessageFactory.getMessage(code, args), cause);
        setSource(source);
        setOriginLocation(location);
        setCode(code);
    }

    public BaseException(String code, Object[] args, Throwable cause,
            ILocatable location) {
        this(code, args, cause, location.getOriginSource(), location
                .getOriginLocation());
    }

    public BaseException(String code, Object[] args, ILocatable location) {
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
    
    /**
     * Pass origin message to MessageFactory.getExceptionMessage()
     * to append more infomation
     */
    public String getMessage() {
        String msg = super.getMessage();
        return MessageFactory.getExceptionMessage(this, msg);
    }

    public TraceElement getTraceElement() {
        return traceElement;
    }

    public void setTraceElement(TraceElement traceElement) {
        this.traceElement = traceElement;
    }

}
