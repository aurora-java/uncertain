/**
 * Created on: 2004-6-7 15:01:26
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;

/**
 * The default OCMapper implementation that perform O/C mapping by reflection
 */
public class ReflectionMapper implements IObjectMapper {	

	OCManager		oc_manager;
	ClassAnalyzer   class_analyzer;
	
	// Class -> MappingRule
	HashMap			rule_map;
	
	/**
	 * Constructor for DefaultMapper.
	 */
	public ReflectionMapper( OCManager _oc_manager) {
		this.oc_manager = _oc_manager;
		rule_map = new HashMap(500);
		class_analyzer = new ClassAnalyzer(_oc_manager);
	}
	
	public MappingRule getMappingRule(Class type){
		MappingRule rule = (MappingRule)rule_map.get(type);
		if( rule == null) rule = createMappingRule(type);
		return rule;
	}
	
	public void setMappingRule(Class type, MappingRule rule){
		rule_map.put(type, rule);
	}
	
	public synchronized MappingRule createMappingRule( Class type){
		MappingRule rule = class_analyzer.analyze(type);
		rule_map.put(type,rule);
		if( oc_manager.isEventEnable())
			oc_manager.fireEvent(OCMEventFactory.newMappingRuleLoadedEvent(this, rule));
		return rule;
	}
	
	public void toObject(CompositeMap map, Object target){
	    toObject(map, target, oc_manager);
	}

	/**
	 * @see uncertain.ocm.IObjectMapper#toObject(CompositeMap, Object)
	 */
	public void toObject(CompositeMap map, Object target, IMappingHandle handle) {
		MappingRule rule = getMappingRule(target.getClass());
		if(rule == null) return;
		ObjectAccessor[] for_attrib = rule.getAttribMapping();
		
		
		// First, populate all fields that come from attribute
		for(int i=0; i<for_attrib.length; i++){
			ObjectAccessor oa = for_attrib[i];
			try{                
			    Object value = map.get(oa.getFieldName());
                if(value!=null)
				    oa.writeToObject(target, value);
			}catch(Exception ex){
			    oc_manager.handleException("error when populate field "+oa.getFieldName(),ex);
			}
		}
		
		Map for_element = rule.getElementMapping();
		if( for_element == null) return;
		
		// Then add elements
		boolean acceptUnknownContainer = handle.acceptUnknownContainer();
		Iterator it = map.getChildIterator();
		if( it != null)
		while( it.hasNext()){
			CompositeMap element = (CompositeMap)it.next();
			String name = element.getName();
			if( name == null) continue;
			name = NamingUtil.toIdentifier(name);
			// if there exists ObjectAccessor for this element's name
			ObjectAccessor oa = (ObjectAccessor)for_element.get(name);
			if( oa != null) {
				try{
					// if accept collection, direct pass child CompositeMap
					if( oa.acceptContainer()){
						oa.writeToObject(target, element);
					// else create new object from this CompositeMap	
					}else{
						Object child_obj = oc_manager.createObject(element,handle);
						if( child_obj != null){
							oa.writeToObject(target, child_obj);
						}	
					}
				}catch(Exception ex){
				    oc_manager.handleException("error when populate field "+oa.getFieldName(),ex);
				}			
			}
			// else check if this object is instanceof IChildContainerAcceptable
			// if so, add child node to this object
			else if( target instanceof IChildContainerAcceptable){
			    ((IChildContainerAcceptable)target).addChild(element);
			}
			else if(acceptUnknownContainer)
			    handle.getUnknownContainer(element);
		}
	}

	/**
	 * @see uncertain.ocm.IObjectMapper#toMap(Object, CompositeMap)
	 */
	public void toContainer(Object target, CompositeMap map) {
        MappingRule rule = getMappingRule(target.getClass());        
        if(rule == null) return;
        ObjectAccessor[] for_attrib = rule.getAttribMapping();
        // First, load all fields that come from attribute
        for(int i=0; i<for_attrib.length; i++){
            ObjectAccessor oa = for_attrib[i];
            try{
                Object value = oa.readFromObject(target);
                map.put(oa.getMappedName(), value);
            }catch(Exception ex){
                oc_manager.handleException("error when reading field "+oa.getFieldName(),ex);                
            }
        }
        // Then load elements
        
        // To be appended        
		
	}

}
