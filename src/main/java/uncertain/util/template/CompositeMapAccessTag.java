/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

import uncertain.composite.CompositeMap;

public class CompositeMapAccessTag implements ITagContent {
    
    /**
     * @param accessPath
     */
    public CompositeMapAccessTag(String accessPath) {
        super();
        mAccessPath = accessPath;
    }

    String      mAccessPath;

    public String getContent(CompositeMap context) {
        Object obj = context.getObject(mAccessPath);
        return obj==null?"":obj.toString();
    }

}
