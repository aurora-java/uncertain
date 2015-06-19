/*
 * Created on 2007-10-31
 */
package uncertain.composite;

/**
 * Get/Set object from CompositeMap by String path
 * ICompositeVisitor
 * @author Zhou Fan
 *
 */
public interface ICompositeAccessor {
    
    public void put( CompositeMap map, String path, Object value );
    
    public Object get( CompositeMap map, String path);

}
