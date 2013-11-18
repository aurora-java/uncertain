/*
 * Created on 2011-4-13 ����08:56:52
 * $Id$
 */
package uncertain.mbean;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class RegisterJDK14 {

    public static void register(String name, Object obj)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        MBeanServer mbs = MBeanServerFactory.createMBeanServer("SimpleAgent" );
        //MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName on = new ObjectName(name);
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
