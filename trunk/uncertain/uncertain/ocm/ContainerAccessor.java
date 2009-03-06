/**
 * Created on: 2004-6-11 11:21:59
 * Author:     zhoufan
 */
package uncertain.ocm;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class ContainerAccessor extends ObjectAccessor {
	
	ObjectAccessor		actual_accessor;	

	/**
	 * Constructor for ContainerAccessor.
	 * @param _field_name
	 */
	public ContainerAccessor(ObjectAccessor oa) {
		super(oa.getFieldName());
		this.actual_accessor = oa;
	}

	/**
	 * @see uncertain.ocm.ObjectAccessor#writeToObject(Object, Object)
	 */
	public void writeToObject(Object obj, Object value)
		throws Exception {
			CompositeMap m = (CompositeMap) value;
			actual_accessor.writeToObject(obj, m);
	}

	/**
	 * @see uncertain.ocm.ObjectAccessor#readFromObject(Object)
	 */
	public Object readFromObject(Object obj) throws Exception {
		return (CompositeMap)actual_accessor.readFromObject(obj);
	}

	/**
	 * @see uncertain.ocm.ObjectAccessor#acceptContainer()
	 */
	public boolean acceptContainer() {
		return true;
	}

	/**
	 * @see uncertain.ocm.ObjectAccessor#getType()
	 */
	public Class getType() {
		return CompositeMap.class;
	}

}
