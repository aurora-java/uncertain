/*
 * Created on 2011-9-4 下午04:37:51
 * $Id$
 */
package uncertain.ocm.mbean;

public class InstanceMapping implements InstanceMappingMBean {

    Class type;
    Class instanceType;
    String instanceName;
    
    public InstanceMapping( Class type, Object instance ){
        this.type = type;
        this.instanceType = instance.getClass();
        this.instanceName = instance.toString();
    }

    /* (non-Javadoc)
     * @see uncertain.ocm.mbean.InstanceMappingMBean#getType()
     */
    public String getType() {
        return type.getName();
    }

    /* (non-Javadoc)
     * @see uncertain.ocm.mbean.InstanceMappingMBean#getInstanceType()
     */
    public String getInstanceType() {
        return instanceType.getName();
    }

    /* (non-Javadoc)
     * @see uncertain.ocm.mbean.InstanceMappingMBean#getInstanceName()
     */
    public String getInstanceName() {
        return instanceName;
    }
}
