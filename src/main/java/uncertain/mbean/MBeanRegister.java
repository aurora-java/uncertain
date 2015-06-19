/*
 * Created on 2011-4-13 ����08:56:01
 * $Id$
 */
package uncertain.mbean;

import java.lang.reflect.Method;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

/*
 * JDK 1.5+: mbs = ManagementFactory.getPlatformMBeanServer();.
 to JDK 1.4:  mbs = MBeanServerFactory.createMBeanServer("SimpleAgent" );
 */
public class MBeanRegister {
    
    public static IMBeanRegister getInstance(){
        return RegisterJDK15.getInstance();
    }

    static Class[]         ARG_TYPES = {String.class, Object.class};
    static  Method         REGISTER_METHOD = null;
    static{
        int v = getJDKVersion();
        String cls = MBeanRegister.class.getPackage().getName() + ".RegisterJDK";
        if(v>4)
            cls += "15";
        else
            cls += "14";
        try{
            Class regcls = Class.forName(cls);
            REGISTER_METHOD = regcls.getMethod("register", ARG_TYPES);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static String getDefaultMBeanName( String domain, String type, String name ){
        return "org.uncertain."+domain+":type="+type+",name="+name;
    }
    
    public static String getDefaultMBeanName( String type, String name ){
        return "org.uncertain:type="+type+",name="+name;
    }

    public static void resiterMBean(String name, Object obj) {
        if(REGISTER_METHOD!=null){
            Object[] args = new Object[]{name, obj};
            try{
                REGISTER_METHOD.invoke(null,args);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    public static int getJDKVersion(){
        String s = System.getProperty("java.version");
        String[] array = s.split("\\.");
        if(array.length>1)
            return Integer.parseInt(array[1]);
        return 0;
    }


}
