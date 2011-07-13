/*
 * Created on 2011-7-11 下午09:54:57
 * $Id$
 */
package uncertain.event;

public interface IEventDispatcher {
    
    public int fireEvent(String event_name, Object[] args) throws Exception;
    
    public void addParticipant(Object obj);


}
