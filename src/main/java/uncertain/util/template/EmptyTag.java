/*
 * Created on 2009-5-15
 */
package uncertain.util.template;

import uncertain.composite.CompositeMap;

public class EmptyTag implements ITagContent {

    public String getContent(CompositeMap context) {
        return "";
    }

}
