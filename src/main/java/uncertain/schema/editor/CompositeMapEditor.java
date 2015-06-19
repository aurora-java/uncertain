/*
 * Created on 2009-7-20
 */
package uncertain.schema.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;

public class CompositeMapEditor {

	CompositeMap mData;
	Element mElement;
	ISchemaManager mSchemaManager;
	List mAttribs;

	/**
	 * @param data
	 * @param schemaManager
	 */
	public CompositeMapEditor(ISchemaManager schemaManager, CompositeMap data) {
		this.mData = data;
		this.mSchemaManager = schemaManager;
		mElement = mSchemaManager.getElement(mData.getQName());
		if (mElement != null)
			mAttribs = mElement.getAllAttributes();
	}

	/**
	 * Get all attributes for edit. If there exists schema for contained
	 * CompositeMap, All attributes defined by schema with corresponding value
	 * will be returned; otherwise, an array with all attributes in data will be
	 * returned.
	 * 
	 * @return
	 */
	public AttributeValue[] getAttributeList() {
		Set attrName = new HashSet();
		int total = mData.entrySet().size()+(mAttribs==null?0:mAttribs.size());
		AttributeValue[] values = new AttributeValue[total];
		int i = 0;
		if (mElement != null) {
			Collections.sort(mAttribs);

			for (Iterator it = mAttribs.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				AttributeValue av = new AttributeValue(mData, attrib, mData
						.get(attrib.getLocalName()));
				values[i++] = av;
				attrName.add(attrib.getLocalName());
			}
		}
		Iterator it = mData.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = entry.getKey() == null ? null : entry.getKey()
					.toString();
			if (key == null)
				continue;
			Object value = entry.getValue();
			if (!attrName.contains(key)) {
				Attribute attrib = Attribute.createInstance(key);
				AttributeValue av = new AttributeValue(mData, attrib, value);
				values[i++] = av;
				attrName.add(attrib);
			}
		}
		AttributeValue[] returnValues = new AttributeValue[i];
		System.arraycopy(values, 0, returnValues, 0, i);
		return returnValues;
	}

}
