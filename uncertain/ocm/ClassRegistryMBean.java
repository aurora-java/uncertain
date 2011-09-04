/*
 * Created on 2011-9-1 下午05:03:14
 * $Id$
 */
package uncertain.ocm;

import java.util.List;
import java.util.Map;

public interface ClassRegistryMBean {

    public List getFeatures(String namespace, String element_name);

    public Map getFeatureMap();

    public String toString();
    
    public int getPackageMappingCount();
    
    public int getClassMappingCount();
    
    public int getFeatureAttachCount();    

}