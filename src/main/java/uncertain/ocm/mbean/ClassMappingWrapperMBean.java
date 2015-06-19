/*
 * Created on 2011-9-1 下午09:46:22
 * $Id$
 */
package uncertain.ocm.mbean;

import uncertain.util.resource.ILocatable;

public interface ClassMappingWrapperMBean extends ILocatable {

    /**
     * @return Returns the className.
     */
    public String getClassName();

    /**
     * @return Returns the elementName.
     */
    public String getElementName();

    /**
     * @return Returns the packageName.
     */
    public String getPackageName();
    
    public String getOriginSource();    

}