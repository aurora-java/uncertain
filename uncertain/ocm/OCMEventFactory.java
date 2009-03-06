/**
 * Created on: 2004-9-9 21:45:24
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.util.logging.Level;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class OCMEventFactory {    
/*	
	public static final int  EVT_OBJECT_MAPPED = 10;	
	public static final int  EVT_OBJECT_CREATED = 11;
	public static final int  EVT_MAPPING_RULE_LOADED = 12;
	public static final int	EVT_NAMESPACE_UNKNOWN = 1000;
	public static final int	EVT_OBJECT_CREATION_FAIL = 1001;
*/	
	
	public static final String OBJECT_MAPPED = "OBJECT_MAPPED";
	public static final String OBJECT_CREATED = "OBJECT_CREATED";
	public static final String MAPPING_RULE_LOADED = "MAPPING_RULE_LOADED";	
	public static final String NAMESPACE_UNKNOWN = "NAMESPACE_UNKNOWN";
	public static final String OBJECT_CREATION_FAIL = "OBJECT_CREATION_FAIL";
	public static final String CANNOT_MAP_CLASS = "CANNOT_MAP_CLASS";
	public static final String CLASS_NOT_FOUND = "CLASS_NOT_FOUND";
		
	public static OCMEvent newNamespaceUnknownEvent(Object sender, String namespace){
		OCMEvent event = new OCMEvent( NAMESPACE_UNKNOWN, sender, namespace);
		event.level = Level.WARNING.intValue();
		return event;
	}
	
	public static OCMEvent newObjectCreationFailEvent(Object sender, CompositeMap container){
		OCMEvent event = new OCMEvent(OBJECT_CREATION_FAIL, sender, container);
		event.level = Level.SEVERE.intValue();
		return event;	
	}
	
	public static OCMEvent newObjectMappedEvent(Object sender, Object mappedObject){
		OCMEvent event = new OCMEvent(OBJECT_MAPPED, sender, mappedObject);
		event.level = Level.FINER.intValue();
		return event;	
	}
	
	public static OCMEvent newObjectCreatedEvent(Object sender, Object obj){
		OCMEvent event = new OCMEvent(OBJECT_CREATED, sender, obj);
		event.level = Level.FINER.intValue();
		return event;	
	}
	
	public static OCMEvent newMappingRuleLoadedEvent(Object sender, MappingRule rule){
		OCMEvent event = new OCMEvent(MAPPING_RULE_LOADED, sender, rule);
		return event;	
	}	
	
	public static OCMEvent newCannotMapClassEvent(Object sender, CompositeMap source){
		OCMEvent event = new OCMEvent(CANNOT_MAP_CLASS, sender, source);
		event.level = Level.SEVERE.intValue();
		return event;	    
	}
	
	public static OCMEvent newClassNotFoundEvent(Object sender, String class_name){
	    OCMEvent event = new OCMEvent(CLASS_NOT_FOUND, sender, class_name);
	    event.level = Level.SEVERE.intValue();
	    return event;
	}
	
}
