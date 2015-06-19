/*
 * MethodAcceptor.java
 *
 * Created on 2002年1月8日, 下午2:24
 */

package uncertain.util.reflect;
import java.lang.reflect.Method;
/**
 *
 * @author  Administrator
 * @version 
 */
public interface MethodFilter {
    
    public boolean accepts( Class owner, Method m);
    
}

