/*
 * Created on 2009-8-21
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

/**
 * Declares general methods that a referenced object should provide
 * @author Zhou Fan
 *
 */
public interface IReference extends IHasReference {
    
    /**
     * @return Whether this object is a reference to another  
     */
    public boolean isRef();
    
    /**
     * @return  Qualified name for referenced object
     */
    public QualifiedName getRefQName();
    
    /**
     * @return Referenced object instance
     */
    public ISchemaObject getRefObject();

}
