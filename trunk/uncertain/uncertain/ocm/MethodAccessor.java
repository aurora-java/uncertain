/**
 * Created on: 2004-6-7 15:10:11
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.lang.reflect.Method;

import uncertain.datatype.DataTypeRegistry;

/**
 * extends ObjectAccessor
 */
public class MethodAccessor  extends ObjectAccessor {
	
	Method		get_method;
	Method		set_method;	
	Class		data_type;

	/**
	 * Constructor for MethodAccessor.
	 */
	public MethodAccessor(String name) {
		super(name);
	}
	
	public void setMethodForSet( Method m){
		this.set_method = m;
		Class param_type = m.getParameterTypes()[0];
		if( data_type == null)
			data_type = param_type;
		else
			if( !data_type.equals(param_type)) 
				throw new IllegalArgumentException("parameter type no equal with get method's return type");
	}

	public void setMethodForGet( Method m){
		if( m == null) return;
		this.get_method = m;
		Class param_type = m.getReturnType();
		if( data_type == null)
			data_type = param_type;
		else
			if( !data_type.equals(param_type)) 
				throw new IllegalArgumentException("return type no equal with set method's argument type");
				
	}
	
	public void writeToObject( Object obj, Object value) throws Exception {
		if( set_method == null) return;
		Object[]	param = new Object[1];
		if(value !=null){
			if( data_type.isAssignableFrom(value.getClass()) )
				param[0] = value;
			else{
				 param[0] = oc_manager.getDataTypeHome().convert(value, data_type);
                 if( param[0]==null ) 
                     throw new IllegalArgumentException("Error when setting field value "+value+" for field " + this.getFieldName() + " in instance "+ obj.getClass().getName()+"["+obj+"]: Can't convert data type from "+value.getClass()+" to "+data_type.getName());
                }
            set_method.invoke(obj, param);
        }		
	}
	
	public Object readFromObject( Object obj) throws Exception {
		if( get_method == null) return null;
		return get_method.invoke(obj, null);
	}	
	
	public boolean acceptContainer(){
		return false;
	}
	
	public Class getType(){
		return this.data_type;
	}		

}
