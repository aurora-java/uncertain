/**
 * Created on: 2004-6-7 15:04:08
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.lang.reflect.Field;

/**
 * 
 */
public class FieldAccessor extends ObjectAccessor {
	
	Field	the_field;
	Class	type;

	/**
	 * Constructor for FieldAccessor.
	 */
	public FieldAccessor( String name, Field field) {
		super(name);
		this.the_field = field;
		the_field.setAccessible(true);
		type = the_field.getType();
//		if( type.isArray()) type = type.getComponentType();
	}
	
	public void writeToObject( Object obj, Object value) throws Exception {
		try{
			the_field.set(obj, value);
		}catch(IllegalArgumentException ex){
			Object new_value = oc_manager.getDataTypeHome().convert(value, the_field.getType());
			if( new_value != null) the_field.set(obj, new_value);
		}
			
	}
	
	public Object readFromObject( Object obj) throws IllegalAccessException{
		return the_field.get(obj);	
	}
	
	public boolean acceptContainer(){
		return false;
	}
	
	public Class getType(){
		return type;
	}	

}
