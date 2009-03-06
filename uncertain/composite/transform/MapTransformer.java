/**
 * Created on: 2002-12-25 10:31:08
 * Author:     zhoufan
 */
package uncertain.composite.transform;

import java.util.Iterator;

import uncertain.composite.CompositeMap;

/** put a CompositeMap's child items into the Map
 *  <code>
 *     <transform class="org.lwap.composite.MapTransformer" key-field="key_field"/>
 *  </code> 
 */
public class MapTransformer implements CompositeTransformer {


	public static final String KEY_FIELD = "key-field";

	/**
	 * @see uncertain.composite.CompositeTransformer#transform(CompositeMap, CompositeMap)
	 */
	public CompositeMap transform(
		CompositeMap source,
		CompositeMap transform_config) {
		
		if( source == null || transform_config == null) return null;

		Iterator it = source.getChildIterator();
		if( it == null) return null;
		Object key_field_name = transform_config.get(KEY_FIELD);
		
		while( it.hasNext()){
			CompositeMap child = (CompositeMap)it.next();
			Object key_field_value = child.get(key_field_name);
			if( key_field_value != null)
				source.put(key_field_value.toString(), child);
		}
		
		source.getChilds().clear();
		return source;
		
	}

}
