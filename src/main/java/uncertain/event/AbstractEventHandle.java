/*
 * Created on 2008-12-1
 */
package uncertain.event;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import uncertain.proc.ProcedureRunner;

public abstract class AbstractEventHandle implements ISingleEventHandle {
    
    int         mSequence;    
    String      mEvent;   
    
    public AbstractEventHandle(){
        
    }

    /**
     * @param sequence sequence code
     * @param eventName name of event to handle
     */
    public AbstractEventHandle(int sequence, String eventName) {
        mSequence = sequence;
        mEvent = eventName;
    }
    
    public abstract int handleEvent(int sequence, CompositeMap context, Object[] parameters) throws Exception;
    
    public int handleEvent(int sequence, ProcedureRunner runner, Object[] parameters) throws Exception {
        return handleEvent( sequence, runner.getContext(), parameters );
    }
    
    /**
     * @return the mEvent
     */
    public String getEvent() {
        return mEvent;
    }

    /**
     * @param event the mEvent to set
     */
    public void setEvent(String event) {
        mEvent = event;
    }

    /**
     * @return the mSequence
     */
    public String getSequence() {
        return EventModel.getSequenceName(mSequence);
    }

    /**
     * @param sequence the mSequence to set
     */
    public void setSequence(String seq) {
        mSequence = EventModel.getSequence(seq);
    }
    
    public int getHandleSequence(){
        return mSequence;
    }

}
