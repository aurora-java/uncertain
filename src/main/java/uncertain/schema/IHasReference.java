/*
 * Created on 2009-8-25
 */
package uncertain.schema;

/**
 * schema object that has reference attribute, such as 'category'  
 * @author Zhou Fan
 *
 */
public interface IHasReference {
    
    /**
     * Called by SchemaManager to resolve reference to other schema
     * object
     * @param manager
     */
    public void resolveReference( ISchemaManager manager );    

}
