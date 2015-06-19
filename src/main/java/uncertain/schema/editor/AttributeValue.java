/*
 * Created on 2009-7-20
 */
package uncertain.schema.editor;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

/**
 * A pair of attribute value and its schema Attribute define
 */
public class AttributeValue {
    
    /**
     * @param container CompositeMap that contains the value to be edit
     * @param attribute Attribute config
     * @param value value for edit
     */
    public AttributeValue( CompositeMap container, Attribute attribute, Object value) {
        this.container = container;        
        this.attribute = attribute;
        this.value = value;
    }
    
    CompositeMap    container;
    Attribute       attribute;
    Object          value;
    
    
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Object getValue() {
        return value;
    }

    
    public void setValue(Object value) {
        this.value = value;
    }

    public CompositeMap getContainer() {
        return container;
    }

    public void setContainer(CompositeMap container) {
        this.container = container;
    }
    
    public String getValueString(){
        return value==null?"":value.toString();
    }

}
