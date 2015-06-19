/*
 * Created on 2008-6-3
 */
package uncertain.composite.decorate;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;

public class AttributeModify extends DynamicObject implements
        ICompositeDecorator {
    
    public static final String ATTRIBUTE_SET = "attribute-set";
    
    public static final String ATTRIBUTE_REMOVE = "attribute-remove";
    
    public static final String ATTRIBUTE_SET_BY_PATTERN = "attribute-set-by-pattern";
    
    public static final String KEY_NAME = "name";
    
    public static final String KEY_VALUE = "value";
    
    static AttributeModify createInstance( String name ){
        CompositeMap map = new CompositeMap(name);
        AttributeModify e = new AttributeModify();
        e.initialize(map);
        return e;
    }    
    
    public static AttributeModify createAttributeSet( String name ){
        return createInstance(ATTRIBUTE_SET);
    }
    
    public static AttributeModify createAttributeSet( String name, Object value ){
        AttributeModify m = createInstance(ATTRIBUTE_SET);
        m.setName(name);
        m.setValue(value);
        return m;
    }  
    
    public static AttributeModify createAttributeRemove( String name ){
        AttributeModify m = createInstance(ATTRIBUTE_REMOVE);
        m.setName(name);
        return m;
    }    
    
    public String getName(){
        return getString(KEY_NAME);
    }
    
    public void setName( String name ){
        putString(KEY_NAME, name);
    }
    
    public void setValue( Object obj ){
        put(KEY_VALUE, obj);
    }
    
    public Object getValue(){
        return get(KEY_VALUE);
    }
    
    public void setNameValue( String name, Object value ){
        setName(name);
        setValue(value);
    }
    
    public CompositeMap process(CompositeMap source) {
        String action = getObjectContext().getName();
        String name = getName();
        if(ATTRIBUTE_SET.equalsIgnoreCase(action)){
            if(name==null) throw new ConfigurationError("Must set 'name' property for <attribute-set> ");
            source.put(getName(), getValue());
        }else if(ATTRIBUTE_REMOVE.equalsIgnoreCase(action)){
            if(name==null) throw new ConfigurationError("Must set 'name' property for <attribute-remove> ");
            source.remove(name);
        }            
        return source;
    }

}
