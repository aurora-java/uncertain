/*
 * Created on 2010-12-16 обнГ02:07:40
 * $Id: CompositeMapTagCreator.java 2424 2011-04-20 09:06:07Z seacat.zhou@gmail.com $
 */
package uncertain.util.template;

public class CompositeMapTagCreator implements ITagCreator {
    
   public static final CompositeMapTagCreator DEFAULT_INSTANCE = new CompositeMapTagCreator();

    public ITagContent createInstance(String namespace, String tag) {        
        return new CompositeMapAccessTag(tag);
    }

}
