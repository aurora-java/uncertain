/**
 * Created on: 2002-11-29 13:17:16
 * Author:     zhoufan
 */
package uncertain.composite.transform;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;


/**
 *  Group child CompositeMap on specified field
 *   <code>
 *     	<transform class="org.lwap.composite.GroupTransformer" group-name="modular" group-field="MODULAR_ID">
 *   			<group-field name="MODULAR_NAME" remove-field="true"/>
 *   			<group-field name="MODULAR_ID" remove-field="true" />
 *   		</transform>
 *   </code>
 */
public class GroupTransformer implements CompositeTransformer {
	
	static GroupTransformer default_instance = new GroupTransformer();
	
	public static final String KEY_GROUP_NAME = "group-name";
	public static final String KEY_GROUP_FIELD_NAME = "name";
	public static final String KEY_REMOVE_FIELD = "remove-field";
	public static final String KEY_GROUP_FIELD =	"group-field";	
	
	public static GroupTransformer getInstance(){
		return default_instance;
	}
	
	public static CompositeMap createGroupTransform(String group_field,String group_name){
		CompositeMap config = Transformer.createTransformConfig(GroupTransformer.class.getName());
		config.setName(CompositeTransformer.KEY_TRANSFORM);
		config.put(KEY_GROUP_NAME, group_name);
		config.put(KEY_GROUP_FIELD, group_field);
		return config;
	}
	
	public static CompositeMap addGroupField(CompositeMap config, String field_name, boolean remove){
		CompositeMap field = config.createChild(KEY_GROUP_FIELD);
		field.put(KEY_GROUP_FIELD_NAME, field_name);
		field.putBoolean(KEY_REMOVE_FIELD,remove);
		return field;
	}
	
	public CompositeMap transform( CompositeMap source, CompositeMap transform_config ){		
		
		if( source == null || transform_config == null) return null;
		boolean has_group_fields = transform_config.getChilds() != null;

		String group_field = transform_config.getString(KEY_GROUP_FIELD);
		if(group_field == null) return source;

		Iterator childs = source.getChildIterator();
		if( childs == null) return source;		
		
		String group_name = transform_config.getString(KEY_GROUP_NAME, "group");
		// group-field-value -> group items
		Map	groups = new LinkedHashMap ();
		
		while( childs.hasNext()){
			CompositeMap item = (CompositeMap)childs.next();
			Object value = item.get(group_field);
			CompositeMap group_item = (CompositeMap)groups.get(value);
			if(group_item == null){
				group_item = new CompositeMap(group_name);				
				// put group fields into group_item
				if(has_group_fields){
					Iterator gfields = transform_config.getChildIterator();
					while(gfields.hasNext()){
						CompositeMap gf = (CompositeMap)gfields.next();
						Object gf_name = gf.get(KEY_GROUP_FIELD_NAME);
						group_item.put(gf_name, item.get(gf_name));						
					}
				}
				groups.put(value,group_item); 
			}
			group_item.addChild(item);
		}
		
		source.getChilds().clear();
		source.addChilds(groups.values());
		
		return source;
	}


}
