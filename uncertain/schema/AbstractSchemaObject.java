/*
 * Created on 2009-6-25
 */
package uncertain.schema;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uncertain.ocm.IConfigureListener;

/**
 * Base class for implementing a ISchemaObject
 * 1. Implements getParent()/setParent()
 * 2. Provides addChild() method and set child object's parent to this
 * 3. Maintains a HashSet to hold all childs, call doAssemble() for all childs before this.doAssemble()
 * 4. Provides getSchema() method to get the Schema instance that holds self directly or indirectly
 * 5. Defines validate() method for override, this method will be called right after being populated from configuration
 * AbstractSchemaObject
 * @author Zhou Fan
 *
 */
public abstract class AbstractSchemaObject implements  ISchemaObject, IConfigureListener {

    ISchemaObject       mParent;
    Set                 mChilds;

    public AbstractSchemaObject(){
        
    }

    public void setParent( ISchemaObject parent ){
        mParent = parent;
    }
    
    public ISchemaObject getParent(){
        return mParent;
    }
    
    public ISchemaManager getSchemaManager(){
        Schema schema = getSchema();
        return schema == null? null: schema.getSchemaManager();
    }
    
    public Schema getSchema(){
        for( ISchemaObject o = mParent; o !=null; o=o.getParent())
            if( o instanceof Schema )
                return (Schema)o;
        return null;
    }
    
    public void doAssemble(){
        if( mChilds != null )
            for( Iterator it = mChilds.iterator(); it.hasNext(); ){
                ISchemaObject child = (ISchemaObject)it.next();
                child.doAssemble();
            }
    }
    
    public void validate(){
        
    }
    
    public void endConfigure(){
        validate();
    }
    
    
    public void addChild( ISchemaObject child ){
        if( mChilds==null )
            mChilds = new HashSet();
        child.setParent(this);
        mChilds.add(child);
    }
    
    public void addChilds( ISchemaObject[] childs ){
        for(int i=0; i<childs.length; i++)
            addChild(childs[i]);        
    }
    
    public Set getChilds(){
        return mChilds;
    }
}
