/**
 * Created on: 2004-9-10 18:21:36
 * Author:     zhoufan
 */
package uncertain.ocm;

import uncertain.composite.CompositeMap;

/**
 * Implement this interface to get aware of configuration process
 */
public interface IConfigurable extends IConfigureListener {
	
    /**
     * This method is called just after instance is created, before populating any
     * field from container to object
     * @param config the data container that this instance is crreated from 
     */
	public void beginConfigure(CompositeMap config);
	

}
