/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

/**
 * Creates ITagContent instance under certain namespace 
 * @author Zhou Fan
 *
 */
public interface ITagCreator {
    
    public ITagContent createInstance( String namespace, String tag );

}
