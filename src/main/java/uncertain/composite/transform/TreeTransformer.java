/**
 * Created on: 2002-11-29 14:09:48
 * Author:     zhoufan
 */
package uncertain.composite.transform;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;

/** Transform CompositeMap to tree structure
 * 
 * 	<code>
 *     	<transform class="org.lwap.composite.TreeTransformer" id-field="UNIT_ID" parent-field="PARENT_ID"/>
 *  </code>
 * 
 */
public class TreeTransformer implements CompositeTransformer {

	public static final String KEY_ID_FIELD = "id-field";
	public static final String KEY_PARENT_FIELD = "parent-field";
//	public static final String KEY_ROOT_ID_VALUE = "root-id-value";
	
	public static CompositeMap createTreeTransform(String id_field, String parent_id_field){
		CompositeMap config = Transformer.createTransformConfig(TreeTransformer.class.getName());
		config.put(KEY_ID_FIELD, id_field);
		config.put(KEY_PARENT_FIELD, parent_id_field );
		return config;
	}
	
	public CompositeMap transform(
		CompositeMap source,
		CompositeMap transform_config) {

		if( source == null || transform_config == null) return null;
		
		Object id_field = transform_config.get(KEY_ID_FIELD);
		if(id_field==null) throw new IllegalArgumentException("TreeTransformer:id-field must be set");
		Object parent_field = transform_config.get(KEY_PARENT_FIELD);
		if(parent_field==null) throw new IllegalArgumentException("TreeTransformer:parent-field must be set");
		

		Iterator childs = source.getChildIterator();
		if( childs == null) return source;
		
		Map 		all_items = new LinkedHashMap((int)(source.getChilds().size() / 0.75));
		
		while( childs.hasNext()){
			CompositeMap item = (CompositeMap)childs.next();
			item.setParent(null);
			Object id_value = item.get(id_field);
			/*
            if(id_value==null){                
                System.out.println("[TreeTransformer] warning:can't get id-field in record");
                System.out.println(item.toXML());
            }
            */
			all_items.put(id_value,item);
		}
		
		
		childs = source.getChildIterator();
		while( childs.hasNext()){
			CompositeMap item = (CompositeMap)childs.next();
			Object parent_id_value = item.get(parent_field);
			CompositeMap parent = (CompositeMap)all_items.get(parent_id_value);
			if( parent != null) parent.addChild(item);
		}
		
		source.getChilds().clear();
		Iterator items = all_items.values().iterator();
		while( items.hasNext()){
			CompositeMap item = (CompositeMap) items.next();
			if( item.getParent() == null) source.addChild(item);
			//else System.out.println("child item:"+item.get(id_field)+" parent:"+item.getName());
		}
		//System.out.println("after transform:"+source.toXML());
		return source;
	}

}
