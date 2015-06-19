/*
 * IterationHandle.java
 *
 * Created on 2002年1月9日, 下午5:25
 */

package uncertain.composite;

/**
 *
 * @author  Administrator
 * @version 
 */
public interface IterationHandle {
    
    public static final int IT_CONTINUE = 0;
    public static final int IT_NOCHILD = 1;
    public static final int IT_BREAK = 2;
    public static final int IT_REMOVE = -1;
    
    public int process( CompositeMap map);

}

