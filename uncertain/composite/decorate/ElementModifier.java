/*
 * Created on 2008-6-2
 */
package uncertain.composite.decorate;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.DynamicObject;

/**
 * Base class for describing element locating info. in a CompositeMap hierarchy
 * ElementLocator
 * @author Zhou Fan
 *
 */
public class ElementModifier extends DynamicObject implements ICompositeDecorator  {
    
    public static final String KEY_ELEMENT_NAME = "elementname";

    public static final String KEY_KEYVALUE = "keyvalue";

    public static final String KEY_KEYFIELD = "keyfield";

    public static final String KEY_ROOT_PATH = "rootpath";
    
    public static final String KEY_PATH = "path";
    
    public static final String ELEMENT_APPEND = "element-append";
    
    public static final String ELEMENT_INSERT = "element-insert";
    
    public static final String ELEMENT_REMOVE = "element-remove";
    
    public static final String ELEMENT_MERGE = "element-merge";
    
    public static final String ELEMENT_REPLACE = "element-replace";
    
    public static final String ATTRIBUTE_MODIFY = "attribute-modify";
    
    
    static ElementModifier createInstance( String name ){
        CompositeMap map = new CompositeMap(name);
        ElementModifier e = new ElementModifier();
        e.initialize(map);
        return e;
    }
    
    public static ElementModifier createElementAppend(){
        return createInstance(ELEMENT_APPEND);
    }

    public static ElementModifier createElementInsert(){
        return createInstance(ELEMENT_INSERT);
    }
    
    public static ElementModifier createElementRemove(){
        return createInstance(ELEMENT_REMOVE);
    }
    
    public static ElementModifier createElementMerge(){
        return createInstance(ELEMENT_MERGE);
    }
    
    public static ElementModifier createElementReplace(){
        return createInstance(ELEMENT_REPLACE);
    }
    
    public static ElementModifier createAttributeModify(){
        return createInstance(ATTRIBUTE_MODIFY);
    }
    
    public String getPath(){
        return getString(KEY_PATH);
    }
    
    public void setPath( String path ){
        putString(KEY_PATH, path);
    }
    
    public String getRootPath(){
        return getString(KEY_ROOT_PATH);
    }
    
    public void setRootPath( String path ){
        putString(KEY_ROOT_PATH, path);
    }
    
    public String getKeyField(){
        return getString(KEY_KEYFIELD);
    }
    
    public void setKeyField( String key_field ){
        putString(KEY_KEYFIELD, key_field);
    }
    
    public Object getKeyValue(){
        return get(KEY_KEYVALUE);
    }
    
    public void setKeyValue( Object key_value ){
        put(KEY_KEYVALUE, key_value);
    }
    
    public String getElementName(){
        return getString(KEY_ELEMENT_NAME);
    }
    
    public void setElementName( String name ){
        putString(KEY_ELEMENT_NAME, name);
    }
    
    public void setType( String type ){
        getObjectContext().setName(type);
    }
    
    public CompositeMap getTargetElement( CompositeMap root ){
        String path = getPath();
        if(path!=null){
            return (CompositeMap)root.getObject(path);
        }else {
            path=getRootPath();
            String ename = getElementName();
            String keyf = getKeyField();
            Object keyv = getKeyValue();
            if( path!=null ){
                if(keyf !=null && keyv != null){
                    return CompositeUtil.findChild(root, ename, keyf, keyv==null?null:keyv.toString());
                }
                else
                    throw new IllegalArgumentException("Must set 'keyField' and 'keyValue' attribute if 'rootPath' is set");
            }else
                return root;
        }
    }
    
    public CompositeMap process( CompositeMap source ){
        String name = getObjectContext().getName();
        if( ELEMENT_APPEND.equalsIgnoreCase(name) )
            appendTo( source );
        else if( ELEMENT_INSERT.equalsIgnoreCase(name))
            insertInto( source );
        else if( ELEMENT_REMOVE.equalsIgnoreCase(name))
            removeFrom( source );
        else if( ELEMENT_MERGE.equalsIgnoreCase(name))
            mergeTo( source );
        else if( ELEMENT_REPLACE.equalsIgnoreCase(name))
            replace( source );
        else if( ATTRIBUTE_MODIFY.equalsIgnoreCase(name))
            setAttributes( source );
        return source;
    }
    
    public void setAttributes( CompositeMap source ){
        CompositeMap map = getTargetElement(source);
        if(map==null) return;
        Iterator it = getObjectContext().getChildIterator();
        while(it.hasNext()){
            CompositeMap item = (CompositeMap)it.next();
            AttributeModify am = (AttributeModify)DynamicObject.cast(item, AttributeModify.class);
            am.process(map);
        }
    }
    
    public void addAttributeModify( AttributeModify am ){
        getObjectContext().addChild( am.getObjectContext() );
    }
    
    public void removeFrom( CompositeMap source ){
        CompositeMap target = getTargetElement( source );
        //System.out.println(target.toXML());
        if( target!=null && target != source ){
            target.getParent().removeChild(target);
        }
    }
    
    public void insertInto( CompositeMap source ){
        CompositeMap target = getTargetElement( source );
        List childs = getObjectContext().getChilds();        
        if( target!=null  && childs !=null ){
            ListIterator it = childs.listIterator(childs.size());            
            while(it.hasPrevious()){
                CompositeMap item = (CompositeMap)it.previous();
                target.addChild(0, item);
            }
        }
    }
    
    public void appendTo( CompositeMap source ){
        CompositeMap target = getTargetElement( source );
        if( target!=null  ){
            target.addChilds(getObjectContext().getChilds());
        }            
    }
    
    static CompositeMap getSingleChild( CompositeMap m){
        List childs = m.getChilds();
        if(childs==null) return null;
        if(childs.size()<1) return null;
        return (CompositeMap)childs.get(0);
        
    }
    
    public void mergeTo ( CompositeMap source ){
        CompositeMap target = getTargetElement( source );
        if( target!=null  ){
            CompositeMap to_merge = getSingleChild( getObjectContext() );
            if(to_merge!=null){
                target.putAll(to_merge);
                target.addChilds(to_merge.getChilds());
            }
        }
    }
    
    public void replace( CompositeMap source ){
        CompositeMap target = getTargetElement( source );
        if( target!=null  ){
            CompositeMap parent = target.getParent();
            if(parent==null) return;
            CompositeMap to_merge = getSingleChild( getObjectContext() );
            if(to_merge!=null){
                parent.replaceChild(target, to_merge);
            }
        }     
    }

}
