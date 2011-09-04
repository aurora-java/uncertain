/*
 * Created on 2011-9-1 下午09:47:03
 * $Id$
 */
package uncertain.mbean;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public interface IMBeanRegister {
    
    public  void register(String name, Object obj)
    throws MalformedObjectNameException,
    InstanceAlreadyExistsException, MBeanRegistrationException,
    NotCompliantMBeanException;    

}
