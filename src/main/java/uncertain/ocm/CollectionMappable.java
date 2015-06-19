/**
 * Created on: 2004-6-9 23:23:59
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public abstract class CollectionMappable extends ObjectAccessor {
	
	ObjectAccessor		actual_accessor;

	/**
	 * Constructor for CollectionAccessor.
	 * @param oa
	 */
	public CollectionMappable(ObjectAccessor oa) {
		super(oa.getFieldName());
		this.actual_accessor = oa;
	}
	
	public abstract Object getObjectToSet( List collection);
	
	public abstract void populateContainer( CompositeMap container, Object value );
		

	/**
     * @see uncertain.ocm.ObjectAccessor#writeToObject(Object, Object)
     */
    public void writeToObject(Object obj, Object value)
    	throws Exception {
    	// here value will be CompositeMap
    	CompositeMap map = (CompositeMap) value;
    	Iterator it = map.getChildIterator();
    	if(it!=null){
    		//Class type = this.the_field.getType().getComponentType();
    		LinkedList lst = new LinkedList();
    		while(it.hasNext()){
    			CompositeMap element = (CompositeMap)it.next();
    			Object child_obj = this.oc_manager.createObject(element);
    			if( child_obj != null)
    				//if( type.isAssignableFrom( child_obj.getClass()))
    				lst.add(child_obj);
    		}
    		/*
    		  Object new_array = Array.newInstance( type, lst.size());
    		  System.arraycopy(lst.toArray(),0,new_array,0,lst.size());
    		  the_field.set(obj, new_array);
    		  */
    		actual_accessor.writeToObject(obj, getObjectToSet(lst));
    	}
    }

	/**
	 * @see uncertain.ocm.ObjectAccessor#readFromObject(Object)
	 */
	
	public Object readFromObject(Object obj) throws Exception {

	    Object value = actual_accessor.readFromObject(obj);
		CompositeMap container = new CompositeMap(10);
		container.setName(getFieldName());
		populateContainer(container, value);
		return container;
	}
	

	/**
	 * @see uncertain.ocm.ObjectAccessor#acceptContainer()
	 */
	public boolean acceptContainer() {
		return true;
	}
	
	public Class getType(){
		return actual_accessor.getType();
	}

}