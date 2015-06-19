/*
 * Created on 2011-9-1 下午04:57:04
 * $Id$
 */
package uncertain.mbean;

public interface IMBeanNameProvider {
    
    /** return MBean name under specified category */
    public String getMBeanName(String category, String sub_name);

}
