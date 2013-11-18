/*
 * Created on 2011-4-13 ����08:56:52
 * $Id$
 */
package uncertain.mbean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class RegisterJDK15 {

    public static class Instance implements IMBeanRegister {

        public void register(String name, Object obj)
                throws MalformedObjectNameException,
                InstanceAlreadyExistsException, MBeanRegistrationException,
                NotCompliantMBeanException {
            RegisterJDK15.register(name, obj);
        }
    }
    
    static final IMBeanRegister DEFAULT_INSTANCE = new  Instance();
    
    public static IMBeanRegister getInstance(){
        return DEFAULT_INSTANCE;
    }

    public static void register(String name, Object obj)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName on = null;
        try {
            on = new ObjectName(name);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid object name:" + name, ex);
        }
        if(mbs.isRegistered(on)){
			try {
				mbs.unregisterMBean(on);
			} catch (InstanceNotFoundException e) {
				throw new RuntimeException(e);
			}
        }
        mbs.registerMBean(obj, on);
    }

}
