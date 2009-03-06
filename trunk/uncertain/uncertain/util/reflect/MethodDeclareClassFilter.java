/*
 * MethodDeclareClassFilter.java
 *
 * Created on 2002年1月8日, 下午2:55
 */

package uncertain.util.reflect;

import java.lang.reflect.*;

/**
 *
 * @author  Administrator
 * @version 
 */
public class MethodDeclareClassFilter  implements MethodFilter {
    
    Class declaring_class = null; 

    /** Creates new MethodDeclareClassFilter   */
    public MethodDeclareClassFilter() {
    }
    
    public MethodDeclareClassFilter( Class flt){
        this.declaring_class = flt;
    }
    
    public boolean accepts(Class owner, Method m) {
        if( declaring_class == null) return m.getDeclaringClass().equals(owner);
        else return m.getDeclaringClass().equals(this.declaring_class);
    }    
    

}
