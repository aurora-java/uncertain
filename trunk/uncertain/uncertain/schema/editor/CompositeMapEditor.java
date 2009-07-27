/*
 * Created on 2009-7-20
 */
package uncertain.schema.editor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.QualifiedName;

public class CompositeMapEditor {

    CompositeMap mData;
    Element mElement;
    ISchemaManager mSchemaManager;

    /**
     * @param data
     * @param schemaManager
     */
    public CompositeMapEditor(ISchemaManager schemaManager, CompositeMap data) {
        this.mData = data;
        this.mSchemaManager = schemaManager;
        mElement = mSchemaManager.getElement( new QualifiedName(mData.getNamespaceURI(), mData
                .getName()));
    }

    /**
     * Get all attributes for edit. If there exists schema for contained CompositeMap,
     * All attributes defined by schema with corresponding value will be returned; otherwise,
     * an array with all attributes in data will be returned. 
     * @return
     */
    public AttributeValue[] getAttributeList() {
        if (mElement != null) {
            List attribs = mElement.getAllAttributes();
            Collections.sort(attribs);
            
            AttributeValue[] values = new AttributeValue[attribs.size()];
            int i=0;
            for( Iterator it = attribs.iterator() ; it.hasNext(); ){
                Attribute attrib = (Attribute)it.next();
                values[i++] = new AttributeValue(mData, attrib, mData.get(attrib.getLocalName()));
            }
            return values;
        }else{
            AttributeValue[] values = new AttributeValue[mData.entrySet().size()];
            Iterator it = mData.entrySet().iterator();
            int i=0;
            while(it.hasNext()){
                Map.Entry entry = (Map.Entry)it.next();
                String key = entry.getKey()==null ? null: entry.getKey().toString();
                if(key==null) continue;
                Object value = entry.getValue();
                Attribute attrib = Attribute.createInstance(key);
                AttributeValue av = new AttributeValue( mData, attrib, value );
                values[i++] = av;                
            }
            return values;
        }
    }

}
