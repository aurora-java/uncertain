/*
 * Created on 2010-12-16 обнГ02:07:40
 * $Id$
 */
package uncertain.util.template;

public class CompositeMapTagCreator implements ITagCreator {
    
   public static final CompositeMapTagCreator DEFAULT_INSTANCE = new CompositeMapTagCreator();

    public ITagContent createInstance(String namespace, String tag) {        
        return new CompositeMapAccessTag(tag);
    }

}
