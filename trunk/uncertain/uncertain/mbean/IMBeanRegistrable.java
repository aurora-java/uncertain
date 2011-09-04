/*
 * Created on 2011-9-1 下午09:57:57
 * $Id$
 */
package uncertain.mbean;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

/**
 * implement this interface to get called by UncertainEngine to register into
 * JMX
 */
public interface IMBeanRegistrable {

    public void registerMBean(IMBeanRegister register,
            IMBeanNameProvider name_provider)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException;

}
