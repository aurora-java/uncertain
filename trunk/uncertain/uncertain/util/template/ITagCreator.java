/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

public interface ITagCreator {
    
    public ITagContent createInstance( String name_space, String tag );

}
