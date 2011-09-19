/*
 * Created on 2008-12-1
 */
package uncertain.event;


public interface ISingleEventHandle extends IEventHandle {
    
    public int getHandleSequence();
    
    public String getEvent();

}
