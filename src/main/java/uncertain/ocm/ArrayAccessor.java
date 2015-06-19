/**
 * Created on: 2004-6-9 14:32:39
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ListIterator;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class ArrayAccessor extends CollectionMappable {
	
	Class		component_type;

	/**
	 * Constructor for CollectionAccessor.
	 * @param _field_name
	 */
	public ArrayAccessor( ObjectAccessor oa ) {
		super(oa);
		component_type = oa.getType().getComponentType();
	}
	
	public Class getType(){
		return component_type;
	}
	
	public Object getObjectToSet( List collection){
		ListIterator it = collection.listIterator();
		while(it.hasNext()){
			Object obj = it.next();
			if( !component_type.isAssignableFrom(obj.getClass()))
				it.remove();
		}
		Object new_array = Array.newInstance( component_type, collection.size());
		System.arraycopy(collection.toArray(),0,new_array,0,collection.size());
		return new_array;
	}
	
	public void populateContainer( CompositeMap container, Object value ){
		int length = Array.getLength(value);
		for(int i=0; i<length; i++){
			Object obj = Array.get(value,i);
			CompositeMap map = this.oc_manager.createContainer(obj);
			if( map!=null ) container.addChild(map);
		}
	}


}
