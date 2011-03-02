/**
 * Created on: 2002-11-13 13:36:46
 * Author:     zhoufan
 */
package uncertain.composite;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Base class for Dynamic member capable classes 
 * 
 */
public class DynamicObject {
	
	/**
	 * cast from a Map instance to a DynamicObject instance
	 * @param context	  A Map instance that serves for new DynamicObject's context
	 * @param inst_class Class of instance to create
	 */	
	public static Object cast( CompositeMap context,  Class inst_class)
	{
		if( context == null) return null;	
        try{
            return ((DynamicObject)inst_class.newInstance()).initialize(context);
        }catch(Exception ex){
            throw new RuntimeException("Can't create instance of "+inst_class.getName(), ex);
        }
	}
	
    /**
     * Cast a collection of CompositeMap to specified class that is sub class of DynamicObject
     * @param records a collection of CompositeMap
     * @param inst_class Desired target class to cast
     * @return casted array of specified type
     */
    public static DynamicObject[] castToArray( Collection records, Class inst_class){
        if( inst_class.isAssignableFrom(DynamicObject.class))
            throw new IllegalArgumentException("class " + inst_class.getName() + " is not sub class of DynamicObject");
        try{
            DynamicObject[] array = (DynamicObject[])Array.newInstance(inst_class, records.size());
            int id = 0;
            Iterator it = records.iterator();
            while(it.hasNext()){
                CompositeMap record = (CompositeMap)it.next();
                DynamicObject obj = (DynamicObject)inst_class.newInstance();
                obj.initialize(record);
                array[id++] = obj;
            }
            return array;
        }catch(Exception ex){
            throw new RuntimeException("Can't create array instance of "+inst_class.getName(), ex);
        }
    }

	protected CompositeMap object_context;
	
	public DynamicObject initialize( CompositeMap context) {
		object_context = context;
		return this;
	}
	
	public DynamicObject initialize(){
		object_context = new CompositeMap(30);
		return this;
	}	
	
	public CompositeMap getObjectContext(){
		return object_context;
	}
	
	public Object castTo( Class new_class){
		return cast( getObjectContext(), new_class);
	}


/*  ----------------- get/set property of specified type ---------------------------*/
    
    public Object get( Object key ){
        if(object_context==null) throw new IllegalStateException("Object context not initialized");
        return object_context.get(key);
    }
    
    public void put( Object key, Object value ){
        object_context.put(key, value);
    }


	public String getString( String key){
		return getObjectContext().getString(key);
	}
	
	public String getString( String key, String default_value){
		return getObjectContext().getString(key,default_value);
	}
	
	public Boolean getBoolean( String key){
		return getObjectContext().getBoolean(key);
	}
	
	public boolean getBoolean( String key, boolean default_value){
		return getObjectContext().getBoolean(key,default_value);
	}
	
	
	public Integer getInteger( String key){
		return getObjectContext().getInt(key);
	}
	
	public int getInt( String key, int default_value){
		return getObjectContext().getInt(key,default_value);
	}

	public Long getLong( String key){
		return getObjectContext().getLong(key);
	}
	
	public long getLong( String key, int default_value){
		return getObjectContext().getLong(key,default_value);
	}
	
	
	public void putString( String key, String value){
		getObjectContext().put(key,value);
	}
	
	public void putInt( String key, int value){
		getObjectContext().put(key,new Integer(value));
	}
	
	public void putLong( String key, long value){
		getObjectContext().put(key,new Long(value));
	}
	
	public void putBoolean( String key, boolean value){
		getObjectContext().put(key, Boolean.valueOf(value));
	}
    
    /**
     * Check validation of configure
     */
    public void checkValidation(){
        
    }
    
    
    public List getChilds( String name ){
        CompositeMap child = object_context.getChild(name);
        if(child==null) return null;
        return child.getChilds();
    }
    
    /**
     * Find a direct child with specified name. If not found, create a new one
     * *** NOTE *** name_space will be ignored when finding child. This parameter 
     * is for setting new created child's namespace
     * @param name_space
     * @param name
     * @return
     */
    public CompositeMap getChildNotNull(String name_space, String name){
        CompositeMap child = object_context.getChild(name);
        if(child==null){
            child = object_context.createChild(name);
            child.setNameSpace(null, name_space);          
        }
        return child;
    }
	
	
}
