/*
 * Created on 2011-6-15 下午03:19:45
 * $Id$
 */
package uncertain.exception;


public class GeneralException extends RuntimeException implements ICodedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 929894246500504136L;
	private String code;
    public GeneralException(String code ){
    	this.code = code;
    }

    /**
     * @param message
     * @param cause
     */
    public GeneralException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * @param message
     */
    public GeneralException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * @param cause
     */
    public GeneralException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        if( code!=null )
            return code + ":"+super.getMessage();
        else
            return super.getMessage();
    }

}
