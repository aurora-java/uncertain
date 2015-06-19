/*
 * Created on 2011-9-4 下午04:24:30
 * $Id$
 */
package uncertain.ocm.mbean;

import uncertain.ocm.PackageMapping;

public class PackageMappingWrapper implements PackageMappingWrapperMBean {
    
    PackageMapping      packageMapping;

    /**
     * @param packageMapping
     */
    public PackageMappingWrapper(PackageMapping packageMapping) {
        this.packageMapping = packageMapping;
    }

    public String getNameSpace() {
        return packageMapping.getNameSpace();
    }

    public String getPackageName() {
        return packageMapping.getPackageName();
    }

    public String getOriginSource() {
        return packageMapping.getOriginSource();
    }

}
