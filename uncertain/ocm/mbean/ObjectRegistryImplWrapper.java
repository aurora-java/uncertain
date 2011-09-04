/*
 * Created on 2011-9-4 下午04:27:30
 * $Id$
 */
package uncertain.ocm.mbean;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;
import uncertain.ocm.ObjectRegistryImpl;

public class ObjectRegistryImplWrapper implements ObjectRegistryImplWrapperMBean, IMBeanRegistrable {
    
    ObjectRegistryImpl      objectRegistry;

    /**
     * @param objectRegistry
     */
    public ObjectRegistryImplWrapper(ObjectRegistryImpl objectRegistry) {
        this.objectRegistry = objectRegistry;
    }

    public String getConstructorName( String class_name )
        throws ClassNotFoundException
    {
        Class cls = Class.forName(class_name);
        Constructor cst = objectRegistry.getConstructor(cls);
        return cst==null?null:cst.toGenericString();
    }

    public boolean canCreateInstance( String class_name)
        throws ClassNotFoundException
    {
        Class cls = Class.forName(class_name);
        return objectRegistry.canCreateInstance(cls);
    }

    public int getInstanceMappingCount(){
        return objectRegistry.getInstanceMapping().size();
    }

    public void registerMBean(IMBeanRegister register,
            IMBeanNameProvider name_provider)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        String reg_name = name_provider.getMBeanName("BuiltinInstances",
        "instanceType=IObjectRegistry");
        register.register(reg_name, this);
        Map mappings = objectRegistry.getInstanceMapping();
        Iterator it = mappings.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Class cls = (Class)entry.getKey();
            Object value = entry.getValue();
            if(value==null)
                continue;
            String by_type = cls.isInterface()?"By_Interface":"By_Class";
            String n = name_provider.getMBeanName("BuiltinInstances",
                    "instanceType=IObjectRegistry,Array=InstanceMappings,RegistrationType="+by_type+",className="
                            + cls.getName());
            register.register(n, new InstanceMapping(cls,value));
        }
    }
}


