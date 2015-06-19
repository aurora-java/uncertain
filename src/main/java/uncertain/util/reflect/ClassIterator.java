/*
 * ClassIterator.java
 *
 * Created on 2002年1月8日, 下午1:16
 */

package uncertain.util.reflect;

import java.lang.reflect.*;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ClassIterator {
/*    
    public static final Integer IT_ACCEPT_METHOD     = new Integer(0);
    public static final Integer IT_ACCEPT_INTERFACE = new Integer(1);
    public static final Integer IT_ACCEPT_METHOD = new Integer(0);
*/
    

    /** Creates new ClassIterator */
    public ClassIterator() {
    }
    
    public void iterate( Class cls, ClassIterateHandle handle) throws SecurityException {
        int n;
        handle.beginIterate(cls);
        
        Method[] methods = cls.getMethods();
        for(n=0 ; n<methods.length; n++) handle.onMethod(cls, methods[n]);
        
        Class[] itfcs = cls.getInterfaces();
        for(n=0; n<itfcs.length; n++) handle.onInterface(cls, itfcs[n]);
        
        Constructor[] cts = cls.getConstructors();
        for(n=0; n<cts.length; n++) handle.onConstructor(cls, cts[n]);
        
        Field[] flds = cls.getFields();
        for(n=0; n<flds.length; n++) handle.onField(cls,flds[n]);
        
        handle.endIterate(cls);
    }

}
