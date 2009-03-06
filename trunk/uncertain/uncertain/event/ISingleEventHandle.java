/*
 * Created on 2008-12-1
 */
package uncertain.event;

import uncertain.proc.IEventHandle;

public interface ISingleEventHandle extends IEventHandle {
    
    public int getHandleSequence();
    
    public String getEvent();

}
