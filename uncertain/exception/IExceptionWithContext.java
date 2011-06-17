/*
 * Created on 2011-6-17 下午02:59:30
 * $Id$
 */
package uncertain.exception;

import uncertain.composite.CompositeMap;

/** Interface for Exception that can provide context info. */
public interface IExceptionWithContext {
    
    public void setExceptionContext( CompositeMap context );
    
    public CompositeMap getExceptionContext();

}
