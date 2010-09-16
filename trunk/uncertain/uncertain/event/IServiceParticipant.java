/*
 * Created on 2010-8-31 ����12:38:30
 * $Id$
 */
package uncertain.event;

/**
 * Implement this interface to be identified as global service participant,
 * and can listen to all service events 
 */
public interface IServiceParticipant {
    
    /** Get scope that this participant belongs to
     *  @see IParticipantManager#SERVICE_SCOPE
     *  @see IParticipantManager#APPLICATION_SCOPE
     *  @see IParticipantManager#GLOBAL_SCOPE
     */
    
    public String   getScope();
    
    

}
