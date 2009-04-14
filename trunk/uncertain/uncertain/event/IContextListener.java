/*
 * Implement this interface to manage context life cycle
 * The framework will call each listener when a new procedure context
 * is created or will be destroyed.
 * Created on 2009-4-7
 */
package uncertain.event;

public interface IContextListener {
    
    public void onContextCreate( RuntimeContext context );
    
    public void onContextDestroy( RuntimeContext context );

}
