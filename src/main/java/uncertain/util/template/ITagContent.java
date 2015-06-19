/*
 * Created on 2007-8-5 ионГ02:31:45
 */
package uncertain.util.template;

import uncertain.composite.CompositeMap;

/**
 * Wrap around a String tag and can create dynamic content from CompositeMap
 * @author Zhou Fan
 *
 */
public interface ITagContent {
    
    public String getContent( CompositeMap context );

}
