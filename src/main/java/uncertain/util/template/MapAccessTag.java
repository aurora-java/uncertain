/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

import uncertain.composite.CompositeMap;

public class MapAccessTag implements ITagContent {
    
    /**
     * @param tag
     */
    public MapAccessTag(String tag) {
        super();
        mTag = tag;
    }
    
    String      mTag;

    public String getContent(CompositeMap context) {
        Object obj = context.get(mTag);
        return obj==null?"":obj.toString();
    }

}
