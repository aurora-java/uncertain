/*
 * EmptyMapException.java
 *
 * Created on 2002��10��29��, ����5:06
 */

package uncertain.composite;

/**
 *
 * @author  zhoufan
 */
public class EmptyMapException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>EmptyMapException</code> without detail message.
     */
    public EmptyMapException() {
    }
    
    
    /**
     * Constructs an instance of <code>EmptyMapException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EmptyMapException(String msg) {
        super(msg);
    }
}
