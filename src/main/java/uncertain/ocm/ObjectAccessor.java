/**
 * Created on: 2004-6-7 18:57:37
 * Author:     zhoufan
 */
package uncertain.ocm;

/**
 *  abstract class that can perform O/C mapping
 *  for a certain attribute
 */
public abstract class ObjectAccessor {

	String 			field_name;
    String          mapped_name;
	OCManager		oc_manager;
	
	public ObjectAccessor( String _field_name ){
		this.field_name = _field_name;
        this.mapped_name = _field_name;
	}

    public ObjectAccessor( String _field_name, String _mapped_name ){
        this.field_name = _field_name;
        this.mapped_name = _mapped_name;
    }
    
    
	public void setOCManager(OCManager _oc_manager){
		this.oc_manager = _oc_manager;
	}
	
	public String getFieldName(){
		return field_name;
	}
    
    public String getMappedName(){
        return mapped_name;
    }
	
	public String toString(){
		return "[name:"+getFieldName()+" type:"+getType().getName()+" implementation class:"+getClass().getName()+']';
	}
	
	public abstract void writeToObject( Object obj, Object value) throws Exception;
	
	public abstract Object readFromObject( Object obj) throws Exception;
	
	public abstract boolean acceptContainer();
	
	public abstract Class getType();
}
