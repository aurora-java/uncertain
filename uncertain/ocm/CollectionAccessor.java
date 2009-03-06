/**
 * Created on: 2004-6-10 14:50:12
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class CollectionAccessor extends CollectionMappable {

	/**
	 * Constructor for CollectionAccessor.
	 * @param oa ObjectAccessor
	 */
	public CollectionAccessor(ObjectAccessor oa) {
		super(oa);
	}

	/**
	 * @see uncertain.ocm.CollectionMappable#getObjectToSet(List)
	 */
	public Object getObjectToSet(List collection) {
		return collection;
	}

	/**
	 * @see uncertain.ocm.CollectionMappable#populateContainer(CompositeMap, Object)
	 */
	
	public void populateContainer(CompositeMap container, Object value) {
		Collection cl = (Collection)value;
		Iterator it = cl.iterator();
		while(it.hasNext()){
			Object child_obj = it.next();
			CompositeMap map = this.oc_manager.createContainer(child_obj);
			if( map != null) container.addChild(map);
		}
	}
	

}
