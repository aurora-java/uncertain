/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

public class DefaultTagCreator implements ITagCreator {
    
    static final DefaultTagCreator DEFAULT_INSTANCE = new DefaultTagCreator();
    
    public static DefaultTagCreator getInstance(){
        return DEFAULT_INSTANCE;
    }

    public ITagContent createInstance(String name_space, String tag) {
        return new MapAccessTag(tag);
    }

}
