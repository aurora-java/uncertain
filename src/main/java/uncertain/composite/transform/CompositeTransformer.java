/**
 * Created on: 2002-11-29 13:11:47
 * Author:     zhoufan
 */
package uncertain.composite.transform;

import uncertain.composite.CompositeMap;


/** Make some transform on a CompositeMap, such as group
 * 
 */
public interface CompositeTransformer {
	
	public static final String KEY_TRANSFORM = "transform";
	public static final String KEY_CLASS = "class";
	
	
	/**
	 * Make the transform
	 * @param source  source CompositeMap to transform
	 * @param transform_config configuration of transform 
	 * @return CompositeMap Transformed CompositeMap
	 */
	public CompositeMap transform( CompositeMap source, CompositeMap transform_config );

}
