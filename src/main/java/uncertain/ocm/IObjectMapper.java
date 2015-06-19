/**
 * Created on: 2004-5-18 14:28:17
 * Author:     zhoufan
 */
package uncertain.ocm;

import uncertain.composite.CompositeMap;

/**
 * Interface that defines basic method set for mapping pure data container to object
 * Currently only ReflectionMapper implemented
 */
public interface IObjectMapper {
	
	public void toObject( CompositeMap map, Object obj);
	
	public void toContainer( Object obj, CompositeMap map);

}
