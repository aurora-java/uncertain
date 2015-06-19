/*
 * Created on 2011-6-16 下午11:27:45
 * $Id$
 */
package uncertain.exception;

/**
 *  Invoked by framework to save exception info.  
 */
public interface IExceptionListener {
    
    /** This method must guarantee no more exception would be thrown */ 
    public void onException( Throwable exception );

}
