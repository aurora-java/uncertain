/*
 * Created on 2005-12-23
 */
package uncertain.event;

import uncertain.proc.ProcedureRunner;


/**
 * Implements this interface to monitor every event fired during procedure
 * @author Zhou Fan
 * 
 */
public interface IEventListener {
    
    /**
     * Called by ProcedureRunner when an event is fired
     * @param runner <code>ProcedureRunner</code> that event is fired from
     * @param sequence sequence of event handle @see uncertain.event.EventModel
     * @param event_name name of event
     */
    public int onEvent( ProcedureRunner runner, int sequence, String event_name);

}
