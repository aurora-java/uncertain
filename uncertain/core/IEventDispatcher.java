/*
 * Created on 2007-11-2
 */
package uncertain.core;

/**
 * An abstract interface to define two methods:
 * 1. To fire an event;
 * 2. To add an event listener
 * @author Zhou Fan
 *
 */
public interface IEventDispatcher {
    
    public void fireEvent( String event_name, Object[] args );
    
    public void addListener( Object listener );

}
