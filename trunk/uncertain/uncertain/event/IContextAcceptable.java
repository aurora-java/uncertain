/*
 * Created on 2011-8-9 上午10:38:31
 * $Id$
 */
package uncertain.event;

import uncertain.composite.CompositeMap;

/**
 * This interface is used when context map need to be passed
 * 
 */
public interface IContextAcceptable {
    
    public void setContext( CompositeMap context );

}
