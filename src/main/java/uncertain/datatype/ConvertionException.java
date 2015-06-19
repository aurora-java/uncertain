/*
 * Created on 2006-11-25
 */
package uncertain.datatype;

/**
 * An exception that is thrown when a DataType instance can't convert a object
 * to desired type 
 * @author Zhou Fan
 *
 */
public class ConvertionException extends Exception {

    public ConvertionException() {
    }

    public ConvertionException(String message) {
        super(message);
    }

    public ConvertionException(Throwable cause) {
        super(cause);
    }

    public ConvertionException(String message, Throwable cause) {
        super(message, cause);
    }

}
