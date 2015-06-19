/*
 * Created on 2009-7-23
 */
package uncertain.schema;

/**
 * General interface for all schema object
 * @author Zhou Fan
 *
 */
public interface ISchemaObject {

    /**
     * Called by parent to do initialize work when all necessary
     * parameter have been set
     */
    public void doAssemble();
    
    /**
     * Set parent of this schema object
     * @param parent
     */
    public void setParent( ISchemaObject parent );
    
    public ISchemaObject getParent();


}
