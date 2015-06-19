/*
 * Created on 2009-8-24
 */
package uncertain.schema;

public class InvalidQNameError extends SchemaError {

    public InvalidQNameError() {

    }

    public InvalidQNameError(String message) {
        super(message);
    }

    public InvalidQNameError(Throwable cause) {
        super(cause);
    }

    public InvalidQNameError(String message, Throwable cause) {
        super(message, cause);
    }

}
