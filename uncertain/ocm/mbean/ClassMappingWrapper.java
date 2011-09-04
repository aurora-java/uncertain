/*
 * Created on 2011-9-4 下午04:16:40
 * $Id$
 */
package uncertain.ocm.mbean;

import uncertain.ocm.ClassMapping;
import uncertain.util.resource.Location;

public class ClassMappingWrapper  implements ClassMappingWrapperMBean {
    
    ClassMapping        classMapping;

    /**
     * @param classMapping
     */
    public ClassMappingWrapper(ClassMapping classMapping) {
        super();
        this.classMapping = classMapping;
    }

    public String getClassName() {
        return classMapping.getClassName();
    }

    public String getOriginSource() {
        return classMapping.getOriginSource();
    }

    public Location getOriginLocation() {
        return classMapping.getOriginLocation();
    }
    

    public String getElementName() {
        return classMapping.getElementName();
    }

    public String getPackageName() {
        return classMapping.getPackageName();
    }
    

}
