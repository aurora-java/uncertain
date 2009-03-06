/*
 * Created on 2005-7-17
 */
package uncertain.ocm;

import java.lang.reflect.InvocationTargetException;

/**
 * IObjectCreator
 * @author Zhou Fan
 * 
 */
public interface IObjectCreator {
    
    public Object createInstance(Class cls)  throws Exception;

}
