/**
 * Created on: 2004-6-9 20:59:45
 * Author:     zhoufan
 */

package uncertain.ocm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;


/**
 * Provides mapping rule for specific class: name of attribute, field or method
 * mapped to, etc
 */
public class MappingRule {
	
	Class				target_class;
	OCManager			oc_manager;

	// ObjectAccessor from attribute	
	ObjectAccessor[]	for_attrib;
	HashMap				for_attrib_map;
	
	// element_name -> ObjectAccessor 
	HashMap			    for_element_map;
	
	// all mapping fields
	//HashMap				all_mapping;

	/**
	 * Constructor for MappingRule.
	 */
	
	public MappingRule(Class _target_class, OCManager _oc_manager) {
		this.target_class = _target_class;
		this.oc_manager = _oc_manager;
		//all_mapping    = new HashMap();
		for_attrib_map = new HashMap();				
	}
	
	public ObjectAccessor wrapAccessor( ObjectAccessor oac){
		Class type = oac.getType();
		if( type.isArray())
			return new ArrayAccessor(oac);
		else if( CompositeMap.class.isAssignableFrom(type))
			return new ContainerAccessor(oac);
		else if( Collection.class.isAssignableFrom(type))
			return new CollectionAccessor(oac);
		else	
			return oac;
	}


	public void addMapping( String name, ObjectAccessor oa, boolean from_attribute){
		oa.setOCManager(oc_manager);
		if( oa instanceof CollectionMappable || oa instanceof ContainerAccessor || !from_attribute){
			if(for_element_map == null) for_element_map = new HashMap();
			for_element_map.put(name, oa);
		}
		else
			for_attrib_map.put(name, oa);	
	}

	public void addMapping( String name, Field field, boolean from_attribute){
		FieldAccessor fa = new FieldAccessor(name, field);
		ObjectAccessor oa = wrapAccessor(fa);
		addMapping(name, oa, from_attribute);
	}	
	
	public void addMapping(String name, Method set_method, Method get_method, boolean from_attribute){
		MethodAccessor ma = new MethodAccessor(name);
		ma.setMethodForGet(get_method);
		ma.setMethodForSet(set_method);
		ObjectAccessor oa = wrapAccessor(ma);
		addMapping(name, oa, from_attribute);
	}

	
	void getReady(){
		Collection entries = for_attrib_map.values();
		for_attrib = new ObjectAccessor[entries.size()];
		Object[] objs = entries.toArray();
		System.arraycopy(objs,0,for_attrib,0,objs.length);
	}
	
	public ObjectAccessor[] getAttribMapping(){
		return for_attrib;
	}
	
	public Map getElementMapping(){
		return for_element_map;
	}

	public Map getAttributeMapping(){
		return for_attrib_map;
	}
	
	public String toString(){
		return "\r\nMappingRule for "+target_class.getName()+"\r\n{\r\nattributes:"+for_attrib_map+"\r\nelements:"+for_element_map+" \r\n}\r\n";
	}

}
