/*
 * Created on 2011-7-1 上午11:13:12
 * $Id$
 */
package uncertain.core;

/**
 * Defines start & shutdown 
 */
public interface ILifeCycle {

    /**
     * @return true if this operation is success
     */
    public boolean startup();
    
    public void shutdown();

}
