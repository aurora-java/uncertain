/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

/**
 * A registry to get ITagCreator from namespace, and can have parent registry.
 * @author Zhou Fan
 *
 */
public interface ITagCreatorRegistry {
    
    public ITagCreator getTagCreator( String name_space );
    
    public void setParent( ITagCreatorRegistry parent );    
    
    public void registerTagCreator( String name_space, ITagCreator creator );
    
}
