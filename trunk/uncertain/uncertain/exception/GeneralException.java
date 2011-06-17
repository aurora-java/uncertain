/*
 * Created on 2011-6-15 下午03:19:45
 * $Id$
 */
package uncertain.exception;

import java.util.Locale;

public class GeneralException extends RuntimeException implements ICodedException {
 
    /**
     * 
     */
    public GeneralException() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public GeneralException(String code ){

    }

    /**
     * @param message
     * @param cause
     */
    public GeneralException(String code, String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public GeneralException(String code, String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public GeneralException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public String getCode() {
        // TODO Auto-generated method stub
        return null;
    }

}
